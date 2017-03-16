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
package org.kie.config.cli.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kie.config.cli.command.impl.AddDeploymentConfigCliCommand;
import org.kie.config.cli.command.impl.AddRepositoryToOrganizationalUnitCliCommand;
import org.kie.config.cli.command.impl.AddGroupToOrganizationalUnitCliCommand;
import org.kie.config.cli.command.impl.AddGroupToProjectCliCommand;
import org.kie.config.cli.command.impl.AddGroupToRepositoryCliCommand;
import org.kie.config.cli.command.impl.CreateOrganizationalUnitCliCommand;
import org.kie.config.cli.command.impl.CreateRepositoryCliCommand;
import org.kie.config.cli.command.impl.DiscardCliCommand;
import org.kie.config.cli.command.impl.ExitCliCommand;
import org.kie.config.cli.command.impl.FetchGitRepositoryCliCommand;
import org.kie.config.cli.command.impl.HelpCliCommand;
import org.kie.config.cli.command.impl.ListDeploymentsCliCommand;
import org.kie.config.cli.command.impl.ListOrganizationalUnitCliCommand;
import org.kie.config.cli.command.impl.ListProjectDetailsCliCommand;
import org.kie.config.cli.command.impl.ListRepositoriesCliCommand;
import org.kie.config.cli.command.impl.PushGitRepositoryCliCommand;
import org.kie.config.cli.command.impl.RemoveDeploymentConfigCliCommand;
import org.kie.config.cli.command.impl.RemoveOrganizationalUnitCliCommand;
import org.kie.config.cli.command.impl.RemoveRepositoryCliCommand;
import org.kie.config.cli.command.impl.RemoveRepositoryFromOrganizationalUnitCliCommand;
import org.kie.config.cli.command.impl.RemoveGroupFromOrganizationalUnitCliCommand;
import org.kie.config.cli.command.impl.RemoveGroupFromProjectCliCommand;
import org.kie.config.cli.command.impl.RemoveGroupFromRepositoryCliCommand;

public class CliCommandRegistry {

	private static CliCommandRegistry instance;
	
	private Map<String, CliCommand> commands = new HashMap<String, CliCommand>();
	
	private CliCommandRegistry() {
		commands.put("exit", new ExitCliCommand());
        commands.put("discard", new DiscardCliCommand());
        commands.put("help", new HelpCliCommand());
        commands.put("list-deployment", new ListDeploymentsCliCommand());
        commands.put("list-repo", new ListRepositoriesCliCommand());
        commands.put("list-org-units", new ListOrganizationalUnitCliCommand());
        commands.put("create-org-unit", new CreateOrganizationalUnitCliCommand());
        commands.put("remove-org-unit", new RemoveOrganizationalUnitCliCommand());
        commands.put("add-deployment", new AddDeploymentConfigCliCommand());
        commands.put("remove-deployment", new RemoveDeploymentConfigCliCommand());
        commands.put("create-repo", new CreateRepositoryCliCommand());
        commands.put("remove-repo", new RemoveRepositoryCliCommand());
        commands.put("add-repo-org-unit", new AddRepositoryToOrganizationalUnitCliCommand());
        commands.put("remove-repo-org-unit", new RemoveRepositoryFromOrganizationalUnitCliCommand());
        commands.put("add-group-repo", new AddGroupToRepositoryCliCommand());
        commands.put("remove-group-repo", new RemoveGroupFromRepositoryCliCommand());
        commands.put("add-group-org-unit", new AddGroupToOrganizationalUnitCliCommand());
        commands.put("remove-group-org-unit", new RemoveGroupFromOrganizationalUnitCliCommand());
        commands.put("add-group-project", new AddGroupToProjectCliCommand());
        commands.put("remove-group-project", new RemoveGroupFromProjectCliCommand());
        commands.put("push-changes", new PushGitRepositoryCliCommand());
		commands.put("fetch-changes", new FetchGitRepositoryCliCommand());
		commands.put("list-project-details", new ListProjectDetailsCliCommand());
	}
	
	public static CliCommandRegistry get() {
		if (instance == null) {
			instance = new CliCommandRegistry();
		}
		
		return instance;
	}
	
	public CliCommand getCommand(String name) {
		
		return commands.get(name);
	}

	public List<String> findMatching(String commandName) {
		List<String> matched = new ArrayList<String>();
		for (String command : commands.keySet()) {
			if (command.startsWith(commandName)) {
				matched.add(command);
			}
		}
		return matched;
	}
}
