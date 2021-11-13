package org.wso2.carbon.apimgt.grpc.service;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.grpc.Server;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.grpc.service.internal.GRPCEventAdapterHolder;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.event.input.adapter.core.InputEventAdapter;
import org.wso2.carbon.event.input.adapter.core.InputEventAdapterConfiguration;
import org.wso2.carbon.event.input.adapter.core.InputEventAdapterListener;
import org.wso2.carbon.event.input.adapter.core.exception.ConnectionUnavailableException;
import org.wso2.carbon.event.input.adapter.core.exception.InputEventAdapterException;
import org.wso2.carbon.event.input.adapter.core.exception.InputEventAdapterRuntimeException;
import org.wso2.carbon.event.input.adapter.core.exception.TestConnectionNotSupportedException;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class GRPCEventAdapter implements InputEventAdapter {

    private static final Log log = LogFactory.getLog(GRPCEventAdapter.class);
    private final InputEventAdapterConfiguration eventAdapterConfiguration;
    private InputEventAdapterListener inputEventAdapterListener;
    public GRPCEventAdapter(InputEventAdapterConfiguration eventAdapterConfiguration,
                            Map<String, String> globalProperties) {

        this.eventAdapterConfiguration = eventAdapterConfiguration;
    }

    @Override
    public void init(InputEventAdapterListener inputEventAdapterListener) throws InputEventAdapterException {

        this.inputEventAdapterListener = inputEventAdapterListener;

    }

    @Override
    public void testConnect() throws TestConnectionNotSupportedException, InputEventAdapterRuntimeException,
            ConnectionUnavailableException {

        throw new TestConnectionNotSupportedException("not-supported");

    }

    @Override
    public void connect() throws InputEventAdapterRuntimeException, ConnectionUnavailableException {
        String streamId = eventAdapterConfiguration.getInputStreamIdOfWso2eventMessageFormat();
        String tenantDomain = PrivilegedCarbonContext.getThreadLocalCarbonContext().getTenantDomain(true);
        GRPCEventAdapterHolder.registerAdapterService(tenantDomain,streamId, this);

    }

    @Override
    public void disconnect() {
        String streamId = eventAdapterConfiguration.getInputStreamIdOfWso2eventMessageFormat();
        String tenantDomain = PrivilegedCarbonContext.getThreadLocalCarbonContext().getTenantDomain(true);
        GRPCEventAdapterHolder.unregisterAdapterService(tenantDomain, streamId, this);

    }

    @Override
    public void destroy() {

    }

    @Override
    public boolean isEventDuplicatedInCluster() {

        return false;
    }

    @Override
    public boolean isPolling() {

        return false;
    }

    public String getEventAdapterName() {
        return eventAdapterConfiguration.getName();
    }
    public InputEventAdapterListener getEventAdaptorListener() {
        return inputEventAdapterListener;
    }
}
