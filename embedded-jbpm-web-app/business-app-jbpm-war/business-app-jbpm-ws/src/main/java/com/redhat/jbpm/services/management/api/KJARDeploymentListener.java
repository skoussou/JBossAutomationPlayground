package com.redhat.jbpm.services.management.api;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import org.jbpm.services.api.DeploymentEvent;
import org.jbpm.services.cdi.Activate;
import org.jbpm.services.cdi.Deactivate;
import org.jbpm.services.cdi.Deploy;
import org.jbpm.services.cdi.Undeploy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class KJARDeploymentListener {
	private static final Logger logger = LoggerFactory.getLogger(KJARDeploymentListener.class);

	public void onDeployment(@Observes @Deploy DeploymentEvent event) {
		// TODO - Logging added per CNAM here
		logger.info("@@@@@@@@@@>>>>>>> Unit {} has been successfully deployed ", event.getDeploymentId(), event.getDeployedUnit());
	}

	public void onUndeployment(@Observes @Undeploy DeploymentEvent event) {
		logger.info("@@@@@@@@@@>>>>>>> Unit {} has been successfully undeployed", event.getDeploymentId());
	}
	
    public void onActivate(@Observes @Activate DeploymentEvent event) {
		logger.info("@@@@@@@@@@>>>>>>> Unit {} has been successfully activated ", event.getDeploymentId(), event.getDeployedUnit());
    }

    public void onDeactivate(@Observes @Deactivate DeploymentEvent event) {
		logger.info("@@@@@@@@@@>>>>>>> Unit {} has been successfully de-sactivated ", event.getDeploymentId(), event.getDeployedUnit());
    }

}