package org.wso2.carbon.apimgt.grpc.service;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.core.ServerShutdownHandler;
import org.wso2.carbon.core.ServerStartupObserver;

import java.io.IOException;

public class GrpcServerStatupObserver implements ServerStartupObserver, ServerShutdownHandler {

    private static final Log log = LogFactory.getLog(GrpcServerStatupObserver.class);
    private Server server;
    private boolean isConnected;

    @Override
    public void completingServerStartup() {

    }

    @Override
    public void completedServerStartup() {

        server =
                ServerBuilder.forPort(9553).addService(new EventServiceImpl()).build();
        try {
            server.start();
            isConnected = true;
        } catch (IOException e) {
            log.error("Error while starting GRPC server", e);
        }
    }

    @Override
    public void invoke() {

        if (isConnected) {
            if (server != null) {
                this.server.shutdownNow();
                isConnected = false;
            }
        }
    }
}
