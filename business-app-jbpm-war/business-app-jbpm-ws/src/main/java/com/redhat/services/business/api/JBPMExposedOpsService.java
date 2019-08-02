/*
 * JBoss, Home of Professional Open Source
 * Copyright 2015, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.redhat.services.business.api;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import com.redhat.consulting.jbpm.model.TaskActionDetails;


@WebService(targetNamespace = "urn://helloworld/HelloWorld")
public interface JBPMExposedOpsService {

	/**
	 * Starts a new Business Process
	 *
	 * @return The ProcessInstance Id in a message
	 */
	@WebMethod
	String startNewBusinessProcess(@WebParam(name = "KjarName") String KjarName, @WebParam(name = "processId") String processId);

	/**
	 * Claims and completes tasks
	 * 
	 * @param deploymentId
	 * @param person
	 * @return
	 */
	@WebMethod
	String claimCompleteTask(@WebParam(name = "deploymentId") String deploymentId, @WebParam(name = "TaskDetails") final TaskActionDetails task);
	
	/**
	 * Claims task on behalf of user
	 * 
	 * @param deploymentId
	 * @param person
	 * @return
	 */
	@WebMethod
	String claimTask(@WebParam(name = "deploymentId") String deploymentId, @WebParam(name = "TaskDetails") final TaskActionDetails task);
	
	/**
	 * Completes task on behalf of user
	 * 
	 * @param deploymentId
	 * @param person
	 * @return
	 */
	@WebMethod
	String completeTask(@WebParam(name = "deploymentId") String deploymentId, @WebParam(name = "TaskDetails") final TaskActionDetails task);	
}
