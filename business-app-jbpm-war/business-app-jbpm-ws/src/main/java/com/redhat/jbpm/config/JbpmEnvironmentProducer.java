package com.redhat.jbpm.config;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnit;

import org.jbpm.services.api.DeploymentService;
import org.jbpm.services.cdi.Kjar;
import org.jbpm.services.cdi.Selectable;
import org.jbpm.services.cdi.producer.UserGroupInfoProducer;
import org.jbpm.services.task.audit.JPATaskLifeCycleEventListener;
import org.kie.api.task.TaskLifeCycleEventListener;
import org.kie.api.task.UserInfo;
import org.kie.internal.identity.IdentityProvider;

/**
 * Provides within the Maestro Application (along with beans.xml and possibly properties TBD if 
 * they are hardcoded here or externalized) the bootstrapping and configuration of RH PAM JBPM 
 * Engine and Services in Embedded Mode
 * 
 * @author stkousso
 *
 */
@ApplicationScoped
public class JbpmEnvironmentProducer {

	@PersistenceUnit(unitName = "org.jbpm.domain")
	private EntityManagerFactory emf;

	
	@PostConstruct
	public void postConstruct() {
		System.setProperty("org.jbpm.var.log.length", "1024");
		System.setProperty("org.apache.cxf.logging.enabled", "true");
		//System.setProperty("kie.maven.settings.custom", "/home/stkousso/.m2/settings.xml");
	
		System.setProperty("jbpm.audit.jms.connection.factory", "JmsXA");
		System.setProperty("jbpm.audit.jms.connection.factory.jndi", "java:/JmsXA");
		System.setProperty("jbpm.audit.jms.enabled", "true");
		System.setProperty("jbpm.audit.jms.queue", "KIE.AUDIT.ALL");
		System.setProperty("jbpm.audit.jms.queue.jndi", "queue/KIE.AUDIT.ALL");
		System.setProperty("jbpm.audit.jms.transacted", "true");
		
		
	// https://access.redhat.com/documentation/en-us/red_hat_jboss_bpm_suite/6.4/html/administration_and_configuration_guide/configuration_properties
	}
	
	
	@Produces
	public EntityManagerFactory produceEntityManagerFactory() {
		if (this.emf == null) {
			this.emf = Persistence
					.createEntityManagerFactory("org.jbpm.domain");
		}
		return this.emf;
	}

	@Inject
	@Kjar
	private Instance<DeploymentService> deploymentService;

	@Produces
	public DeploymentService produceDeploymentService() {
		return deploymentService.select(new AnnotationLiteral<Kjar>() {}).get();
	}

	@Produces
	public TaskLifeCycleEventListener produceAuditListener() {
		return new JPATaskLifeCycleEventListener(true);
	}

	@Produces
	public IdentityProvider produceIdentityProvider() {
		return new IdentityProvider() {

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return "";
			}

			@Override
			public List<String> getRoles() {
				// TODO Auto-generated method stub
				return new ArrayList<String>();
			}

			@Override
			public boolean hasRole(String role) {
				// TODO Auto-generated method stub
				return true;
			}
			// implement identity provider
		};	    
	}

	@Inject
	@Selectable
	private UserGroupInfoProducer userGroupInfoProducer;

	@Produces
	public org.kie.api.task.UserGroupCallback produceSelectedUserGroupCalback() {
		return userGroupInfoProducer.produceCallback();
	}

	@Produces
	public UserInfo produceUserInfo() {
		return userGroupInfoProducer.produceUserInfo();
	}


}