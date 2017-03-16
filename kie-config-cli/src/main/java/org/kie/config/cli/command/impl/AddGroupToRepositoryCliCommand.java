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

import javax.enterprise.event.Event;
import javax.enterprise.util.AnnotationLiteral;

import org.guvnor.structure.backend.config.OrgUnit;
import org.guvnor.structure.config.SystemRepositoryChangedEvent;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryService;
import org.jboss.weld.environment.se.WeldContainer;
import org.kie.config.cli.CliContext;
import org.kie.config.cli.command.CliCommand;
import org.kie.config.cli.support.InputReader;

public class AddGroupToRepositoryCliCommand implements CliCommand {

	@Override
	public String getName() {
		return "add-group-repo";
	}

    private static final String REPO_ALIAS = "REPO_ALIAS";
    private static final String GROUPS_IN = "GROUPS_IN";

	@Override
	public String execute(CliContext context) {
		StringBuffer result = new StringBuffer();
		WeldContainer container = context.getContainer();

		RepositoryService repositoryService = container.instance().select(RepositoryService.class).get();
		
        HashMap<String, String> inputs = new HashMap<String, String>();
        
		try {
			inputs = readInput(context, result, inputs);
		} catch (Exception e) {
			System.out.println("Exception caught gathering facts during command ["+getName()+"]");
			return e.getMessage();
		}
		String alias = inputs.get(REPO_ALIAS)==null ? "":inputs.get(REPO_ALIAS);
		String groupsIn = inputs.get(GROUPS_IN)==null ? "":inputs.get(GROUPS_IN);
		Repository repo = repositoryService.getRepository(alias);

		if (groupsIn.trim().length() > 0) {
			
			String[] groups = groupsIn.split(",");
			for (String group : groups) {
				if (repo.getGroups().contains(group)) {
					continue;
				}
				repositoryService.addGroup(repo, group);
				result.append("Group " + group + " added successfully to repository " + repo.getAlias() + "\n");
			}
		}
		container.getBeanManager().fireEvent(new SystemRepositoryChangedEvent(), new AnnotationLiteral<OrgUnit>() {});
		return result.toString();
	}

    private HashMap<String, String> readInput(CliContext context, StringBuffer result, HashMap<String, String> inputs) throws Exception {

        // PROMPT BASED COMMANDS
        if (context.getCommands() == null || context.getCommands().size() == 0) {
            InputReader input = context.getInput();
    		System.out.print(">>Repository alias:");
    		String alias = input.nextLine();

    		System.out.print(">>Security groups (comma separated list):");
    		String groupsIn = input.nextLine();
             
			inputs.put(REPO_ALIAS, alias);
			inputs.put(GROUPS_IN, groupsIn);

	    // FILE BASED COMMANDS
    	} else {

            String alias = context.getCommands().pop();
			inputs.put(REPO_ALIAS, alias);
            System.out.println(">Repository alias:"+alias);

            String groupsIn = context.getCommands().pop();
			inputs.put(GROUPS_IN, groupsIn);
            System.out.println( ">>Security groups (comma separated list):"+groupsIn);
    	}
        
        return inputs;
    }
	
}
