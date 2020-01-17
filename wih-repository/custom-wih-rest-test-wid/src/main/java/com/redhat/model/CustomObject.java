package com.redhat.model;

import java.io.Serializable;

public class CustomObject implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2963367410512877275L;
	/**
	 * 
	 */
	private String mapperType;
	private String myName;
	
	
	
	public CustomObject(String mapperType, String myName) {
		super();
		this.mapperType = mapperType;
		this.myName = myName;
	}
	public String getMapperType() {
		return mapperType;
	}
	public void setMapperType(String mapperType) {
		this.mapperType = mapperType;
	}
	public String getMyName() {
		return myName;
	}
	public void setMyName(String myName) {
		this.myName = myName;
	}
	@Override
	public String toString() {
		return "CustomGarantiObject [mapperType=" + mapperType + ", myName=" + myName + "]";
	}
	
	
}
