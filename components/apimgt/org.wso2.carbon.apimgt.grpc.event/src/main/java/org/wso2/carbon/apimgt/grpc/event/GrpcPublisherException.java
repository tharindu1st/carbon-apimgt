package org.wso2.carbon.apimgt.grpc.event;

import java.net.MalformedURLException;

public class GrpcPublisherException extends Throwable {

    public GrpcPublisherException(String message) {

        super(message);

    }

    public GrpcPublisherException(String message, Throwable e) {

        super(message, e);

    }
}
