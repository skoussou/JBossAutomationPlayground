package com.redhat.jbpm.config.task.audit.jms;

import java.util.List;

import org.jbpm.services.task.audit.impl.model.AuditTaskData;
import org.jbpm.services.task.audit.impl.model.AuditTaskImpl;
import org.jbpm.services.task.audit.impl.model.TaskEventImpl;
import org.jbpm.services.task.audit.impl.model.TaskVariableImpl;
import org.kie.api.task.model.OrganizationalEntity;

public class MaestroAuditTaskData extends AuditTaskData {
	
	private List<OrganizationalEntity> businessOwners;
	
	private List<OrganizationalEntity> potentialOwners;
	
	private List<OrganizationalEntity> excludedOwners;

	public MaestroAuditTaskData(AuditTaskImpl auditTask) {
       super(auditTask);
    }	
	
    public MaestroAuditTaskData(AuditTaskImpl auditTask, TaskEventImpl taskEvent) {
    	super(auditTask);
    	super.addTaskEvent(taskEvent);
    }

    public MaestroAuditTaskData(AuditTaskImpl auditTask, List<TaskEventImpl> taskEvents) {
    	super(auditTask);
    	super.setTaskEvents(taskEvents);
    }

    public MaestroAuditTaskData(AuditTaskImpl auditTask, List<TaskEventImpl> taskEvents, List<TaskVariableImpl> taskInputs, List<TaskVariableImpl> taskOutputs) {
    	super(auditTask);
    	super.setTaskEvents(taskEvents);
    	super.setTaskInputs(taskInputs);
    	super.setTaskOutputs(taskOutputs);
    }


    public List<OrganizationalEntity> getBusinessOwners() {
		return businessOwners;
	}



	public void setBusinessOwners(List<OrganizationalEntity> businessOwners) {
		this.businessOwners = businessOwners;
	}



	public List<OrganizationalEntity> getPotentialOwners() {
		return potentialOwners;
	}



	public void setPotentialOwners(List<OrganizationalEntity> potentialOwners) {
		this.potentialOwners = potentialOwners;
	}



	public List<OrganizationalEntity> getExcludedOwners() {
		return excludedOwners;
	}



	public void setExcludedOwners(List<OrganizationalEntity> excludedOwners) {
		this.excludedOwners = excludedOwners;
	}
	
}
