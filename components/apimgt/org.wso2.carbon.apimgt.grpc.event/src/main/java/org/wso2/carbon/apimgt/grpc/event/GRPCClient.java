package org.wso2.carbon.apimgt.grpc.event;

import com.google.protobuf.ByteString;
import com.google.protobuf.Empty;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import static org.wso2.carbon.apimgt.grpc.event.EventServiceGrpc.newStub;

public class GRPCClient {

    private static final Log log = LogFactory.getLog(GRPCClient.class);
    private ManagedChannel channel;
    private EventServiceGrpc.EventServiceStub eventServiceStub;
    private StreamObserver<Event> requestObserver;

    public GRPCClient(String host, int port) {
        channel =
                ManagedChannelBuilder.forAddress(host,port)
                        .usePlaintext().enableRetry().keepAliveWithoutCalls(true).build();
        eventServiceStub = newStub(channel);
        StreamObserver<Empty> responseObserver = new StreamObserver<Empty>() {

            @Override
            public void onNext(Empty empty) {

            }

            @Override
            public void onError(Throwable throwable) {

                log.error("Error while publishing", throwable);
            }

            @Override
            public void onCompleted() {

                log.info("Completing grpc messages");
            }
        };
        requestObserver = eventServiceStub.push(responseObserver);

    }

    public void publish(org.wso2.carbon.databridge.commons.Event event) {

        byte[] data = SerializationUtils.serialize(event);
        requestObserver.onNext(org.wso2.carbon.apimgt.grpc.event.Event.newBuilder().setPayload(ByteString.copyFrom(data)).build());
    }
}
