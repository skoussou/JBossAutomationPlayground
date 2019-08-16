package com.redhat.consulting.jbpm.model;

import java.io.Serializable;

public class TaskActionDetails implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7258852808220056060L;

	private String deploymentId;
	private long taskId;
	private String userId;
	private String groups;

	public TaskActionDetails() {
		// TODO Auto-generated constructor stub
	}

	public TaskActionDetails(String deploymentId, long taskId, String userId, String groups) {
		super();
		this.deploymentId = deploymentId;
		this.taskId = taskId;
		this.userId = userId;
		this.groups = groups;
	}

	public String getDeploymentId() {
		return deploymentId;
	}

	public long getTaskId() {
		return taskId;
	}

	public void setTaskId(long taskId) {
		this.taskId = taskId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getGroups() {
		return groups;
	}

	public void setGroups(String groups) {
		this.groups = groups;
	}

	public void setDeploymentId(String deploymentId) {
		this.deploymentId = deploymentId;
	}


}
