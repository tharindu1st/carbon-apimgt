package org.wso2.carbon.apimgt.grpc.service;

import org.wso2.carbon.event.input.adapter.core.InputEventAdapter;
import org.wso2.carbon.event.input.adapter.core.InputEventAdapterConfiguration;
import org.wso2.carbon.event.input.adapter.core.InputEventAdapterFactory;
import org.wso2.carbon.event.input.adapter.core.MessageType;
import org.wso2.carbon.event.input.adapter.core.Property;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GRPCEventAdapterFactory extends InputEventAdapterFactory {

    @Override
    public String getType() {

        return "grpc";
    }

    @Override
    public List<String> getSupportedMessageFormats() {

        List<String> supportInputMessageTypes = new ArrayList<String>();
        supportInputMessageTypes.add(MessageType.WSO2EVENT);
        return supportInputMessageTypes;
    }

    @Override
    public List<Property> getPropertyList() {

        return null;
    }

    @Override
    public String getUsageTips() {

        return null;
    }

    @Override
    public InputEventAdapter createEventAdapter(InputEventAdapterConfiguration inputEventAdapterConfiguration,
                                                Map<String, String> map) {

        return new GRPCEventAdapter(inputEventAdapterConfiguration, map);
    }
}
