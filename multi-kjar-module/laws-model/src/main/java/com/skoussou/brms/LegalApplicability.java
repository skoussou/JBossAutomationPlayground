package com.skoussou.brms;


public class LegalApplicability implements Applicability {

	private ActivityType type;
	private Activity activity;

	public LegalApplicability(ActivityType type, Activity activity) {
		super();
		this.type = type;
		this.activity = activity;
	}


	public LegalApplicability(ActivityType type) {
		super();
		this.type = type;
	}
	

	public ActivityType getType() {
		return type;
	}
	

	public Activity getActivity() {
		return activity;
	}


	public void setActivity(Activity activity) {
		this.activity = activity;
	}

}
