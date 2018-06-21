package com.skoussou.brms;

import java.util.ArrayList;

/**
 * 
 * @author skoussou@gmail.com
 *
 */
public class BRMSEngineResults {
	private ArrayList<Permit> permits = new ArrayList<Permit> ();
	private ArrayList<Notification> notifications = new ArrayList<Notification> ();
	
	public ArrayList<Permit> getPermits() {
		return permits;
	}
	public void setPermits(ArrayList<Permit> permits) {
		this.permits = permits;
	}
	public ArrayList<Notification> getNotifications() {
		return notifications;
	}
	public void setNotifications(ArrayList<Notification> notifications) {
		this.notifications = notifications;
	}
	@Override
	public String toString() {
		return "BRMSEngineResults [permits=" + permits + ", notifications=" + notifications + "]";
	}


}
