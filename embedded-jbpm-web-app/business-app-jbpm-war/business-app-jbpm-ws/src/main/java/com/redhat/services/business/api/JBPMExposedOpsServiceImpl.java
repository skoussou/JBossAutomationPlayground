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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.jws.WebService;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import com.redhat.consulting.jbpm.model.TaskActionDetails;
import com.redhat.jbpm.services.runtime.api.BusinessTaskService;
import com.redhat.jbpm.services.runtime.api.BusinessWorkflowService;
import com.redhat.jbpm.services.util.JBPMConstant;
import com.redhat.jbpm.services.util.JBPMIntegrationUtils;
import com.redhat.services.deployment.api.MaestroDeploymentUnifInfo;

@WebService(serviceName = "JBPMExposedOpsService", portName = "JBPMExposedOps", name = "JBPMExposedOps", endpointInterface = "com.redhat.services.business.api.JBPMExposedOpsService", targetNamespace = "urn://jbpmexposedops/JBPMExposedOps")
public class JBPMExposedOpsServiceImpl implements JBPMExposedOpsService {

	@Inject
	private BusinessWorkflowService processService;
	
	@Inject
	private BusinessTaskService taskService;
	
	@Override
	public String startNewBusinessProcess(String KjarName, String processId) {
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("pMainVariable", "CNAM");
		
		
		//String deploymentId = JBPMIntegrationUtils.createDeploymentUnit(JBPMConstant.PROCESS_PACKAGE, JBPMConstant.TEST_BUSINESS_PROJECT_KJAR, JBPMConstant.VERSION_1_0_0);
		String deploymentId = KjarName;
		
		//Long newProcessId = processService.startBusinessProcess(deploymentId,JBPMConstant.TEST_BUSINESS_PROJECT_KJAR_SIMPLE_AUTOMATED_PROC_ID, params);
		Long newProcessId = processService.startBusinessProcess(deploymentId,processId, params);
		
		//return "New Process Instances ["+newProcessId+"] started for process ["+JBPMConstant.TEST_BUSINESS_PROJECT_KJAR_SIMPLE_AUTOMATED_PROC_ID+"]";
		return "New Process Instances ["+newProcessId+"] started for process ["+processId+"]";

	}

	@Override
	public String claimCompleteTask(String deploymentId, TaskActionDetails task) {
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", task.getUserId());
		params.put("groups", createGroupsByCommaSepration(task.getGroups()));
//		String deploymentId = JBPMIntegrationUtils.createDeploymentUnit(JBPMConstant.PROCESS_PACKAGE, JBPMConstant.TEST_BUSINESS_PROJECT_KJAR, JBPMConstant.VERSION_1_0_0);
		
		//taskService.autoCompleteTask(deploymentId, task.getTaskId(), params);
		taskService.autoCompleteTask(deploymentId, task.getTaskId(), params, task.getUserId(), createGroupsByCommaSepration(task.getGroups()));

		return "Hello!! Hopefully TASK ["+task.getTaskId()+"] has beeen claimed and auto-completed by [" + task.getUserId() + "] please check it in DB";
	}
	

	@Override
	public String claimTask(String deploymentId, TaskActionDetails task) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", task.getUserId());
		params.put("groups", createGroupsByCommaSepration(task.getGroups()));
		
		//taskService.claimTask(deploymentId, task.getTaskId(), params);
		taskService.claimTask(deploymentId, task.getTaskId(), params, task.getUserId(), createGroupsByCommaSepration(task.getGroups()));

		return "Hello!! Hopefully TASK ["+task.getTaskId()+"] has beeen claimed by [" + task.getUserId() + "] please check it in DB";
	}

	@Override
	public String completeTask(String deploymentId, TaskActionDetails task) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", task.getUserId());
		params.put("groups", createGroupsByCommaSepration(task.getGroups()));
		
		//taskService.completeTask(deploymentId, task.getTaskId(), params);
		taskService.completeTask(deploymentId, task.getTaskId(), params, task.getUserId(), createGroupsByCommaSepration(task.getGroups()));

		return "Hello!! Hopefully TASK ["+task.getTaskId()+"] has beeen completed by [" + task.getUserId() + "] please check it in DB";
	}	
	
	private static List<String> createGroupsByCommaSepration(String groups) {
		String [] jbpmgroups = groups.split(",");
		List<String> jbpmgroupslist = Arrays.asList(jbpmgroups);
		return jbpmgroupslist;
	}


}
