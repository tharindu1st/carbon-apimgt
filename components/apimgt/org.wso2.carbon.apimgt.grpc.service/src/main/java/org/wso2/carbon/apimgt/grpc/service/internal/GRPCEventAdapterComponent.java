package org.wso2.carbon.apimgt.grpc.service.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.wso2.carbon.apimgt.grpc.service.GRPCEventAdapterFactory;
import org.wso2.carbon.apimgt.grpc.service.GrpcServerStatupObserver;
import org.wso2.carbon.core.ServerShutdownHandler;
import org.wso2.carbon.core.ServerStartupObserver;
import org.wso2.carbon.event.input.adapter.core.InputEventAdapterFactory;

@Component(
        name = "org.wso2.carbon.apimgt.grpc.component",
        immediate = true)
public class GRPCEventAdapterComponent {

    private static final Log log = LogFactory.getLog(GRPCEventAdapterComponent.class);

    /**
     * initialize the agent service here service here.
     *
     * @param context
     */
    @Activate
    protected void activate(ComponentContext context) {

        try {
            InputEventAdapterFactory grpcEventAdapterFactory = new GRPCEventAdapterFactory();
            context.getBundleContext().registerService(InputEventAdapterFactory.class.getName(),
                    grpcEventAdapterFactory, null);
            GrpcServerStatupObserver grpcServerStatupObserver = new GrpcServerStatupObserver();
            context.getBundleContext().registerService(ServerStartupObserver.class, grpcServerStatupObserver, null);
            context.getBundleContext().registerService(ServerShutdownHandler.class, grpcServerStatupObserver, null);
            if (log.isDebugEnabled()) {
                log.debug("Successfully deployed the input HTTP adapter service");
            }
        } catch (RuntimeException e) {
            log.error("Can not create the input HTTP adapter service ", e);
        }
    }

}
