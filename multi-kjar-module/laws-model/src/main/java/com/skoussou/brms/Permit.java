package com.skoussou.brms;

public class Permit implements Conclusion {

	private String permitType;
	private String responisbleBody;
	public Permit(String permitType, String responisbleBody) {
		super();
		this.permitType = permitType;
		this.responisbleBody = responisbleBody;
	}
	public String getPermitType() {
		return permitType;
	}
	public String getResponisbleBody() {
		return responisbleBody;
	}
	@Override
	public String toString() {
		return "Permit [permitType=" + permitType + ", responisbleBody=" + responisbleBody + "]";
	}

	
	
}
