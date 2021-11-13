package org.wso2.carbon.apimgt.gateway.throttling.publisher;

import org.wso2.carbon.databridge.agent.DataPublisher;
import org.wso2.carbon.databridge.commons.Event;

public class BinaryDataPublisher implements Publisher {

    private DataPublisher dataPublisher;

    public BinaryDataPublisher(DataPublisher dataPublisher) {

        this.dataPublisher = dataPublisher;
    }

    @Override
    public void publish(Event event) {

        dataPublisher.tryPublish(event);
    }
}
