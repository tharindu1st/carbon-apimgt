package org.wso2.carbon.apimgt.gateway.throttling.publisher;

import org.wso2.carbon.apimgt.api.APIManagementException;
import org.wso2.carbon.databridge.commons.Event;

public interface Publisher {

    public void publish(Event event) throws APIManagementException;
}
