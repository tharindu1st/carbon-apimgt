package org.wso2.carbon.apimgt.rest.api.gateway;

import org.wso2.carbon.apimgt.rest.api.gateway.dto.ErrorDTO;
import org.wso2.carbon.apimgt.rest.api.gateway.dto.SubscriptionListDTO;
import org.wso2.carbon.apimgt.rest.api.gateway.ApisApiService;
import org.wso2.carbon.apimgt.rest.api.gateway.impl.ApisApiServiceImpl;
import org.wso2.carbon.apimgt.api.APIManagementException;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.inject.Inject;

import io.swagger.annotations.*;
import java.io.InputStream;

import org.apache.cxf.jaxrs.ext.MessageContext;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;

import java.util.Map;
import java.util.List;
import javax.validation.constraints.*;
@Path("/apis")

@Api(description = "the apis API")
@Consumes({ "application/json" })
@Produces({ "application/json" })


public class ApisApi  {

  @Context MessageContext securityContext;

ApisApiService delegate = new ApisApiServiceImpl();


    @GET
    
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @ApiOperation(value = "Get API Metadata stored in in-memory", notes = "This operation is used to get the API Metadata from the storage. If the Tenant domain is not provided carbon.super will be picked as the Tenant domain. ", response = SubscriptionListDTO.class, tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "OK. API Metadata successfully Retrieved From the Storage. ", response = SubscriptionListDTO.class),
        @ApiResponse(code = 400, message = "Bad Request. Invalid request or validation error ", response = ErrorDTO.class),
        @ApiResponse(code = 404, message = "Not Found. Requested API does not exist. ", response = ErrorDTO.class) })
    public Response getAPIS( @NotNull @ApiParam(value = "Name of the API ",required=true)  @QueryParam("apiName") String apiName,  @NotNull @ApiParam(value = "version of the API ",required=true)  @QueryParam("version") String version,  @ApiParam(value = "Context of the API ")  @QueryParam("context") String context,  @ApiParam(value = "Tenant Domain of the API ")  @QueryParam("tenantDomain") String tenantDomain) throws APIManagementException{
        return delegate.getAPIS(apiName, version, context, tenantDomain, securityContext);
    }
}
