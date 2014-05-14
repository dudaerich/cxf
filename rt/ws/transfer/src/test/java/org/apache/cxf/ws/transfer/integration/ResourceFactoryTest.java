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

package org.apache.cxf.ws.transfer.integration;

import java.io.PrintWriter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.ws.transfer.Create;
import org.apache.cxf.ws.transfer.CreateResponse;
import org.apache.cxf.ws.transfer.ReferenceParametersType;
import org.apache.cxf.ws.transfer.Representation;
import org.apache.cxf.ws.transfer.manager.ResourceManager;
import org.apache.cxf.ws.transfer.resourcefactory.ResourceFactory;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author erich
 */
public class ResourceFactoryTest extends IntegrationBaseTest {
    
    private static final String RESOURCE_UUID = "123456";
    
    private static final String REF_PARAM_NAMESPACE = "org.apache.cxf.transfer/manager";
    
    private static final String REF_PARAM_LOCAL_NAME = "UUID";
    
    private ReferenceParametersType createReferenceParamters() {
        try {
            ReferenceParametersType refParam = new ReferenceParametersType();
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.newDocument();
            Element uuidEl = doc.createElementNS(REF_PARAM_NAMESPACE, REF_PARAM_LOCAL_NAME);
            uuidEl.setTextContent(RESOURCE_UUID);
            refParam.getAny().add(uuidEl);
            return refParam;
        } catch (ParserConfigurationException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    @Test
    public void createLocalResource() {
        ReferenceParametersType refParams = createReferenceParamters();
        manager = EasyMock.createMock(ResourceManager.class);
        EasyMock.expect(manager.create(EasyMock.isA(Representation.class)))
                .andReturn(refParams);
        EasyMock.expectLastCall().once();
        EasyMock.replay(manager);
        
        createResourceFactory();
        
        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        factory.setBus(bus);
        factory.setServiceClass(ResourceFactory.class);
        factory.setAddress(RESOURCE_FACTORY_ADDRESS);
        factory.getInInterceptors().add(new LoggingInInterceptor(new PrintWriter(System.out)));
        factory.getOutInterceptors().add(new LoggingOutInterceptor(new PrintWriter(System.out)));
        ResourceFactory client = (ResourceFactory) factory.create();
        Create createRequest = new Create();
        Representation representation = new Representation();
        createRequest.setRepresentation(representation);
        CreateResponse response = client.create(createRequest);
        Assert.assertTrue("ResourceAddress is other than expected.",
                response.getResourceCreated().getAddress().getValue().equals(RESOURCE_ADDRESS));
        Element refParamEl = (Element) response.getResourceCreated().getReferenceParameters().getAny().get(0);
        Assert.assertEquals(REF_PARAM_NAMESPACE, refParamEl.getNamespaceURI());
        Assert.assertEquals(REF_PARAM_LOCAL_NAME, refParamEl.getLocalName());
        Assert.assertEquals(RESOURCE_UUID, refParamEl.getTextContent());
        
        EasyMock.verify(manager);
        resourceFactory.destroy();
    }
    
}
