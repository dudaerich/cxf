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

package org.apache.cxf.jaxrs.ext;

import java.util.Map;

import javax.activation.DataHandler;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.MessageBodyWorkers;

/**
 * Represents an invocation context
 *
 */
public interface MessageContext {
    
    String INBOUND_MESSAGE_ATTACHMENTS = "org.apache.cxf.jaxrs.attachments.inbound";
    String OUTBOUND_MESSAGE_ATTACHMENTS = "org.apache.cxf.jaxrs.attachments.outbound";
    
    Object get(Object key);
    void put(Object key, Object value);
    
    Map<String, DataHandler> getAttachments();
    
    UriInfo getUriInfo();
    Request getRequest();
    HttpHeaders getHttpHeaders();
    SecurityContext getSecurityContext();
    MessageBodyWorkers getProviders();
    
    HttpServletRequest getHttpServletRequest(); 
    HttpServletResponse getHttpServletResponse();
    ServletContext getServletContext();
    ServletConfig getServletConfig();
    
    <T> T getContext(Class<T> contextClass);
}
