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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryService;
import org.jboss.weld.environment.se.WeldContainer;
import org.kie.config.cli.CliContext;
import org.kie.config.cli.command.CliCommand;
import org.kie.config.cli.support.InputReader;

public class CreateOrganizationalUnitCliCommand implements CliCommand {

    @Override
    public String getName() {
        return "create-org-unit";
    }

    private static final String ORG_UNIT_NAME = "ORG_UNIT_NAME";
    private static final String ORG_UNIT_OWNER = "ORG_UNIT_OWNER";
    private static final String ORG_DEFAULT_PACKAGE_NAME = "ORG_DEFAULT_PACKAGE_NAME";
    private static final String ORG_UNIT_REPOSITORIES = "ORG_UNIT_REPOSITORIES";

    @Override
    public String execute( CliContext context ) {
        StringBuffer result = new StringBuffer();
        WeldContainer container = context.getContainer();

        OrganizationalUnitService organizationalUnitService = container.instance().select( OrganizationalUnitService.class ).get();
        RepositoryService repositoryService = container.instance().select( RepositoryService.class ).get();
        
        HashMap<String, String> inputs = new HashMap<String, String>();
        
		try {
			inputs = readInput(context, result, inputs);
		} catch (Exception e) {
			System.out.println("Exception caught gathering facts during command ["+getName()+"]");
			return e.getMessage();
		}
        
		String name = inputs.get(ORG_UNIT_NAME);
		String owner = inputs.get(ORG_UNIT_OWNER)==null ? "":inputs.get(ORG_UNIT_OWNER);
		String defaultGroupId = inputs.get(ORG_DEFAULT_PACKAGE_NAME)==null ? "":inputs.get(ORG_DEFAULT_PACKAGE_NAME);
		String repos = inputs.get(ORG_UNIT_REPOSITORIES)==null ? "":inputs.get(ORG_UNIT_REPOSITORIES);

        Collection<Repository> repositories = new ArrayList<Repository>();
        if ( repos != null && repos.trim().length() > 0 ) {
            String[] repoAliases = repos.split( "," );
            for ( String alias : repoAliases ) {
                Repository repo = repositoryService.getRepository( alias );
                if ( repo != null ) {
                    repositories.add( repo );
                } else {
                    System.out.println( "WARN: Repository with alias " + alias + " does not exists and will be skipped" );
                }
            }
        }

        OrganizationalUnit organizationalUnit = organizationalUnitService.createOrganizationalUnit( name, owner, defaultGroupId, repositories );
        result.append( "Organizational Unit " + organizationalUnit.getName() + " successfully created" );
        return result.toString();
    }

    private HashMap<String, String> readInput(CliContext context, StringBuffer result, HashMap<String, String> inputs) throws Exception {

        // PROMPT BASED COMMANDS
        if (context.getCommands() == null || context.getCommands().size() == 0) {
            InputReader input = context.getInput();
            System.out.print( ">>Organizational Unit name:" );
            String name = input.nextLine();

            System.out.print( ">>Organizational Unit owner:" );
            String owner = input.nextLine();

            System.out.print( ">>Default Group Id for this Organizational Unit:" );
            String defaultGroupId = input.nextLine();

            System.out.print( ">>Repositories (comma separated list):" );
            String repos = input.nextLine();
             
			inputs.put(ORG_UNIT_NAME, name);
			inputs.put(ORG_UNIT_OWNER, owner);
			inputs.put(ORG_DEFAULT_PACKAGE_NAME, defaultGroupId);
			inputs.put(ORG_UNIT_REPOSITORIES, repos);

	    // FILE BASED COMMANDS
    	} else {

    		String name = context.getCommands().pop();
			inputs.put(ORG_UNIT_NAME, name);
            System.out.println( ">>Organizational Unit name:"+name);

    		String owner = context.getCommands().pop();
			inputs.put(ORG_UNIT_OWNER, owner);
            System.out.print( ">>Organizational Unit owner:"+owner);

    		String groupId = context.getCommands().pop();
			inputs.put(ORG_UNIT_OWNER, groupId);
            System.out.println( ">>Default Group Id for this Organizational Unit:"+groupId);
            
    		String repos = context.getCommands().pop();
    		System.out.println("ACTUAL REPOS LIST["+repos+"]");
			inputs.put(ORG_UNIT_OWNER, repos);
            System.out.println( ">>Repositories (comma separated list): "+repos);
    	}
        
        return inputs;
    }
    
}
