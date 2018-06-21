package com.skoussou.brms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum ActionType {

	BUILDING(new ArrayList<String>(Arrays.asList("REBUILD","BUILD")));

	private List<String> possibleActivityActions;
	
	ActionType(List<String> actions) {
		this.possibleActivityActions = actions;
	}
}
