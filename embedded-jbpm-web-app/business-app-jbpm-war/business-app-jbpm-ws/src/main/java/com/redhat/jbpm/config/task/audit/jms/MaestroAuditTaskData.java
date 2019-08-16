package com.redhat.jbpm.config.task.audit.jms;

import java.util.ArrayList;
import java.util.List;

import org.jbpm.services.task.audit.impl.model.AuditTaskData;
import org.jbpm.services.task.audit.impl.model.AuditTaskImpl;
import org.jbpm.services.task.audit.impl.model.TaskEventImpl;
import org.jbpm.services.task.audit.impl.model.TaskVariableImpl;
import org.kie.api.task.model.OrganizationalEntity;

//public class MaestroAuditTaskData extends AuditTaskData {
public class MaestroAuditTaskData {
	
    private AuditTaskImpl auditTask;

    private List<TaskEventImpl> taskEvents = new ArrayList<>();

    private List<TaskVariableImpl> taskInputs;

    private List<TaskVariableImpl> taskOutputs;
	
	private List<OrganizationalEntity> businessOwners;
	
	private List<OrganizationalEntity> potentialOwners;
	
	private List<OrganizationalEntity> excludedOwners;

	public MaestroAuditTaskData(AuditTaskImpl auditTask) {
        this.auditTask = auditTask;
    }	
	
    public MaestroAuditTaskData(AuditTaskImpl auditTask, TaskEventImpl taskEvent) {
        this.auditTask = auditTask;
        this.addTaskEvent(taskEvent);
    }

    public MaestroAuditTaskData(AuditTaskImpl auditTask, List<TaskEventImpl> taskEvents) {
        this.auditTask = auditTask;
        this.setTaskEvents(taskEvents);
    }

    public MaestroAuditTaskData(AuditTaskImpl auditTask, List<TaskEventImpl> taskEvents, List<TaskVariableImpl> taskInputs, List<TaskVariableImpl> taskOutputs) {
        this.auditTask = auditTask;
        this.setTaskEvents(taskEvents);
        this.setTaskInputs(taskInputs);
        this.setTaskOutputs(taskOutputs);
    }


    public AuditTaskImpl getAuditTask() {
        return auditTask;
    }

    public void setAuditTask(AuditTaskImpl auditTask) {
        this.auditTask = auditTask;
    }

    public List<TaskEventImpl> getTaskEvents() {
        return taskEvents;
    }

    public void setTaskEvents(List<TaskEventImpl> taskEvents) {
        this.taskEvents = taskEvents;
    }

    public List<TaskVariableImpl> getTaskInputs() {
        return taskInputs;
    }

    public void setTaskInputs(List<TaskVariableImpl> taskInputs) {
        this.taskInputs = taskInputs;
    }

    public List<TaskVariableImpl> getTaskOutputs() {
        return taskOutputs;
    }

    public void setTaskOutputs(List<TaskVariableImpl> taskOutputs) {
        this.taskOutputs = taskOutputs;
    }
    
    public void addTaskEvent(TaskEventImpl taskEvent) {
        this.taskEvents.add(taskEvent);
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
