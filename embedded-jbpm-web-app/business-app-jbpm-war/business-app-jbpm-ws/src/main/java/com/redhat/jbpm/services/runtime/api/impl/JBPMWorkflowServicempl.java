package com.redhat.jbpm.services.runtime.api.impl;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.jbpm.services.api.ProcessService;
import org.jbpm.services.task.audit.impl.model.TaskEventImpl;
import org.jbpm.services.task.impl.model.TaskImpl;
import org.kie.api.task.TaskEvent;
import org.kie.api.task.model.Task;
import org.kie.internal.task.api.TaskContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.jbpm.config.TemporaryMaestroUserGroupCallback;
import com.redhat.jbpm.services.runtime.api.BusinessWorkflowService;


/**
 * Implements {@code com.redhat.jbpm.services.runtime.api.BusinessWorkflowService}
 * 
 * @author stkousso
 *
 */
@ApplicationScoped
@Transactional(value=TxType.REQUIRED)
public class JBPMWorkflowServicempl implements BusinessWorkflowService {

	private static final Logger logger = LoggerFactory.getLogger(JBPMWorkflowServicempl.class);
	
	@Inject
	ProcessService processSvc;
	
	@PostConstruct
	public void PostConstruct() {
		//TODO - Remove??
	}

	@Override
	@Transactional(value=TxType.REQUIRED)
	public Long startBusinessProcess(String deploymentId, String processId, Map<String, Object> params) {
		
		// TODO - Based on discussions with CNAM/SOPRA teams during Create/Ready states of a Human Task no further checks on groups/user existence required
		// userGroupStorage.pushThreadUserGroups((String) params.get("userId"), Arrays.asList(new String[]{"redhat"}));
		
		// TODO Handle Exceptions CNAM way
		// Any other handling for the business puproses of CNAM should be added here
		logger.debug("Starting Process ["+processId+"] in KieContainer ["+deploymentId+"] with params ["+params+"]");
		return 	processSvc.startProcess(deploymentId, processId, params);
	}
	
	@Override
	public void notifyRunningBusinessProcess(String deploymentId, Long processInstanceId, String signalName, Object params) {

		// TODO - Based on discussions with CNAM/SOPRA teams during Create/Ready states of a Human Task no further checks on groups/user existence required
		// userGroupStorage.pushThreadUserGroups((String) params.get("userId"), Arrays.asList(new String[]{"redhat"}));
		
		
		// TODO Handle Exceptions CNAM way
		// Any other handling for the business puproses of CNAM should be added here
		if (processInstanceId != null) {
			logger.info("Signalling Process with Instance ID ["+processInstanceId+"] in KieContainer ["+deploymentId+"]");
			processSvc.signalProcessInstance(deploymentId, processInstanceId, signalName, params);

		} else {
			logger.info("Signalling All processes with Signal Id ["+signalName+"] in KieContainer ["+deploymentId+"]");
			processSvc.signalEvent(deploymentId, signalName, params);
		}
	}


}
