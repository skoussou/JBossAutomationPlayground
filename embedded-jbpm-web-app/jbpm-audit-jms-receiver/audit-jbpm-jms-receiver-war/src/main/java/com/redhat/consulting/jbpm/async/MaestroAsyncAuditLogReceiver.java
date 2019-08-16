package com.redhat.consulting.jbpm.async;

import static org.kie.soup.commons.xstream.XStreamUtils.createTrustingXStream;

import java.util.List;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.jbpm.process.audit.AbstractAuditLogger;
import org.jbpm.process.audit.NodeInstanceLog;
import org.jbpm.process.audit.ProcessInstanceLog;
import org.jbpm.process.audit.jms.AsyncAuditLogReceiver;
import org.jbpm.services.task.audit.impl.model.AuditTaskImpl;
import org.jbpm.services.task.audit.impl.model.TaskEventImpl;
import org.jbpm.services.task.audit.impl.model.TaskVariableImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.consulting.jbpm.JBPMAsyncAuditLogReceiver;
import com.thoughtworks.xstream.XStream;

public class MaestroAsyncAuditLogReceiver extends AsyncAuditLogReceiver {

	private static final Logger logger = LoggerFactory.getLogger(MaestroAsyncAuditLogReceiver.class);

	
    private enum AuditVariableTypesEnum{
    	INPUT,
    	OUPUT;
    }
	
    private XStream xstream;
	
	public MaestroAsyncAuditLogReceiver(EntityManagerFactory entityManagerFactory) {
		super(entityManagerFactory);
		initXStream();
	}
	
    

    private void initXStream() {
        if(xstream==null) {
            xstream = createTrustingXStream();
            String[] voidDeny = {"void.class", "Void.class"};
            xstream.denyTypes(voidDeny);
        }
    }
	
