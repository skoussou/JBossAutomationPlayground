package com.redhat.services.deployment.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@XmlRootElement(name = "DeploymentUnits")
@XmlAccessorType(XmlAccessType.FIELD)
public class MaestroDeploymentUnits implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5886518916796283656L;
	
	@JsonProperty("DeploymentUniInfo")
	private List<MaestroDeploymentUnifInfo> units = new ArrayList<MaestroDeploymentUnifInfo>();

	public MaestroDeploymentUnits(List<MaestroDeploymentUnifInfo> units) {
		super();
		this.units = units;
	}

	public MaestroDeploymentUnits() {
		// TODO Auto-generated constructor stub
	}

	public List<MaestroDeploymentUnifInfo> getUnits() {
		return units;
	}

	public void setUnits(List<MaestroDeploymentUnifInfo> units) {
		this.units = units;
	}
	
	
}
