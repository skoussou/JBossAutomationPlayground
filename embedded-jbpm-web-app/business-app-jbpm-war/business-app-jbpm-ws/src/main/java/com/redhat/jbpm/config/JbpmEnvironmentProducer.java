package com.redhat.jbpm.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;
import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnit;

import org.jbpm.services.api.DeploymentService;
import org.jbpm.services.cdi.Kjar;
import org.jbpm.services.cdi.Selectable;
import org.jbpm.services.cdi.impl.manager.InjectableRegisterableItemsFactory;
import org.jbpm.services.cdi.producer.UserGroupInfoProducer;
import org.jbpm.services.task.audit.JPATaskLifeCycleEventListener;
import org.jbpm.services.task.audit.TaskAuditLoggerFactory;
import org.jbpm.services.task.audit.jms.AsyncTaskLifeCycleEventProducer;
import org.kie.api.task.TaskLifeCycleEventListener;
import org.kie.api.task.UserInfo;
import org.kie.internal.identity.IdentityProvider;

//import com.redhat.jbpm.config.task.audit.jms.MaestroAsyncTaskLifeCycleEventProducer;
//import com.redhat.jbpm.config.task.audit.jms.MaestroTaskAuditLoggerFactory;

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


	@Inject
	@Kjar
	private Instance<DeploymentService> deploymentService;	
	

	@Inject
	@Selectable
	private UserGroupInfoProducer userGroupInfoProducer;
	
	/**
	 * In PostConstruct RH PAM JBPM Engine Behavior which will not change at runtime (eg. between environments)
	 * will be defined based on properties (SEE ??? for complete set of properties)
	 * - https://access.redhat.com/documentation/en-us/red_hat_jboss_bpm_suite/6.4/html/administration_and_configuration_guide/configuration_properties
	 * 
	 * Runtime behavior eg. kie.maven.settings.custom will be provided via system properties -Dproperty=value in
	 * - standalone.xml
	 * - hosts.xml
	 * - EAP startup
	 */
	@PostConstruct
	public void postConstruct() {
		System.setProperty("org.jbpm.var.log.length", "1024");
		System.setProperty("org.apache.cxf.logging.enabled", "true");
		//System.setProperty("kie.maven.settings.custom", "/home/stkousso/.m2/settings.xml");
	
		System.setProperty("jbpm.audit.jms.connection.factory", "JmsXA");
		System.setProperty("jbpm.audit.jms.connection.factory.jndi", "java:/JmsXA");
		//System.setProperty("jbpm.audit.jms.enabled", "true");
		System.setProperty("jbpm.audit.jms.queue", "KIE.AUDIT.ALL");
		System.setProperty("jbpm.audit.jms.queue.jndi", "queue/KIE.AUDIT.ALL");
		System.setProperty("jbpm.audit.jms.transacted", "true");
		
		/* Enables the BAMTaskEventListener class, which populates the BAMTASKSUMMARY 
		 * table to allow the BAM module to query tasks. */
		System.setProperty("org.jbpm.task.bam.enabled", "true");

		/* Enables the TaskCleanUpProcessEventListener class, which archives 
		 * and removes completed tasks with the associated process ID. */
		System.setProperty("org.jbpm.task.cleanup.enabled", "true");

		
		
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


	@Produces
	public DeploymentService produceDeploymentService() {
		return deploymentService.select(new AnnotationLiteral<Kjar>() {}).get();
	}

	
	/**
	 * Enable Instead of com.redhat.jbpm.config.task.audit.jms.MaestroAsyncTaskLifeCycleEventProducer (ie. remove that bean completely from the codebase
	 * if you prefer to use the OOB JMS based AsyncTaskLifeCycleEventProducer
	 * @return
	 */
//	@Produces
//	public TaskLifeCycleEventListener produceAuditListener() {
//		Map<String, Object> props = new HashMap<String,Object>();
//		props.put("jbpm.audit.jms.connection.factory.jndi", "java:/JmsXA");
//		//props.put("jbpm.audit.jms.enabled", "true");
//		props.put("jbpm.audit.jms.queue.jndi", "queue/KIE.AUDIT.ALL");
//		props.put("jbpm.audit.jms.transacted", "true");	
//		
//		return TaskAuditLoggerFactory.newJMSInstance(props);
//		//return new AsyncTaskLifeCycleEventProducer();
//	}
	

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


	@Produces
	public org.kie.api.task.UserGroupCallback produceSelectedUserGroupCalback() {
		return userGroupInfoProducer.produceCallback();
	}

	@Produces
	public UserInfo produceUserInfo() {
		return userGroupInfoProducer.produceUserInfo();
	}


}
