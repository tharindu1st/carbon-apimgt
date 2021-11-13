package org.wso2.carbon.apimgt.grpc.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.databridge.commons.Event;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class GRPCPublisher {
    private static final Log log  = LogFactory.getLog(GRPCPublisher.class);
    BlockingQueue<Event> events = new ArrayBlockingQueue<>(30000);
    private Set<GrpcWorker> grpcPublisher = new HashSet<>();

    public GRPCPublisher(List<String> urls) throws GrpcPublisherException {

        for (String url : urls) {
            GrpcWorker grpcWorker;
            try {
                grpcWorker = new GrpcWorker(url);
            } catch (GrpcPublisherException e) {
                throw e;
            }
            grpcPublisher.add(grpcWorker);
        }
        new Thread(() -> {
            while (true) {
                List<Event> eventList = new ArrayList<>();
                events.drainTo(eventList, 50);
                if (eventList.size()>0){
                    grpcPublisher.forEach(grpcWorker -> grpcWorker.push(eventList));
                }
            }
        }).start();
    }

    public void publish(Event event) throws GrpcPublisherException {
        if (events.remainingCapacity()<0){
            throw new GrpcPublisherException("EventQueue is full");
        }
        events.add(event);
    }
}
