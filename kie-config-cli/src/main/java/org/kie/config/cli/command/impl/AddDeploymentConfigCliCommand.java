/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.config.cli.command.impl;

import java.util.HashMap;

import org.guvnor.structure.deployment.DeploymentConfigService;
import org.jboss.weld.environment.se.WeldContainer;
import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.kie.config.cli.CliContext;
import org.kie.config.cli.command.CliCommand;
import org.kie.config.cli.support.InputReader;

public class AddDeploymentConfigCliCommand implements CliCommand {

	@Override
	public String getName() {
		return "add-deployment";
	}

    private static final String GROUP_ID = "GROUP_ID";
    private static final String ARTIFACT_ID = "ARTIFACT_ID";
    private static final String VERSION = "VERSION";
    private static final String KBASE_NAME = "KBASE_NAME";
    private static final String KSESSION_NAME = "KSESSION_NAME";
    private static final String RUNTIME_STRATEGY = "RUNTIME_STRATEGY";
	
	@Override
	public String execute(CliContext context) {
		StringBuffer result = new StringBuffer();
		WeldContainer container = context.getContainer();

		DeploymentConfigService deploymentConfigService = container.instance().select(DeploymentConfigService.class).get();
		
        HashMap<String, String> inputs = new HashMap<String, String>();
        
		try {
			inputs = readInput(context, result, inputs);
		} catch (Exception e) {
			System.out.println("Exception caught gathering facts during command ["+getName()+"]");
			return e.getMessage();
		}
		
		String groupId = inputs.get(GROUP_ID);
		String artifactId = inputs.get(ARTIFACT_ID);
		String version = inputs.get(VERSION);
		String kbase = inputs.get(KBASE_NAME);
		String ksession = inputs.get(KSESSION_NAME);
		String strategy = inputs.get(RUNTIME_STRATEGY);

		
		KModuleDeploymentUnit unit = new KModuleDeploymentUnit(groupId, artifactId, version, kbase, ksession, strategy);
		
		deploymentConfigService.addDeployment(unit.getIdentifier(), unit);
		
		result.append("Deployment " + unit.getIdentifier() + " has been successfully added");
		return result.toString();
	}
    
    private HashMap<String, String> readInput(CliContext context, StringBuffer result, HashMap<String, String> inputs) throws Exception {

        // PROMPT BASED COMMANDS
        if (context.getCommands() == null || context.getCommands().size() == 0) {
    		InputReader input = context.getInput();
    		System.out.print(">>GroupId:");
    		String groupId = input.nextLine();
    		
    		System.out.print(">>ArtifactId:");
    		String artifactId = input.nextLine();
    		
    		System.out.print(">>Version:");
    		String version = input.nextLine();
    		
    		System.out.print(">>KBase name:");
    		String kbase = input.nextLine();
    		
    		System.out.print(">>KSession name:");
    		String ksession = input.nextLine();
    		
    		System.out.print(">>Runtime strategy[SINGLETON]:");
    		String strategy = input.nextLine();
    		
    		if (strategy.trim().length() == 0) {
    			strategy = "SINGLETON";
    		}

			inputs.put(GROUP_ID, groupId);
			inputs.put(ARTIFACT_ID, artifactId);
			inputs.put(VERSION, version);
			inputs.put(KBASE_NAME, kbase);			
			inputs.put(KSESSION_NAME, ksession);
			inputs.put(RUNTIME_STRATEGY, strategy);

	    // FILE BASED COMMANDS
    	} else {
           
    		String groupId = context.getCommands().pop();
			inputs.put(GROUP_ID, groupId);
    		System.out.println(">>GroupId:"+groupId);

    		String artifactId = context.getCommands().pop();
			inputs.put(ARTIFACT_ID, artifactId);
    		System.out.println(">>ArtifactId:"+artifactId);

    		String version = context.getCommands().pop();
			inputs.put(VERSION, version);
			System.out.println(">>Version:"+version);

    		String kbase = context.getCommands().pop();
			inputs.put(KBASE_NAME, kbase);
    		System.out.println(">>KBase name:"+kbase);

    		String ksession = context.getCommands().pop();
			inputs.put(KSESSION_NAME, ksession);
    		System.out.println(">>KSession name:"+ksession);

    		String strategy = context.getCommands().pop();
    		if (strategy.trim().length() == 0) {
    			strategy = "SINGLETON";
    		}
			inputs.put(RUNTIME_STRATEGY, strategy);
    		System.out.println(">>Runtime strategy[SINGLETON]:"+strategy);
    	}
        
        return inputs;
    }
}
