/*
 * JBoss, Home of Professional Open Source
 * Copyright 2015, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.redhat.services.deployment.api;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.drools.core.xml.jaxb.util.JaxbListAdapter;
import org.drools.core.xml.jaxb.util.JaxbListWrapper;
import org.jbpm.services.api.model.DeployedUnit;
import org.jbpm.services.api.model.DeploymentUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.jbpm.config.TemporaryMaestroUserInfoProducer;
import com.redhat.jbpm.services.management.api.KJARDeploymentService;
import com.redhat.jbpm.services.util.JAXBContextFactory;

/**
 * A simple REST service which is able to deploy, undeploy, activate, de-activate 
 * RH PAM KieContainers in an embedded fashion to the app
 *
 * @author stelios@redhat.com
 *
 */

@Path("/server/containers")
public class BusinessComponentsDeploymentService {
	
	private static final Logger logger = LoggerFactory.getLogger(BusinessComponentsDeploymentService.class);
	
    @Inject
    KJARDeploymentService kjarDeploymentSvc;


    @PUT
    @Path("{releaseId}")
    @Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
    public Response addNewBusinessLogicComponent(@PathParam(value = "releaseId") String releaseId) {
    	kjarDeploymentSvc.deployKJAR(releaseId);
    	
    	// TODO - Also handle exceptions with other codes
    	return Response.ok().build();
    }

    @DELETE
    @Path("{releaseId}")
    @Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
    public Response removeBusinessLogicComponent(@PathParam(value = "releaseId") String releaseId) {
    	System.out.println("BusinessComponentsDeploymentService.removeBusinessLogicComponent.releaseId ==> "+ releaseId);

    	kjarDeploymentSvc.undeployKJAR(releaseId);
    	
    	// TODO - Also handle exceptions with other codes    	
    	return Response.ok().build();
    }
    
    @GET
    @Path("/")
    //@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
    @Produces({"application/json","application/xml"})
    public MaestroDeploymentUnits getBusinessLogicComponents() {

    	Collection<DeployedUnit> units = kjarDeploymentSvc.getDeployedKJARs();
    	
    	logger.info("<------------------------ Deployment Units active inside Maeastro ----------------------->");
    	logger.info("Deployment Units inside Maeastro");

    	MaestroDeploymentUnits dbUnits = new MaestroDeploymentUnits();
    	if (units != null) {
    		units.forEach(value -> logger.info(value.getDeploymentUnit().getIdentifier()));
    		units.forEach(unit -> dbUnits.getUnits().add(new MaestroDeploymentUnifInfo(unit.getDeploymentUnit().getIdentifier(), unit.isActive())));
    	}
    	
    	logger.info("<------------------------ Deployment Units inside Maeastro ----------------------->");

    	return dbUnits;
    }    
    
    @PUT
    @Path("/activate/{releaseId}")
    @Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
    public Response activateBusinessLogicComponent(@PathParam(value = "releaseId") String releaseId) {
    	kjarDeploymentSvc.activateKJAR(releaseId);
    	
    	// TODO - Also handle exceptions with other codes    	
    	return Response.ok().build();
    }
    
    @PUT
    @Path("/deactivate/{releaseId}")
    @Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
    public Response deactivateBusinessLogicComponent(@PathParam(value = "releaseId") String releaseId) {
    	System.out.println("BusinessComponentsDeploymentService.deactivateBusinessLogicComponent.releaseId ==> "+ releaseId);
    	kjarDeploymentSvc.deactivateKJAR(releaseId);
    	
    	// TODO - Also handle exceptions with other codes    	
    	return Response.ok().build();
    }

}
