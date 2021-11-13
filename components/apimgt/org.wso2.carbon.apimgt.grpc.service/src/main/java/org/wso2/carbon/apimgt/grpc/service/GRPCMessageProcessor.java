package org.wso2.carbon.apimgt.grpc.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.grpc.service.internal.GRPCEventAdapterHolder;
import org.wso2.carbon.base.MultitenantConstants;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.event.input.adapter.core.InputEventAdapterListener;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.concurrent.ConcurrentHashMap;

public class GRPCMessageProcessor implements Runnable {

    private byte[] payload;
    private String tenantDomain;
    private Log log = LogFactory.getLog(GRPCMessageProcessor.class);

    public GRPCMessageProcessor(byte[] payload, String tenantDomain) {

        this.payload = payload;
        this.tenantDomain = tenantDomain;
    }

    @Override
    public void run() {

        boolean tenantFlowStarted = false;
        try {
            PrivilegedCarbonContext.startTenantFlow();
            PrivilegedCarbonContext.getThreadLocalCarbonContext()
                    .setTenantDomain(tenantDomain, true);
            tenantFlowStarted = true;
            if (log.isDebugEnabled()) {
                log.debug("Event received in HTTP Event Adapter - " + payload);
            }

            if (payload != null) {
                org.wso2.carbon.databridge.commons.Event event = toEvent(payload);
                if (event != null) {
                    ConcurrentHashMap<String, GRPCEventAdapter> adapters =
                            GRPCEventAdapterHolder.getAdapterService(tenantDomain, event.getStreamId());
                    if (adapters != null) {
                        for (GRPCEventAdapter adapter : adapters.values()) {
                            adapter.getEventAdaptorListener().onEvent(event);
                        }
                        if (log.isDebugEnabled()) {
                            log.debug("Event received in grpcEvent Adapter - " + event);
                        }
                    }

                }
            } else {
                log.warn("Dropping the empty/null event received through grpc adapter");
            }
        } catch (Exception e) {
            log.error("Error while parsing http request for processing: " + e.getMessage(), e);
        } finally {
            if (tenantFlowStarted) {
                PrivilegedCarbonContext.endTenantFlow();
            }
        }
    }

    private org.wso2.carbon.databridge.commons.Event toEvent(byte[] bytes) {

        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes)) {
            return (org.wso2.carbon.databridge.commons.Event) new ObjectInputStream(bis).readObject();
        } catch (IOException | ClassNotFoundException e) {
            log.error("Error while Deserialize byte array.", e);
        }
        return null;
    }

}
