package org.wso2.carbon.apimgt.grpc.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.databridge.commons.Event;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class GrpcWorker {

    private static final Log log = LogFactory.getLog(GrpcWorker.class);
    private final GRPCClientPool grpcClientPool;
    private final BlockingQueue<List<Event>> blockingQueue = new ArrayBlockingQueue<>(10000);
    private final ScheduledExecutorService scheduledExecutorService;
    public GrpcWorker(String url) throws GrpcPublisherException {

        grpcClientPool = new GRPCClientPool(url);
        scheduledExecutorService = new ScheduledThreadPoolExecutor(100);
        scheduledExecutorService.scheduleAtFixedRate(() -> {

            List<Event> events = blockingQueue.poll();
            if (events != null){
                GRPCClient grpcClient = null;
                try {
                    grpcClient = grpcClientPool.get();
                    if (grpcClient != null){
                        for (Event event : events) {
                            grpcClient.publish(event);
                        }
                    }
                } catch (Exception e) {
                    log.error("Error while creating clients from pool",e);
                }finally {
                    if (grpcClient != null){
                        try {
                            grpcClientPool.release(grpcClient);
                        } catch (Exception e) {
                            log.error("Error while releasing clients to pool",e);
                        }
                    }
                }
            }
        },10,1, TimeUnit.MILLISECONDS);
    }

    public void push(List<Event> events) {

        log.info(blockingQueue.remainingCapacity());
        blockingQueue.add(events);
    }

    public void close() {

        if (scheduledExecutorService != null && !scheduledExecutorService.isTerminated()) {
            scheduledExecutorService.shutdown();
        }
        grpcClientPool.cleanup();
    }
}
