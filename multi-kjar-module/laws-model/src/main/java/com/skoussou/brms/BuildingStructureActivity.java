package com.skoussou.brms;

/**
 * 
 * @author stelios
 *
 */
public class BuildingStructureActivity implements Activity {

	private ActivityType type;
	private ActionType actionType;
	private Size size;

	public BuildingStructureActivity(ActivityType type, ActionType actionType, Size size) {
		super();
		this.type = type;
		this.actionType = actionType;
		this.size = size;
	}
	
	public ActivityType getType() {
		return type;
	}

	public void setType(ActivityType type) {
		this.type = type;
	}

	public ActionType getActionType() {
		return actionType;
	}

	public void setActionType(ActionType actionType) {
		this.actionType = actionType;
	}
	
	
	public Size getSize() {
		return size;
	}

	public void setSize(Size size) {
		this.size = size;
	}

}
