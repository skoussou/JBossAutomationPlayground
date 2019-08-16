package com.redhat.jbpm.services.util;

public class JBPMIntegrationUtils {

	public static String GAV_SEPARATOR = ":";
	
	public static String createDeploymentUnit(String KjarGroupId, String KjarArtifactId, String KjarVersion) {
		return new StringBuffer(KjarGroupId+GAV_SEPARATOR+KjarArtifactId+GAV_SEPARATOR+KjarVersion).toString();
	}

}
