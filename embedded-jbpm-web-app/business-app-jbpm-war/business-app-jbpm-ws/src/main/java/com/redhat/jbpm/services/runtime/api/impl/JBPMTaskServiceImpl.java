package com.redhat.jbpm.services.runtime.api.impl;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.jbpm.services.api.UserTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.jbpm.config.TemporaryMaestroUserGroupCallbackInterceptorImpl;
import com.redhat.jbpm.config.TemporaryMaestroUserGroupCallback;
import com.redhat.jbpm.services.runtime.api.BusinessTaskService;

@Interceptors({ TemporaryMaestroUserGroupCallbackInterceptorImpl.class })
public class JBPMTaskServiceImpl implements BusinessTaskService {

	private static final Logger logger = LoggerFactory.getLogger(JBPMTaskServiceImpl.class);
	
	@Inject
	private UserTaskService taskSvc;
	
	// TODO - This is added here in order to add MANUALLY user and roles before claiming/completing task. CNAM method is to use interceptor to do this
//	@Inject
//	private TemporaryMaestroUserGroupCallback userGroupStorage;

	
	@Override
	public void autoCompleteTask(String deploymentId, Long taskId, Map<String, Object> params, final String pIdDemandeur, final List<String> pListeGroupes) {
//		logger.info("autoCompleteTask : Task ID["+taskId+"] as User ["+params.get("userId")+"] with Groups ["+params.get("groups")+"]");
//
//		// TODO - This is added here in order to add MANUALLY user and roles before claiming/completing task. CNAM method is to use interceptor to do this
//		userGroupStorage.pushThreadUserGroups((String) params.get("userId"), (List<String>) params.get("groups"));
//		
//		taskSvc.completeAutoProgress(taskId, (String) params.get("userId"), params);
		
		logger.info("autoCompleteTask : Task ID["+taskId+"] as User ["+pIdDemandeur+"] with Groups ["+pListeGroupes+"]");
		
		taskSvc.completeAutoProgress(taskId, (String) params.get("userId"), params);

	}

	@Override
	public void claimTask(String deploymentId, Long taskId, Map<String, Object> params, final String pIdDemandeur, final List<String> pListeGroupes) {
//		logger.info("claimTask : Task ID["+taskId+"] as User ["+params.get("userId")+"] with Groups ["+params.get("groups")+"]");
//
//		// TODO - This is added here in order to add MANUALLY user and roles before claiming/completing task. CNAM method is to use interceptor to do this
//		userGroupStorage.pushThreadUserGroups((String) params.get("userId"), (List<String>) params.get("groups"));
//		
//		taskSvc.claim(taskId, (String) params.get("userId"));	
		
		logger.info("claimTask : Task ID["+taskId+"] as User ["+pIdDemandeur+"] with Groups ["+pListeGroupes+"]");
		
		taskSvc.claim(taskId, (String) params.get("userId"));		
	}

	@Override
	public void completeTask(String deploymentId, Long taskId, Map<String, Object> params, final String pIdDemandeur, final List<String> pListeGroupes) {
//		logger.info("completeTask : Task ID["+taskId+"] as User ["+params.get("userId")+"] with Groups ["+params.get("groups")+"]");
//
//		// TODO - This is added here in order to add MANUALLY user and roles before claiming/completing task. CNAM method is to use interceptor to do this
//		userGroupStorage.pushThreadUserGroups((String) params.get("userId"), (List<String>) params.get("groups"));
//		
//		taskSvc.start(taskId, (String) params.get("userId"));				
//		taskSvc.complete(taskId, (String) params.get("userId"), params)
		
		logger.info("completeTask : Task ID["+taskId+"] as User ["+pIdDemandeur+"] with Groups ["+pListeGroupes+"]");
		
		taskSvc.start(taskId, (String) params.get("userId"));				
		taskSvc.complete(taskId, (String) params.get("userId"), params);		

		
		
	}

}
