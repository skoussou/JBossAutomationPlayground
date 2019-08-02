package com.redhat.services.deployment.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlRootElement;

import org.jbpm.services.api.model.DeployedUnit;

@XmlRootElement
public class DeploymentUnitCollectionWrapper {


	public ArrayList <DeployedUnit> myCoolection = null;
//	private List<DeployedUnit> wrappedCollection;
//	
//    public DeploymentUnitCollectionWrapper(Collection<DeployedUnit> units) {
//    	
//		this.wrappedCollection = units.stream().collect(Collectors.toCollection(ArrayList::new)); 
//	}
//
//	public List<DeployedUnit> getWrappedCollection() {
//		return wrappedCollection;
//	}

	
}
