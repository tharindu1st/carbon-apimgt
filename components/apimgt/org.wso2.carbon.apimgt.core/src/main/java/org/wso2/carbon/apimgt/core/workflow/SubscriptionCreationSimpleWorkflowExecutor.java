/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 * 
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.apimgt.core.workflow;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.core.api.WorkflowExecutor;
import org.wso2.carbon.apimgt.core.api.WorkflowResponse;
import org.wso2.carbon.apimgt.core.exception.WorkflowException;
import org.wso2.carbon.apimgt.core.models.Workflow;
import org.wso2.carbon.apimgt.core.models.WorkflowStatus;

/**
 * This is a simple work flow extension to have Subscription creation process
 */
public class SubscriptionCreationSimpleWorkflowExecutor implements WorkflowExecutor {
    private static final Log log = LogFactory.getLog(SubscriptionCreationSimpleWorkflowExecutor.class);

    /**
     * Execute the workflow executor
     *
     * @param workFlow
     * @throws WorkflowException
     */

    public WorkflowResponse execute(Workflow workFlow) throws WorkflowException {
        if (log.isDebugEnabled()) {
            log.debug("Executing Subscription creation Workflow..");
        }
    
        WorkflowResponse workflowResponse = new GeneralWorkflowResponse();
        //set the state to approved
        workflowResponse.setWorkflowStatus(WorkflowStatus.APPROVED);
        return workflowResponse;
    }

    /**
     * Complete the external process status
     * Based on the workflow status we will update the status column of the
     * Application table
     *
     * @param workFlow - Workflow
     */
    public WorkflowResponse complete(Workflow workFlow) throws WorkflowException {
        if (log.isDebugEnabled()) {
            log.debug("Complete  Subscription creation Workflow..");
        }

        WorkflowResponse workflowResponse = new GeneralWorkflowResponse();
        workflowResponse.setWorkflowStatus(workFlow.getStatus());
        return workflowResponse;
    }

    @Override
    public void cleanUpPendingTask(String workflowExtRef) throws WorkflowException {
        
    }
}
