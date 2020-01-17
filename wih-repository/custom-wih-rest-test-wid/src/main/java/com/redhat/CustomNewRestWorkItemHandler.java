/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.redhat;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.jbpm.process.workitem.core.AbstractLogOrThrowWorkItemHandler;
import org.jbpm.process.workitem.core.util.RequiredParameterValidator;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;

import org.jbpm.process.workitem.core.util.Wid;
import org.jbpm.process.workitem.core.util.WidParameter;
import org.jbpm.process.workitem.core.util.WidResult;
import org.jbpm.process.workitem.core.util.service.WidAction;
import org.jbpm.process.workitem.core.util.service.WidAuth;
import org.jbpm.process.workitem.core.util.service.WidService;
import org.jbpm.process.workitem.core.util.WidMavenDepends;


public class CustomNewRestWorkItemHandler extends AbstractLogOrThrowWorkItemHandler {

    public void executeWorkItem(WorkItem workItem,
                                WorkItemManager manager) {

        
		String urlvalue = (String) workItem.getParameter("Url");
		String method = (String) workItem.getParameter("Method");
		String data = (String) workItem.getParameter("ContentData");
		String mapper = (String) workItem.getParameter("MapperType");

        Map<String, Object> results = new HashMap<String, Object>();
		
		try {
			
			RequiredParameterValidator.validate(this.getClass(), workItem);
			
			URL url = new URL(urlvalue);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod(method);
			//conn.setRequestProperty("Content-Type", "application/json");

			conn.disconnect();

			
            results.put("STATUS_CODE", "200");		
            results.put("CUSTOM_OBJECT", new com.redhat.model.CustomObject(mapper, "Stelios"));
			
            System.out.println("WIH OUTPUT: Results --> {"+results+"}");
            
			manager.completeWorkItem(workItem.getId(), results);

		} catch(Throwable cause) {
            handleException(cause);
        }       
        
        
    }

    @Override
    public void abortWorkItem(WorkItem workItem,
                              WorkItemManager manager) {
    	 manager.abortWorkItem(workItem.getId());
    }
}


