package com.redhat.jbpm.services.management.api.impl;

import java.util.Collection;

import javax.inject.Inject;

import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.jbpm.services.api.DeploymentService;
import org.jbpm.services.api.model.DeployedUnit;
import org.jbpm.services.api.model.DeploymentUnit;

import com.redhat.jbpm.services.management.api.KJARDeploymentService;

public class KJARDeploymentServiceImpl implements KJARDeploymentService {

	@Inject
	DeploymentService deploymentSvc;
	
	@Override
	public void deployKJAR(String releaseId) {
		DeploymentUnit unit = getDeploymentUnit(releaseId);
		
		//TODO - Define logic if you are re-deploying existing deployment UNIT (Maybe desired for SNAPPSHOTS) 
		if (!deploymentSvc.isDeployed(unit.getIdentifier())) {
			deploymentSvc.deploy(unit);
		}
	}

	@Override
	public void undeployKJAR(String releaseId) {
		DeploymentUnit unit = getDeploymentUnit(releaseId);
		
		//TODO - Define logic if you are re-deploying existing deployment UNIT (Maybe desired for SNAPPSHOTS) 
		if (deploymentSvc.isDeployed(unit.getIdentifier())) {
			deploymentSvc.undeploy(unit);
		}
	}
	

	@Override
	public Collection<DeployedUnit> getDeployedKJARs() {
		return deploymentSvc.getDeployedUnits();
		
	}	

	@Override
	public void activateKJAR(String releaseId) {
		DeploymentUnit unit = getDeploymentUnit(releaseId);

		if (deploymentSvc.isDeployed(unit.getIdentifier())) {
			deploymentSvc.activate(unit.getIdentifier());
		}
	}

	@Override
	public void deactivateKJAR(String releaseId) {
		DeploymentUnit unit = getDeploymentUnit(releaseId);

		if (deploymentSvc.isDeployed(unit.getIdentifier())) {
			deploymentSvc.deactivate(unit.getIdentifier());
		}
	}
	
	private DeploymentUnit getDeploymentUnit(String releaseId) {
		// TODO - Exception Handling due to null
		String[] splitted = releaseId.split(":");

		DeploymentUnit unit = new KModuleDeploymentUnit(splitted[0], splitted[1], splitted[2]);
		return unit;
		
	}


}
