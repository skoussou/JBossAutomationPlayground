package com.redhat.consulting.jbpm;

import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import org.jbpm.process.audit.jms.AsyncAuditLogReceiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.consulting.jbpm.async.MaestroAsyncAuditLogReceiver;


/**
 * Note: in 7.3.1 there is now a complete receiver for Process and Task Events.
 * It is described here: http://mswiderski.blogspot.com/2018/11/audit-log-mode-applied-to-all-audit-data.html
 * Code here: https://github.com/kiegroup/jbpm/blob/master/jbpm-services/jbpm-kie-services/src/main/java/org/jbpm/kie/services/impl/CompositeAsyncAuditLogReceiver.java
 * Therefore, this now is better than our custom MaestroAsyncAuditLogReceiver.java having said that it would not be utilising MaestroAuditTaskData which contains the added content and hence going forward take as an example the code at https://github.com/kiegroup/jbpm/blob/master/jbpm-services/jbpm-kie-services/src/main/java/org/jbpm/kie/services/impl/CompositeAsyncAuditLogReceiver.java and implement your own 
 * 
 * @author stkousso
 *
 */

@MessageDriven(name="JBPMAsyncAuditLogReceiver")
public class JBPMAsyncAuditLogReceiver extends  MaestroAsyncAuditLogReceiver {


	private static final Logger logger = LoggerFactory.getLogger(JBPMAsyncAuditLogReceiver.class);

	@PersistenceUnit(unitName = "org.jbpm.domain.audit")
	private EntityManagerFactory emf;

	public JBPMAsyncAuditLogReceiver() {
		super(null);
	}

	public JBPMAsyncAuditLogReceiver(EntityManagerFactory entityManagerFactory) {
		super(entityManagerFactory);
	}

	@Override
	public void onMessage(Message message) {
		super.onMessage(message);

		try {
			logger.trace("Audit log message received and persisted {}", ((TextMessage) message).getText());
		} catch (JMSException e) {
		}

	}

	public EntityManager getEntityManager() {
		return emf.createEntityManager();
	}

}
