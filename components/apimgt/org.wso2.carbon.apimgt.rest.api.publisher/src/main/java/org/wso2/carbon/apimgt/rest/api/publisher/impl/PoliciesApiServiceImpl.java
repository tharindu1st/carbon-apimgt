package org.wso2.carbon.apimgt.rest.api.publisher.impl;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.apimgt.core.exception.APIManagementException;
import org.wso2.carbon.apimgt.core.models.policy.Policy;
import org.wso2.carbon.apimgt.core.util.ETagUtils;
import org.wso2.carbon.apimgt.rest.api.common.util.RestApiUtil;
import org.wso2.carbon.apimgt.rest.api.publisher.NotFoundException;
import org.wso2.carbon.apimgt.rest.api.publisher.PoliciesApiService;
import org.wso2.carbon.apimgt.rest.api.publisher.utils.RestAPIPublisherUtil;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.util.List;

@javax.annotation.Generated(value = "class org.wso2.maven.plugins.JavaMSF4JServerCodegen", date = "2017-01-03T20:31:12.997+05:30")
public class PoliciesApiServiceImpl extends PoliciesApiService {

    private static final Logger log = LoggerFactory.getLogger(PoliciesApiService.class);

    /**
     * Retrieve tiers that corresponding to the level specified
     * 
     * @param tierLevel Tier Level
     * @param limit Maximum tiers to return in a single response
     * @param offset Starting position of the pagination
     * @param accept Accept header value
     * @param ifNoneMatch If-None-Match header value
     * @param minorVersion Minor version header value
     * @return A list of tiers qualifying
     * @throws NotFoundException When the particular resource does not exist in the system
     */
    @Override
    public Response policiesTierLevelGet(String tierLevel, Integer limit, Integer offset, String accept,
                                         String ifNoneMatch, String minorVersion) throws NotFoundException {
        String username = RestApiUtil.getLoggedInUsername();

        log.info("Received Policy GET request for tierLevel " + tierLevel);

        try {
            List<Policy> policies = RestAPIPublisherUtil.getApiPublisher(username).getAllPoliciesByLevel(tierLevel);
            return Response.ok().entity(policies).build();
        } catch (APIManagementException e) {
            String msg = "Error occurred while retrieving Policies";
            RestApiUtil.handleInternalServerError(msg, e, log);
            org.wso2.carbon.apimgt.rest.api.common.dto.ErrorDTO errorDTO = RestApiUtil.getErrorDTO(e.getErrorHandler());
            log.error(msg, e);
            return Response.status(e.getErrorHandler().getHttpStatusCode()).entity(errorDTO).build();
        }

    }

    /**
     * Retrieves a single tier
     * 
     * @param tierName Name of the tier
     * @param tierLevel Tier Level
     * @param accept Accept header value
     * @param ifNoneMatch If-None-Match header value
     * @param ifModifiedSince If-Modified-Since value
     * @param minorVersion Minor version header value
     * @return Requested tier as the response
     * @throws NotFoundException When the particular resource does not exist in the system
     */
    @Override
    public Response policiesTierLevelTierNameGet(String tierName, String tierLevel, String accept, String ifNoneMatch,
                                                 String ifModifiedSince, String minorVersion) throws NotFoundException {
        String username = RestApiUtil.getLoggedInUsername();
        String existingFingerprint = policiesTierLevelTierNameGetFingerprint(tierName, tierLevel, accept,
                ifNoneMatch, ifModifiedSince, minorVersion);
        if (!StringUtils.isEmpty(ifNoneMatch) && !StringUtils.isEmpty(existingFingerprint) && ifNoneMatch
                .contains(existingFingerprint)) {
            return Response.notModified().build();
        }

        try {
            Policy policy = RestAPIPublisherUtil.getApiPublisher(username).getPolicyByName(tierLevel, tierName);
            return Response.ok().header(HttpHeaders.ETAG, "\"" + existingFingerprint + "\"").entity(policy).build();
        } catch (APIManagementException e) {
            String msg = "Error occurred while retrieving Policy";
            RestApiUtil.handleInternalServerError(msg, e, log);
            org.wso2.carbon.apimgt.rest.api.common.dto.ErrorDTO errorDTO = RestApiUtil.getErrorDTO(e.getErrorHandler());
            log.error(msg, e);
            return Response.status(e.getErrorHandler().getHttpStatusCode()).entity(errorDTO).build();
        }
    }

    /**
     * Retrieves the fingerprint of an existing tier
     * 
     * @param tierName Name of the tier
     * @param tierLevel Tier Level
     * @param accept Accept header value
     * @param ifNoneMatch If-None-Match header value
     * @param ifModifiedSince If-Modified-Since value
     * @param minorVersion Minor version header value
     * @return fingerprint of an existing tier
     */
    public String policiesTierLevelTierNameGetFingerprint(String tierName, String tierLevel, String accept,
            String ifNoneMatch, String ifModifiedSince, String minorVersion) {
        String username = RestApiUtil.getLoggedInUsername();
        try {
            String lastUpdatedTime = RestAPIPublisherUtil.getApiPublisher(username)
                    .getLastUpdatedTimeOfThrottlingPolicy(tierLevel, tierName);
            return ETagUtils.generateETag(lastUpdatedTime);
        } catch (APIManagementException e) {
            //gives a warning and let it continue the execution
            String errorMessage = "Error while retrieving last updated time of policy :" + tierLevel + "/" + tierName;
            log.error(errorMessage, e);
            return null;
        }
    }
}
