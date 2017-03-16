/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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

import javax.enterprise.util.AnnotationLiteral;

import org.guvnor.structure.backend.config.OrgUnit;
import org.guvnor.structure.config.SystemRepositoryChangedEvent;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.jboss.weld.environment.se.WeldContainer;
import org.kie.config.cli.CliContext;
import org.kie.config.cli.command.CliCommand;
import org.kie.config.cli.support.InputReader;

public class RemoveGroupFromOrganizationalUnitCliCommand implements CliCommand {

    @Override
    public String getName() {
        return "remove-group-org-unit";
    }
    
    private static final String ORG_UNIT_NAME = "ORG_UNIT_NAME";
    private static final String SECURITY_GROUPS = "SECURITY_GROUPS";

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
		String groupsIn = inputs.get(SECURITY_GROUPS)==null ? "":inputs.get(SECURITY_GROUPS);

        OrganizationalUnit organizationalUnit = organizationalUnitService.getOrganizationalUnit( name );
        if ( organizationalUnit == null ) {
            return "No Organizational Unit " + name + " was found";
        }

        
        if ( groupsIn.trim().length() > 0 ) {

            String[] groups = groupsIn.split( "," );
            for ( String group : groups ) {
                organizationalUnitService.removeGroup( organizationalUnit, group );
                result.append( "Group " + group + " removed successfully from Organizational Unit " + organizationalUnit.getName() + "\n" );
            }
        }
        container.getBeanManager().fireEvent(new SystemRepositoryChangedEvent(), new AnnotationLiteral<org.guvnor.structure.backend.config.Repository>() {});
        container.getBeanManager().fireEvent(new SystemRepositoryChangedEvent(), new AnnotationLiteral<OrgUnit>() {});
        return result.toString();
    }

    private HashMap<String, String> readInput(CliContext context, StringBuffer result, HashMap<String, String> inputs) throws Exception {

        // PROMPT BASED COMMANDS
        if (context.getCommands() == null || context.getCommands().size() == 0) {
            InputReader input = context.getInput();
            
            System.out.print( ">>Organizational Unit name:" );
            String name = input.nextLine();

            System.out.print( ">>Security groups (comma separated list):" );
            String groupsIn = input.nextLine();
             
			inputs.put(ORG_UNIT_NAME, name);
			inputs.put(SECURITY_GROUPS, groupsIn);

	    // FILE BASED COMMANDS
    	} else {

            String name = context.getCommands().pop();
			inputs.put(ORG_UNIT_NAME, name);
            System.out.println( ">>Organizational Unit name:" +name);

            String groupsIn = context.getCommands().pop();
			inputs.put(SECURITY_GROUPS, groupsIn);
            System.out.println( ">>Security groups (comma separated list):"+ groupsIn);
    	}
        
        return inputs;
    }
}
