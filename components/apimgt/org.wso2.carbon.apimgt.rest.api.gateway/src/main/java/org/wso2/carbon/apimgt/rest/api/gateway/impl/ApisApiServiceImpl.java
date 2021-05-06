package org.wso2.carbon.apimgt.rest.api.gateway.impl;

import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.jaxrs.ext.MessageContext;
import org.wso2.carbon.apimgt.api.model.subscription.URLMapping;
import org.wso2.carbon.apimgt.keymgt.SubscriptionDataHolder;
import org.wso2.carbon.apimgt.keymgt.model.SubscriptionDataStore;
import org.wso2.carbon.apimgt.keymgt.model.entity.API;
import org.wso2.carbon.apimgt.rest.api.common.RestApiCommonUtil;
import org.wso2.carbon.apimgt.rest.api.gateway.ApisApiService;
import org.wso2.carbon.apimgt.rest.api.gateway.dto.APIDTO;
import org.wso2.carbon.apimgt.rest.api.gateway.dto.APIListDTO;
import org.wso2.carbon.apimgt.rest.api.gateway.dto.URLMappingDTO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.ws.rs.core.Response;

/**
 * This class used to retrieve in-memory API Metadata holds in gateway.
 */
public class ApisApiServiceImpl implements ApisApiService {

    public Response getAPIS(String apiName, String version, String context, String tenantDomain,
                            MessageContext messageContext) {

        tenantDomain = RestApiCommonUtil.getValidateTenantDomain(tenantDomain);
        SubscriptionDataStore subscriptionDataStore =
                SubscriptionDataHolder.getInstance().getTenantSubscriptionStore(tenantDomain);
        if (subscriptionDataStore == null) {
            return Response.ok(new APIListDTO()).build();
        }
        if (StringUtils.isEmpty(apiName) || StringUtils.isEmpty(version)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        API api = null;
        if (StringUtils.isNotEmpty(context) && StringUtils.isNotEmpty(version)) {
            api = subscriptionDataStore.getApiByContextAndVersion(context, version);
        } else if (StringUtils.isNotEmpty(apiName) && StringUtils.isNotEmpty(version)) {
            api = subscriptionDataStore.getApiByNameAndVersion(apiName, version);
        }
        if (api != null) {
            APIListDTO apiListDTO = new APIListDTO();
            apiListDTO.setList(Arrays.asList(toAPIDto(api)));
            apiListDTO.setCount(1);
            return Response.ok(apiListDTO).build();
        } else {
            return Response.ok(new APIListDTO()).build();
        }
    }

    private APIDTO toAPIDto(API api) {

        APIDTO apidto =
                new APIDTO().apiId(api.getApiId())
                        .apiType(api.getApiType())
                        .context(api.getContext())
                        .isDefaultVersion(api.isDefaultVersion())
                        .name(api.getApiName())
                        .policy(api.getApiTier())
                        .status(api.getStatus())
                        .provider(api.getApiProvider())
                        .uuid(api.getUuid())
                        .urlMappings(toURlMappings(api.getResources()));
        return apidto;
    }

    private List<URLMappingDTO> toURlMappings(List<URLMapping> resources) {

        List<URLMappingDTO> urlMappingDTOList = new ArrayList<>();
        if (resources != null) {
            for (URLMapping resource : resources) {
                URLMappingDTO urlMappingDTO = new URLMappingDTO()
                        .authScheme(resource.getAuthScheme())
                        .urlPattern(resource.getUrlPattern())
                        .httpMethod(resource.getHttpMethod())
                        .scopes(resource.getScopes())
                        .throttlingPolicy(resource.getThrottlingPolicy());
                urlMappingDTOList.add(urlMappingDTO);
            }
        }
        return urlMappingDTOList;
    }
}
