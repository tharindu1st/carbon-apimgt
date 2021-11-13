package org.wso2.carbon.apimgt.grpc.service.internal;

import org.wso2.carbon.apimgt.grpc.service.GRPCEventAdapter;

import java.util.concurrent.ConcurrentHashMap;

public class GRPCEventAdapterHolder {

    private static ConcurrentHashMap<String, ConcurrentHashMap<String, ConcurrentHashMap<String, GRPCEventAdapter>>> inputEventAdapterListenerMap = new ConcurrentHashMap<>();

    public static synchronized void registerAdapterService(String tenantDomain, String streamId,
                                                           GRPCEventAdapter grpcEventAdapter) {

        ConcurrentHashMap<String, ConcurrentHashMap<String, GRPCEventAdapter>> tenantSpecificInputEventAdapterListenerMap = inputEventAdapterListenerMap.get(tenantDomain);

        if (tenantSpecificInputEventAdapterListenerMap == null) {
            tenantSpecificInputEventAdapterListenerMap = new ConcurrentHashMap<String, ConcurrentHashMap<String,
                    GRPCEventAdapter>>();
            inputEventAdapterListenerMap.put(tenantDomain, tenantSpecificInputEventAdapterListenerMap);
        }
        ConcurrentHashMap<String, GRPCEventAdapter> streamSpecificInputEventAdapterListenerMap =
                tenantSpecificInputEventAdapterListenerMap.get(streamId);
        if (streamSpecificInputEventAdapterListenerMap == null) {
            streamSpecificInputEventAdapterListenerMap = new ConcurrentHashMap<String, GRPCEventAdapter>();
            tenantSpecificInputEventAdapterListenerMap.put(streamId, streamSpecificInputEventAdapterListenerMap);
        }
        streamSpecificInputEventAdapterListenerMap.put(grpcEventAdapter.getEventAdapterName(), grpcEventAdapter);

    }

    public static void unregisterAdapterService(String tenantDomain, String streamId,
                                                GRPCEventAdapter wso2EventAdapter) {

        ConcurrentHashMap<String, ConcurrentHashMap<String, GRPCEventAdapter>> tenantSpecificInputEventAdapterListenerMap = inputEventAdapterListenerMap.get(tenantDomain);

        if (tenantSpecificInputEventAdapterListenerMap != null) {
            ConcurrentHashMap<String, GRPCEventAdapter> streamSpecificInputEventAdapterListenerMap =
                    tenantSpecificInputEventAdapterListenerMap.get(streamId);
            if (streamSpecificInputEventAdapterListenerMap != null) {
                streamSpecificInputEventAdapterListenerMap.remove(wso2EventAdapter.getEventAdapterName());
            }
        }
    }

    public static ConcurrentHashMap<String, GRPCEventAdapter> getAdapterService(String tenantDomain, String streamId) {

        ConcurrentHashMap<String, ConcurrentHashMap<String, GRPCEventAdapter>> tenantSpecificInputEventAdapterListenerMap = inputEventAdapterListenerMap.get(tenantDomain);
        if (tenantSpecificInputEventAdapterListenerMap != null) {
            return tenantSpecificInputEventAdapterListenerMap.get(streamId);
        }
        return null;
    }
}
