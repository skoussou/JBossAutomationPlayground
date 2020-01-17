package com.redhat;

import org.kie.api.task.TaskEvent;
import org.kie.api.task.TaskLifeCycleEventListener;

public class CustomTaskMonitorListener implements TaskLifeCycleEventListener {

	public void beforeTaskActivatedEvent(TaskEvent event) {
		// TODO Auto-generated method stub
		
	}

	public void beforeTaskClaimedEvent(TaskEvent event) {
		// TODO Auto-generated method stub
		
	}

	public void beforeTaskSkippedEvent(TaskEvent event) {
		// TODO Auto-generated method stub
		
	}

	public void beforeTaskStartedEvent(TaskEvent event) {
		// TODO Auto-generated method stub
		
	}

	public void beforeTaskStoppedEvent(TaskEvent event) {
		// TODO Auto-generated method stub
		
	}

	public void beforeTaskCompletedEvent(TaskEvent event) {
		// TODO Auto-generated method stub
		
	}

	public void beforeTaskFailedEvent(TaskEvent event) {
		// TODO Auto-generated method stub
		
	}

	public void beforeTaskAddedEvent(TaskEvent event) {
		// TODO Auto-generated method stub
		
	}

	public void beforeTaskExitedEvent(TaskEvent event) {
		// TODO Auto-generated method stub
		
	}

	public void beforeTaskReleasedEvent(TaskEvent event) {
		// TODO Auto-generated method stub
		
	}

	public void beforeTaskResumedEvent(TaskEvent event) {
		// TODO Auto-generated method stub
		
	}

	public void beforeTaskSuspendedEvent(TaskEvent event) {
		// TODO Auto-generated method stub
		
	}

	public void beforeTaskForwardedEvent(TaskEvent event) {
		// TODO Auto-generated method stub
		
	}

	public void beforeTaskDelegatedEvent(TaskEvent event) {
		// TODO Auto-generated method stub
		
	}

	public void beforeTaskNominatedEvent(TaskEvent event) {
		// TODO Auto-generated method stub
		
	}

	public void afterTaskActivatedEvent(TaskEvent event) {
		// TODO Auto-generated method stub
		
	}

	public void afterTaskClaimedEvent(TaskEvent event) {
		System.out.println("TASK ["+event.getTask().getName()+"] CLAIMED by "+event.getTaskContext().getUserId()+" at"+event.getEventDate());
		
	}

	public void afterTaskSkippedEvent(TaskEvent event) {
		// TODO Auto-generated method stub
		
	}

	public void afterTaskStartedEvent(TaskEvent event) {
		// TODO Auto-generated method stub
		
	}

	public void afterTaskStoppedEvent(TaskEvent event) {
		// TODO Auto-generated method stub
		
	}

	public void afterTaskCompletedEvent(TaskEvent event) {
		// TODO Auto-generated method stub
		
	}

	public void afterTaskFailedEvent(TaskEvent event) {
		// TODO Auto-generated method stub
		
	}

	public void afterTaskAddedEvent(TaskEvent event) {
		// TODO Auto-generated method stub
		
	}

	public void afterTaskExitedEvent(TaskEvent event) {
		// TODO Auto-generated method stub
		
	}

	public void afterTaskReleasedEvent(TaskEvent event) {
		// TODO Auto-generated method stub
		
	}

	public void afterTaskResumedEvent(TaskEvent event) {
		// TODO Auto-generated method stub
		
	}

	public void afterTaskSuspendedEvent(TaskEvent event) {
		// TODO Auto-generated method stub
		
	}

	public void afterTaskForwardedEvent(TaskEvent event) {
		// TODO Auto-generated method stub
		
	}

	public void afterTaskDelegatedEvent(TaskEvent event) {
		// TODO Auto-generated method stub
		
	}

	public void afterTaskNominatedEvent(TaskEvent event) {
		// TODO Auto-generated method stub
		
	}

}
