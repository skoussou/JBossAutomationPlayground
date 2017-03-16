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

import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryService;
import org.jboss.weld.environment.se.WeldContainer;
import org.kie.config.cli.CliContext;
import org.kie.config.cli.command.CliCommand;
import org.kie.config.cli.support.InputReader;

public class AddRepositoryToOrganizationalUnitCliCommand implements CliCommand {

    @Override
    public String getName() {
        return "add-repo-org-unit";
    }
    
    private static final String ORG_UNIT_NAME = "ORG_UNIT_NAME";
    private static final String REPO_ALIAS = "REPO_ALIAS";

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
		String alias = inputs.get(REPO_ALIAS)==null ? "":inputs.get(REPO_ALIAS);
		OrganizationalUnit organizationalUnit = organizationalUnitService.getOrganizationalUnit( name );		
		Repository repo = repositoryService.getRepository( alias );
		
        if ( organizationalUnit == null ) {
            return "No Organizational Unit " + name + " was found";
        }
        if ( repo == null ) {
            return "No repository " + alias + " was found";
        }
        if (organizationalUnit.getRepositories().contains(repo)) {
            return "Repository " + alias + " is already added to " + name;
        }

        organizationalUnitService.addRepository( organizationalUnit, repo );
        result.append( "Repository " + repo.getAlias() + " was successfully added to Organizational Unit " + organizationalUnit.getName() );
        return result.toString();
    }
    
    private HashMap<String, String> readInput(CliContext context, StringBuffer result, HashMap<String, String> inputs) throws Exception {

        // PROMPT BASED COMMANDS
        if (context.getCommands() == null || context.getCommands().size() == 0) {
            InputReader input = context.getInput();
            System.out.print( ">>Organizational Unit name:" );
            String name = input.nextLine();

            System.out.print( ">>Repository alias:" );
            String alias = input.nextLine();
             
			inputs.put(ORG_UNIT_NAME, name);
			inputs.put(REPO_ALIAS, alias);
;

	    // FILE BASED COMMANDS
    	} else {

            String name = context.getCommands().pop();
			inputs.put(ORG_UNIT_NAME, name);
            System.out.println( ">>Organizational Unit name:"+name);

            String alias = context.getCommands().pop();
			inputs.put(REPO_ALIAS, alias);
            System.out.println( ">>Repository alias:"+alias);
    	}
        
        return inputs;
    }

}
