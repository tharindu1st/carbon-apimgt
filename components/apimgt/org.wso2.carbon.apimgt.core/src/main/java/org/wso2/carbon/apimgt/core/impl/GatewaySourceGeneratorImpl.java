/*
* Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
*
*/
package org.wso2.carbon.apimgt.core.impl;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.log.CommonsLogLogChute;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.ballerinalang.composer.service.workspace.swagger.SwaggerConverterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.apimgt.core.APIMConfigurations;
import org.wso2.carbon.apimgt.core.api.GatewaySourceGenerator;
import org.wso2.carbon.apimgt.core.exception.ExceptionCodes;
import org.wso2.carbon.apimgt.core.internal.ServiceReferenceHolder;
import org.wso2.carbon.apimgt.core.models.API;
import org.wso2.carbon.apimgt.core.models.Endpoint;
import org.wso2.carbon.apimgt.core.template.APIConfigContext;
import org.wso2.carbon.apimgt.core.template.APITemplateException;
import org.wso2.carbon.apimgt.core.template.ConfigContext;
import org.wso2.carbon.apimgt.core.template.EndpointContext;
import org.wso2.carbon.apimgt.core.template.ResourceConfigContext;
import org.wso2.carbon.apimgt.core.template.dto.TemplateBuilderDTO;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
//import javax.script.ScriptException;

/**
 * Generate API config template
 */
public class GatewaySourceGeneratorImpl implements GatewaySourceGenerator {
    private static final Logger log = LoggerFactory.getLogger(GatewaySourceGeneratorImpl.class);
    private API api;
    private String packageName;

    public GatewaySourceGeneratorImpl(API api) {
        this();
        this.api = api;
    }

    public GatewaySourceGeneratorImpl() {
        APIMConfigurations config = ServiceReferenceHolder.getInstance().getAPIMConfiguration();
        packageName = config.getGatewayPackageName();
    }

    @Override
    public String getConfigStringFromTemplate(List<TemplateBuilderDTO> apiResources) throws APITemplateException {
        StringWriter writer = new StringWriter();
        String templatePath = "resources" + File.separator + "template" + File.separator + "template.xml";
        try {
            // build the context for template and apply the necessary decorators
            ConfigContext configcontext = new APIConfigContext(this.api, packageName);
            configcontext.validate();
            configcontext = new ResourceConfigContext(configcontext, this.api, apiResources);
            VelocityContext context = configcontext.getContext();
            VelocityEngine velocityengine = new VelocityEngine();
            velocityengine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
            velocityengine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
            velocityengine.setProperty(VelocityEngine.RUNTIME_LOG_LOGSYSTEM, new CommonsLogLogChute());
            velocityengine.init();
            Template template = velocityengine.getTemplate(templatePath);
            template.merge(context, writer);
        } catch (ResourceNotFoundException e) {
            log.error("Template " + templatePath + " not Found", e);
            throw new APITemplateException("Template " + templatePath + " not Found",
                    ExceptionCodes.TEMPLATE_EXCEPTION);
        } catch (ParseErrorException e) {
            log.error("Syntax error in " + templatePath, e);
            throw new APITemplateException("Syntax error in " + templatePath, ExceptionCodes.TEMPLATE_EXCEPTION);
        }
        return writer.toString();
    }

    @Override
    public String getGatewayConfigFromSwagger(String gatewayConfig, String swagger) throws APITemplateException {
        System.setProperty("bal.composer.home", System.getProperty("carbon.home"));
        /*try {
            String jsonModel = SwaggerConverterUtils.generateBallerinaDataModel(swagger, gatewayConfig);
            return jsonModel;
            return SwaggerConverterUtils.getBallerinaFromJsonModel(jsonModel);
        } catch (IOException | ScriptException e) {
            log.error("Error while generating ballerina from swagger", e);
            throw new APITemplateException("Error while generating ballerina from swagger",
                    ExceptionCodes.TEMPLATE_EXCEPTION);
        }*/
        return "";
    }

    @Override
    public String getSwaggerFromGatewayConfig(String gatewayConfig) throws APITemplateException {
        try {
            return SwaggerConverterUtils.generateSwaggerDataModel(gatewayConfig);
        } catch (IOException e) {
            log.error("Error while generating swagger from ballerina", e);
            throw new APITemplateException("Error while generating swagger from ballerina",
                    ExceptionCodes.TEMPLATE_EXCEPTION);
        }
    }

    @Override
    public void setAPI(API api) {
        this.api = api;
    }

    @Override
    public String getEndpointConfigStringFromTemplate(List<Endpoint> endpoints) throws APITemplateException {
        StringWriter writer = new StringWriter();
        String templatePath = "resources" + File.separator + "template" + File.separator + "endpoint.xml";
        try {
            // build the context for template and apply the necessary decorators
            ConfigContext configcontext = new EndpointContext(endpoints, packageName);
            VelocityContext context = configcontext.getContext();
            VelocityEngine velocityengine = new VelocityEngine();
            velocityengine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
            velocityengine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
            velocityengine.init();
            Template template = velocityengine.getTemplate(templatePath);
            template.merge(context, writer);
        } catch (ResourceNotFoundException e) {
            log.error("Template " + templatePath + " not Found", e);
            throw new APITemplateException("Template " + templatePath + " not Found",
                    ExceptionCodes.TEMPLATE_EXCEPTION);
        } catch (ParseErrorException e) {
            log.error("Syntax error in " + templatePath, e);
            throw new APITemplateException("Syntax error in " + templatePath, ExceptionCodes.TEMPLATE_EXCEPTION);
        }
        return writer.toString();
    }

    public void setApi(API api) {
        this.api = api;
    }
}
