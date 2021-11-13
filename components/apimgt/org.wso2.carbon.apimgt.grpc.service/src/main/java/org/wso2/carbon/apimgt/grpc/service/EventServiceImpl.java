package org.wso2.carbon.apimgt.grpc.service;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.grpc.event.Event;
import org.wso2.carbon.apimgt.grpc.event.EventServiceGrpc;
import org.wso2.carbon.base.MultitenantConstants;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class EventServiceImpl extends EventServiceGrpc.EventServiceImplBase {

    private ExecutorService executorService;
    Log log = LogFactory.getLog(EventServiceImpl.class);

    public EventServiceImpl() {
        executorService = new ThreadPoolExecutor(10, 100, 10, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(10000), new ThreadFactoryBuilder().
                setNameFormat("Thread pool- component - GRPCEventAdapter.executorService").build());
    }

    @Override
    public StreamObserver<Event> push(StreamObserver<Empty> responseObserver) {

        return new StreamObserver<Event>() {

            @Override
            public void onNext(Event event) {

                byte[] bytes = event.getPayload().toByteArray();
                executorService.submit(new GRPCMessageProcessor(bytes, MultitenantConstants.SUPER_TENANT_DOMAIN_NAME));
            }

            @Override
            public void onError(Throwable throwable) {
                throwable.getMessage()
                log.error(throwable);

            }

            @Override
            public void onCompleted() {
            }
        };
    }

}
