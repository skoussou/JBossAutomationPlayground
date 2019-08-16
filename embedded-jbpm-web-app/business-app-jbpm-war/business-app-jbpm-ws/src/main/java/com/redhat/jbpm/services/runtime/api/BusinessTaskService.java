package com.redhat.jbpm.services.runtime.api;

import java.util.List;
import java.util.Map;

/**
 * 
 * Exposes within MAESTRO a business API to be used for the TASK Operations executed within RH PAM. Leak of APIs specific to RH PAM
 * should not go beyond this interface. 
 * 
 * 
 * @author stkousso
 *
 */
public interface BusinessTaskService {

	/**
	 * Claim, Start, Complete a Human Task all in one go
	 * @param deploymentId
	 * @param taskId
	 * @param params
	 */
	void autoCompleteTask(String deploymentId, Long taskId, Map<String, Object> params, final String pIdDemandeur, final List<String> pListeGroupes);
	
	/**
	 * Claim a task
	 * @param deploymentId
	 * @param taskId
	 * @param params
	 */
	void claimTask(String deploymentId, Long taskId, Map<String, Object> params, final String pIdDemandeur, final List<String> pListeGroupes);
	
	/**
	 * Start, Complete a task
	 * @param deploymentId
	 * @param taskId
	 * @param params
	 */
	void completeTask(String deploymentId, Long taskId, Map<String, Object> params, final String pIdDemandeur, final List<String> pListeGroupes);

}
