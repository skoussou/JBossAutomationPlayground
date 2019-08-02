package com.redhat.jbpm.services.runtime.api;

import java.util.Map;

//import net.a.g.data.Person;

/**
 * Exposes within MAESTRO a business API to be used for the Business Process Operations executed within RH PAM. Leak of APIs specific to RH PAM
 * should not go beyond this interface. 
 * 
 * @author stkousso
 *
 */
public interface BusinessWorkflowService {

	/**
	 * 
	 * @param string
	 * @param testBusinessProjectrKjarSimpleAutomatedProcId
	 * @param params
	 * @return
	 */
	Long startBusinessProcess(String string, String testBusinessProjectrKjarSimpleAutomatedProcId, Map<String, Object> params);

	void notifyRunningBusinessProcess(String deploymentId, Long processInstanceId, String signalName, Object params);


}
