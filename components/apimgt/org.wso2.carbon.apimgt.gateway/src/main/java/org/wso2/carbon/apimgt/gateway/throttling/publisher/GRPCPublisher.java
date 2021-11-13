package org.wso2.carbon.apimgt.gateway.throttling.publisher;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.api.APIManagementException;
import org.wso2.carbon.apimgt.grpc.event.GrpcPublisherException;
import org.wso2.carbon.databridge.commons.Event;

import java.util.Arrays;

public class GRPCPublisher implements Publisher {

    private static final Log log = LogFactory.getLog(GRPCPublisher.class);
    private org.wso2.carbon.apimgt.grpc.event.GRPCPublisher grpcPublisher;

    public GRPCPublisher(String host, int port) throws APIManagementException {

        try {
            grpcPublisher =
                    new org.wso2.carbon.apimgt.grpc.event.GRPCPublisher(Arrays.asList(host.concat(":").concat(String.valueOf(port))));
        } catch (GrpcPublisherException e) {
            throw new APIManagementException("",e);
        }
    }

    @Override
    public void publish(Event event) throws APIManagementException {

        try {
            grpcPublisher.publish(event);
        } catch (GrpcPublisherException e) {
            throw new APIManagementException("Error while publishing event",e);
        }
    }
}