    @SuppressWarnings("unchecked")
    @Override
    public void onMessage(Message message) {
        if (message instanceof TextMessage) {
            EntityManager em = getEntityManager();
            TextMessage textMessage = (TextMessage) message;
            try {
           	
                String messageContent = textMessage.getText();
               // Integer eventType = textMessage.getIntProperty("EventType");
               // A check as in AuditTask messages this is not setup by the producer and throws NumberformatException
                Integer eventType = new Integer (-1);
                if (textMessage.getObjectProperty("EventType") != null) {
                	eventType = textMessage.getIntProperty("EventType");
                }
                
                Object event = xstream.fromXML(messageContent);
                
                switch (eventType) {
                case AbstractAuditLogger.AFTER_NODE_ENTER_EVENT_TYPE:
                    NodeInstanceLog nodeAfterEnterEvent = (NodeInstanceLog) event;
                    if (nodeAfterEnterEvent.getWorkItemId() != null) {
                    List<NodeInstanceLog> result = em.createQuery(
                            "from NodeInstanceLog as log where log.nodeInstanceId = :nodeId and log.type = 0")
                            .setParameter("nodeId", nodeAfterEnterEvent.getNodeInstanceId()).getResultList();
                            
                            if (result != null && result.size() != 0) {
                            	NodeInstanceLog log = result.get(result.size() - 1);
                               log.setWorkItemId(nodeAfterEnterEvent.getWorkItemId());
                               
                               
                               em.merge(log);   
                           }
                    }
                    break;
                
                case AbstractAuditLogger.AFTER_COMPLETE_EVENT_TYPE:
                    ProcessInstanceLog processCompletedEvent = (ProcessInstanceLog) event;
                    List<ProcessInstanceLog> result = em.createQuery(
                            "from ProcessInstanceLog as log where log.processInstanceId = :piId and log.end is null")
                            .setParameter("piId", processCompletedEvent.getProcessInstanceId()).getResultList();
                            
                            if (result != null && result.size() != 0) {
                               ProcessInstanceLog log = result.get(result.size() - 1);
                               log.setOutcome(processCompletedEvent.getOutcome());
                               log.setStatus(processCompletedEvent.getStatus());
                               log.setEnd(processCompletedEvent.getEnd());
                               log.setDuration(processCompletedEvent.getDuration());
                               
                               em.merge(log);   
                           }
                    break;
                case -1:
                	// In the absence of AUDITASKDATA Event Type from the Producerwe utilize -1 show missing status

                	
        			logger.trace("########## PROCESSING in MaestroAsyncAuditLogReceiver ###########");
        			logger.trace(message.toString());
        			logger.trace("==================================================");
        			logger.trace(((TextMessage) message).getText());
        			logger.trace("#####################");                       	
                	
                	//org.jbpm.services.task.audit.impl.model.AuditTaskData auditTaskEvent = (org.jbpm.services.task.audit.impl.model.AuditTaskData) event;
                	
                	com.redhat.jbpm.config.task.audit.jms.MaestroAuditTaskData auditTaskEvent = (com.redhat.jbpm.config.task.audit.jms.MaestroAuditTaskData) event;
                	
                	// This check as (unsure why) an AuditTask Event is created without any content in the various NodeInstanceLog audits after creation
                	// as at creation is there no audit loss is occured
                	if (auditTaskEvent.getAuditTask() != null) {

                		List<AuditTaskImpl> results = em.createQuery("from AuditTaskImpl as log where log.taskId = :piId")
                				.setParameter("piId", auditTaskEvent.getAuditTask().getTaskId()).getResultList();

                		// If there is an existing AuditTaskImpl (and maybe TaskVariableImpl & TaskEventImp)
                		if (results != null && results.size() != 0) {
                			AuditTaskImpl log = results.get(results.size() - 1);
                			logger.debug("<----------------------------------------------------->");
                			logger.debug("UPDATING AUDIT TASK IMPL");
                			logger.debug("=======================");    
                			logger.debug("TASK ID: "+ auditTaskEvent.getAuditTask().getTaskId());
                			logger.debug("ACTUAL OWNER: "+auditTaskEvent.getAuditTask().getActualOwner());
                			logger.debug("STATUS: "+auditTaskEvent.getAuditTask().getStatus());     
                			logger.debug("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                			logger.debug(" BA Owners: "+ auditTaskEvent.getBusinessOwners());
                			logger.debug(" Potential Owners: "+ auditTaskEvent.getPotentialOwners());
                			logger.debug(" Excluded Owners: "+ auditTaskEvent.getExcludedOwners());
                			logger.debug("<----------------------------------------------------->");

                			// Here I guess will what can be updated on an existing AuditTaskImpl
                			log.setActivationTime(auditTaskEvent.getAuditTask().getActivationTime());
                			log.setActualOwner(auditTaskEvent.getAuditTask().getActualOwner());
                			log.setLastModificationDate(auditTaskEvent.getAuditTask().getLastModificationDate());
                			log.setStatus(auditTaskEvent.getAuditTask().getStatus());


                			em.merge(log);

                		} else {
                			logger.debug("<----------------------------------------------------->");
                			logger.debug("CREATING NEW AUDIT TASK IMPL");
                			logger.debug("============================"); 
                			logger.debug("TASK ID: "+ auditTaskEvent.getAuditTask().getTaskId());
                			logger.debug("ACTUAL OWNER: "+auditTaskEvent.getAuditTask().getActualOwner());
                			logger.debug("STATUS: "+auditTaskEvent.getAuditTask().getStatus());                   	
                			logger.debug("<----------------------------------------------------->"); 

                			em.persist(auditTaskEvent.getAuditTask());

                		}

                		processAuditTaskEvents(auditTaskEvent.getTaskEvents(), em);
                		processAuditEventVariables(auditTaskEvent.getTaskInputs(), AuditVariableTypesEnum.INPUT, em);
                		processAuditEventVariables(auditTaskEvent.getTaskOutputs(), AuditVariableTypesEnum.OUPUT, em);     
                	}

                    break;
                default:
                    em.persist(event);
                    break;
                }
                em.flush();
                em.close();
            } catch (JMSException e) {
                e.printStackTrace();
                throw new RuntimeException("Exception when receiving audit event event", e);
            }
        }

    }

    
    private void processAuditEventVariables(List<TaskVariableImpl> variables, AuditVariableTypesEnum variableType, EntityManager em ) {
    	if (variables != null && variables.size() > 0) {
   		
        	logger.debug("ADDING TASK EVENT [INPUT] VARIABLES");
        	logger.debug("=======================");                     		
    		for (TaskVariableImpl variable : variables) {
            	logger.debug("TASK ID: "+ variable.getTaskId());
            	logger.debug("VARIABLE ID: "+ variable.getName());
            	logger.debug("VARIABLE VALUE: "+ variable.getValue());
            	em.persist(variable);                            	
            	logger.debug("--------------------------------------");
    		}                      		
    	} 
    }
    
    private void processAuditTaskEvents(List<TaskEventImpl> taskEvents, EntityManager em) {
    	if (taskEvents != null && taskEvents.size() > 0) {
//    		List<TaskEventImpl> taskEvents = auditTaskEvent.getTaskEvents();
    		
        	logger.debug("ADDING TASK EVENTS");
        	logger.debug("=======================");                     		
    		for (TaskEventImpl eventEntry : taskEvents) {
            	//logger.debug("EVENT ID: "+ eventEntry.getId());
            	logger.debug("TASK ID: "+ eventEntry.getTaskId());
            	logger.debug("EVENT TYPE: "+eventEntry.getType());
            	logger.debug("EVENT INITIATED BY: "+eventEntry.getUserId());
            	em.persist(eventEntry);                            	
            	logger.debug("--------------------------------------");
    		}                      		
    	}
    }

}
