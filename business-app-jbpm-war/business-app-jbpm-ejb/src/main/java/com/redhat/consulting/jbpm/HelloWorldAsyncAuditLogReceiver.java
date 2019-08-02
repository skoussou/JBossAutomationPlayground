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

//@MessageDriven(name="HelloWorldAsyncAuditLogReceiver")
public class HelloWorldAsyncAuditLogReceiver extends AsyncAuditLogReceiver {


	private static final Logger logger = LoggerFactory.getLogger(HelloWorldAsyncAuditLogReceiver.class);

//	@PersistenceUnit(unitName = "org.jbpm.domain.audit")
//	private EntityManagerFactory emf;

	public HelloWorldAsyncAuditLogReceiver() {
		super(null);
	}

	public HelloWorldAsyncAuditLogReceiver(EntityManagerFactory entityManagerFactory) {
		super(entityManagerFactory);
	}

	@Override
	public void onMessage(Message message) {
		super.onMessage(message);

		try {
			logger.info("Audit log message received {}", ((TextMessage) message).getText());
		} catch (JMSException e) {
		}

	}

//	public EntityManager getEntityManager() {
//		return emf.createEntityManager();
//	}

}
