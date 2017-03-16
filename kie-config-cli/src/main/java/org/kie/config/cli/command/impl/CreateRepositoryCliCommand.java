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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryEnvironmentConfigurations;
import org.guvnor.structure.repositories.RepositoryService;
import org.jboss.weld.environment.se.WeldContainer;
import org.kie.config.cli.CliContext;
import org.kie.config.cli.command.CliCommand;
import org.kie.config.cli.support.InputReader;

public class CreateRepositoryCliCommand implements CliCommand {

    @Override
    public String getName() {
        return "create-repo";
    }
    
    private static final String REPO_ALIAS = "REPO_ALIAS";
    private static final String REPO_USER = "REPO_USER";
    private static final String REPO_PASSWORD = "REPO_PASSWORD";
    private static final String REPO_ORIGIN = "REPO_ORIGIN";

    @Override
    public String execute( CliContext context ) {
        StringBuffer result = new StringBuffer();
        WeldContainer container = context.getContainer();

        RepositoryService repositoryService = container.instance().select( RepositoryService.class ).get();

        HashMap<String, String> inputs = new HashMap<String, String>();
		try {
			inputs = readInput(context, result, repositoryService, inputs);
		} catch (Exception e) {
			System.out.println("Exception caught gathering facts during command ["+getName()+"]");
			return e.getMessage();
		}
        System.out.println("GATHERED INPUTS: <"+inputs+">");
        
        final RepositoryEnvironmentConfigurations configurations = new RepositoryEnvironmentConfigurations();
        configurations.setUserName( inputs.get(REPO_USER) );
        configurations.setPassword( inputs.get(REPO_PASSWORD) );

        if ( inputs.get(REPO_ORIGIN).trim().length() > 0 ) {
            configurations.setOrigin( inputs.get(REPO_ORIGIN) );
        }

        configurations.setManaged( false );

        //Mark this Repository as being created by the kie-config-cli tool. This has no affect on the operation
        //of the Repository in the workbench, but it does indicate to kie-config-cli that the Repository should
        //not have its origin overridden when cloning. A local clone is required to manipulate Projects.
        configurations.getConfigurationMap().put( "org.kie.config.cli.command.CliCommand",
                                                   "CreateRepositoryCliCommand" );

        Repository repo = repositoryService.createRepository( "git", inputs.get(REPO_ALIAS), configurations );
        result.append( "Repository with alias " + repo.getAlias() + " has been successfully created" );

        return result.toString();
    }
    
    private HashMap<String, String> readInput(CliContext context, StringBuffer result, RepositoryService repositoryService, HashMap<String, String> inputs) throws Exception {
        String alias = null;

        // PROMPT BASED COMMANDS
        if (context.getCommands() == null || context.getCommands().size() == 0) {
    		InputReader input = context.getInput();
    		while (alias == null) {
    			System.out.print(">>Repository alias:");
    			alias = input.nextLine();

    			try {
    				new URI("default://localhost/" + alias);
    			} catch (URISyntaxException e) {
    				System.err.print(">> Invalid value for repository alias: '" + alias + "'");
    				alias = null;
    			}
    		}

    		Repository repoCheck = repositoryService.getRepository(alias);
    		if (repoCheck != null) {
    			result.append(" Repository with alias: '" + alias + "' already exists, cannot proceed");
    			throw new Exception(result.toString());
    		}     		
    		System.out.print( ">>User:" );
    		String user = input.nextLine();

    		System.out.print( ">>Password:" );
    		String password = context.getInput().nextLineNoEcho();

    		System.out.print( ">>Remote origin:" );
    		String origin = input.nextLine();
    		
			inputs.put(REPO_ALIAS, alias);
			inputs.put(REPO_USER, user);
			inputs.put(REPO_PASSWORD, password);
			inputs.put(REPO_ORIGIN, origin);

	        // FILE BASED COMMANDS
    	} else {
    		InputReader input = context.getInput();
    		while (alias == null) {
    			alias = context.getCommands().pop();
    			System.out.print(">>Repository alias: <"+alias+">");
    			inputs.put(REPO_ALIAS, alias);

    			try {
    				new URI("default://localhost/" + alias);
    			} catch (URISyntaxException e) {
    				throw new Exception(">> Invalid value for repository alias: '" + alias + "'");
    			}
    		}

    		Repository repoCheck = repositoryService.getRepository(alias);
    		if (repoCheck != null) {
    			result.append(" Repository with alias: '" + alias + "' already exists, cannot proceed");
    			throw new Exception(result.toString());
    		}    		

    		String user = context.getCommands().pop();
    		System.out.print( ">>user: <"+user+">" );
			inputs.put(REPO_USER, user);

    		String password = context.getCommands().pop();
    		System.out.print( ">>Password:*****" );
			inputs.put(REPO_PASSWORD, password);

    		String origin = context.getCommands().pop();
			System.out.print( ">>Remote origin: <"+origin+">" );
			inputs.put(REPO_ORIGIN, origin);
    	}
    	
        return inputs;
    }

}
