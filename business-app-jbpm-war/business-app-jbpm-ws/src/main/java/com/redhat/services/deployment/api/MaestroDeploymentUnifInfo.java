package com.redhat.services.deployment.api;

import java.io.Serializable;
import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlAccessType;


@XmlType(name = "KieContainer")
@XmlAccessorType(XmlAccessType.FIELD)
public class MaestroDeploymentUnifInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8312603664632402684L;

	private String identifier;
	
	private Boolean activated;

	public MaestroDeploymentUnifInfo() {
		
	}
	
	public MaestroDeploymentUnifInfo(String identifier, Boolean isActive) {
		super();
		this.identifier = identifier;
		this.activated = isActive;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public Boolean getIsActive() {
		return activated;
	}

	public void setIsActive(Boolean isActive) {
		this.activated = isActive;
	}
	
	
}
