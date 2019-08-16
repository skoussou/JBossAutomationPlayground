package com.redhat.jbpm.services.management.api;

import java.util.Collection;

import org.jbpm.services.api.model.DeployedUnit;

/**
 * A simple CDI service which is able to manage RH PAM KJAR Deployments
 *
 * @author Stelios Kousouris (stelios@redhat.com)
 * 
 *
 */
public interface KJARDeploymentService {

	/**
	 * Deploy KJAR in Maestro APP
	 * @param releaseID
	 */
    void deployKJAR(String releaseID);
    
	/**
	 * Undeploy KJAR from Maestro APP
	 * @param releaseID
	 */    
    void undeployKJAR(String releaseID);
    
	/**
	 * GET KJAR deployed in Maestro APP
	 * @param releaseID
	 * @return 
	 */    
    Collection<DeployedUnit> getDeployedKJARs();
    
	/**
	 * Activate KJAR in Maestro APP
	 * @param releaseID
	 */    
    void activateKJAR(String releaseID);
    
	/**
	 * DeActivate KJAR in Maestro APP
	 * @param releaseID
	 */    
    void deactivateKJAR(String releaseID);


}
