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
import org.jboss.weld.environment.se.WeldContainer;
import org.kie.config.cli.CliContext;
import org.kie.config.cli.command.CliCommand;
import org.kie.config.cli.support.InputReader;

public class RemoveOrganizationalUnitCliCommand implements CliCommand {

    @Override
    public String getName() {
        return "remove-org-unit";
    }

    private static final String ORG_UNIT_NAME = "ORG_UNIT_NAME";
    
    @Override
    public String execute( CliContext context ) {
        StringBuffer result = new StringBuffer();
        WeldContainer container = context.getContainer();

        OrganizationalUnitService organizationalUnitService = container.instance().select( OrganizationalUnitService.class ).get();

        HashMap<String, String> inputs = new HashMap<String, String>();
        
		try {
			inputs = readInput(context, result, inputs);
		} catch (Exception e) {
			System.out.println("Exception caught gathering facts during command ["+getName()+"]");
			return e.getMessage();
		}
        
		String name = inputs.get(ORG_UNIT_NAME);

        OrganizationalUnit organizationalUnit = organizationalUnitService.getOrganizationalUnit( name );
        if ( organizationalUnit == null ) {
            return "No Organizational Unit " + name + " was found, exiting";
        }
        organizationalUnitService.removeOrganizationalUnit( name );
        result.append( "Organizational Unit " + name + " was removed successfully" );

        return result.toString();
    }
    
    private HashMap<String, String> readInput(CliContext context, StringBuffer result, HashMap<String, String> inputs) throws Exception {

        // PROMPT BASED COMMANDS
        if (context.getCommands() == null || context.getCommands().size() == 0) {
            InputReader input = context.getInput();
            System.out.print( ">>Organizational Unit name:" );
            String name = input.nextLine();
             
			inputs.put(ORG_UNIT_NAME, name);

	    // FILE BASED COMMANDS
    	} else {

    		String name = context.getCommands().pop();
			inputs.put(ORG_UNIT_NAME, name);
            System.out.println( ">>Organizational Unit name:"+name);

    	}
        
        return inputs;
    }

}
