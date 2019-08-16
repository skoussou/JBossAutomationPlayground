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
 * De-activated in favour of using a different deployable to receive the audit logs
 * look at audit-jbpm-jms-receiver-war
 *  
 * @author stkousso
 *
 */
//@MessageDriven(name="JBPMAsyncAuditLogReceiver")
public class JBPMAsyncAuditLogReceiver extends  MaestroAsyncAuditLogReceiver {
//public class HelloWorldAsyncAuditLogReceiver extends AsyncAuditLogReceiver {


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
