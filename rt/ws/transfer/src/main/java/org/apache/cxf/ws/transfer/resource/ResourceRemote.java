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

package org.apache.cxf.ws.transfer.resource;

import org.apache.cxf.ws.transfer.Create;
import org.apache.cxf.ws.transfer.CreateResponse;
import org.apache.cxf.ws.transfer.ReferenceParametersType;
import org.apache.cxf.ws.transfer.resourcefactory.ResourceFactory;
import org.apache.cxf.ws.transfer.validationtransformation.ValidAndTransformHelper;

/**
 *
 * @author erich
 */
public class ResourceRemote extends ResourceLocal implements ResourceFactory {

    @Override
    public CreateResponse create(Create body) {
        ValidAndTransformHelper.validationAndTransformation(getValidators(), body.getRepresentation());
        ReferenceParametersType refParams = getManager().create(body.getRepresentation());
        CreateResponse response = new CreateResponse();
        response.getResourceCreated().setReferenceParameters(refParams);
        response.setRepresentation(body.getRepresentation());
        return response;
    }
    
}
