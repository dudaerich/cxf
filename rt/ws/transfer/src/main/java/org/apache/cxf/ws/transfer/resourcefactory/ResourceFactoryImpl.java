/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
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

package org.apache.cxf.ws.transfer.resourcefactory;

import java.util.ArrayList;
import java.util.List;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.ws.transfer.AttributedURIType;
import org.apache.cxf.ws.transfer.Create;
import org.apache.cxf.ws.transfer.CreateResponse;
import org.apache.cxf.ws.transfer.EndpointReferenceType;
import org.apache.cxf.ws.transfer.ReferenceParametersType;
import org.apache.cxf.ws.transfer.Representation;
import org.apache.cxf.ws.transfer.resourcefactory.resolver.ResourceReference;
import org.apache.cxf.ws.transfer.resourcefactory.resolver.ResourceResolver;
import org.apache.cxf.ws.transfer.shared.TransferConstants;
import org.apache.cxf.ws.transfer.shared.faults.InvalidRepresentation;
import org.apache.cxf.ws.transfer.validationtransformation.ResourceValidator;
import org.apache.cxf.ws.transfer.validationtransformation.ValidatorResult;

/**
 * ResourceFactory implementation.
 */
public class ResourceFactoryImpl implements ResourceFactory {

    protected ResourceResolver resourceResolver;
    
    protected List<ResourceValidator> validators;
    
    @Override
    public CreateResponse create(Create body) {
        validationAndTransformation(body.getRepresentation());
        ResourceReference resourceReference = resourceResolver.resolve(body);
        if (resourceReference.getResourceManager() != null) {
            return createLocally(body, resourceReference);
        } else {
            return createRemotely(body, resourceReference);
        }
    }

    public ResourceResolver getResourceResolver() {
        return resourceResolver;
    }

    public void setResourceResolver(ResourceResolver resourceResolver) {
        this.resourceResolver = resourceResolver;
    }

    public List<ResourceValidator> getValidators() {
        if (validators == null) {
            validators = new ArrayList<ResourceValidator>();
        }
        return validators;
    }

    public void setValidators(List<ResourceValidator> validators) {
        this.validators = validators;
    }
    
    private void validationAndTransformation(Representation representation) {
        if (validators != null && !validators.isEmpty()) {
            for (ResourceValidator validator : validators) {
                ValidatorResult valResult = validator.validate(representation);
                if (valResult.isValidate()) {
                    if (valResult.getTransformer() != null) {
                        valResult.getTransformer().transform(representation);
                    }
                    return;
                }
            }
            throw new InvalidRepresentation();
        }
    }
    
    private CreateResponse createLocally(Create body, ResourceReference ref) {
        ReferenceParametersType referenceParams = 
                    ref
                    .getResourceManager()
                    .create(body.getRepresentation());
            
        CreateResponse response = new CreateResponse();
        response.setResourceCreated(new EndpointReferenceType());
        response.getResourceCreated().setAddress(new AttributedURIType());
        response.getResourceCreated()
                .getAddress()
                .setValue(ref.getResourceURL());
        response.getResourceCreated()
                .setReferenceParameters(referenceParams);

        return response;
    }
    
    private CreateResponse createRemotely(Create body, ResourceReference ref) {
        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        factory.getInInterceptors().add(new LoggingInInterceptor());
        factory.getOutInterceptors().add(new LoggingOutInterceptor());
        factory.setServiceClass(ResourceFactory.class);
        factory.setAddress(ref.getResourceURL() 
                + TransferConstants.RESOURCE_REMOTE_SUFFIX);
        ResourceFactory client = (ResourceFactory) factory.create();
        
        return client.create(body);
    }
}
