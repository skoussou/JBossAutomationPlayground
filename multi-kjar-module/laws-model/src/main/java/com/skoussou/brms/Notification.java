package com.skoussou.brms;

public class Notification implements Conclusion {
	private String messageFor;
	private String message;
	public Notification(String messageFor, String message) {
		super();
		this.messageFor = messageFor;
		this.message = message;
	}
	public String getMessageFor() {
		return messageFor;
	}
	public String getMessage() {
		return message;
	}
	
	@Override
	public String toString() {
		return "Notification [messageFor=" + messageFor + ", message=" + message + "]";
	}
	

}
