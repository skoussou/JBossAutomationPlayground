package com.redhat.jbpm.config.task.audit.jms;


import java.util.Map;

import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import javax.naming.InitialContext;
import javax.naming.NamingException;


public class MaestroTaskAuditLoggerFactory {

    /**
     * Creates new instance of JMS task audit logger based on given parameters.
     * Supported parameters are as follows:
     * <ul>
     * <li>jbpm.audit.jms.transacted - determines if JMS session is transacted or not - default true - type Boolean</li>
     * <li>jbpm.audit.jms.connection.factory - connection factory instance - type javax.jms.ConnectionFactory</li>
     * <li>jbpm.audit.jms.queue - JMS queue instance - type javax.jms.Queue</li>
     * <li>jbpm.audit.jms.connection.factory.jndi - JNDI name of the connection factory to look up - type String</li>
     * <li>jbpm.audit.jms.queue.jndi - JNDI name of the queue to look up - type String</li>
     * </ul>
     * @param properties - optional properties for the type of logger to initialize it
     * @return new instance of JMS task audit logger
     */
    public static MaestroAsyncTaskLifeCycleEventProducer newJMSInstance(Map<String, Object> properties) {
    	MaestroAsyncTaskLifeCycleEventProducer logger = new MaestroAsyncTaskLifeCycleEventProducer();
        boolean transacted = true;
        if (properties.containsKey("jbpm.audit.jms.transacted")) {
            Object transactedObj = properties.get("jbpm.audit.jms.transacted");
            if (transactedObj instanceof Boolean) {
                transacted = (Boolean) properties.get("jbpm.audit.jms.transacted");
            } else {
                transacted = Boolean.parseBoolean(transactedObj.toString());
            }
        }
        
        logger.setTransacted(transacted);
        
        // set connection factory and queue if given as property
        if (properties.containsKey("jbpm.audit.jms.connection.factory")) {
            ConnectionFactory connFactory = (ConnectionFactory) properties.get("jbpm.audit.jms.connection.factory"); 
            logger.setConnectionFactory(connFactory);
        }                
        if (properties.containsKey("jbpm.audit.jms.queue")) {
            Queue queue = (Queue) properties.get("jbpm.audit.jms.queue"); 
            logger.setQueue(queue);
        }
        try {
            // look up connection factory and queue if given as property
            if (properties.containsKey("jbpm.audit.jms.connection.factory.jndi")) {
                ConnectionFactory connFactory = (ConnectionFactory) InitialContext.doLookup(
                        (String)properties.get("jbpm.audit.jms.connection.factory.jndi")); 
                logger.setConnectionFactory(connFactory);
            }                
            if (properties.containsKey("jbpm.audit.jms.queue.jndi")) {
                Queue queue = (Queue) InitialContext.doLookup((String)properties.get("jbpm.audit.jms.queue.jndi")); 
               logger.setQueue(queue);
            }
        } catch (NamingException e) {
            throw new RuntimeException("Error when looking up ConnectionFactory/Queue", e);
        }
        
        return logger;
    }
    
    /**
     * Creates new instance of JMS task audit logger based on given connection factory and queue.
     * @param transacted determines if JMS session is transacted or not
     * @param connFactory connection factory instance
     * @param queue JMS queue instance
     * @return new instance of JMS task audit logger
     */
    public static MaestroAsyncTaskLifeCycleEventProducer newJMSInstance(boolean transacted, ConnectionFactory connFactory, Queue queue) {
    	MaestroAsyncTaskLifeCycleEventProducer logger = new MaestroAsyncTaskLifeCycleEventProducer();
        logger.setTransacted(transacted);
        logger.setConnectionFactory(connFactory);
        logger.setQueue(queue);
        
        return logger;
    }
}
