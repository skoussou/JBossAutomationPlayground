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

import org.kie.config.cli.CliContext;
import org.kie.config.cli.command.CliCommand;

public class HelpCliCommand implements CliCommand {

	@Override
	public String getName() {
		return "help";
	}

	@Override
	public String execute(CliContext context) {
		StringBuffer helpMessage = new StringBuffer();
		helpMessage.append("****************** KIE CLI Help ************************\n");
		helpMessage.append("********************************************************\n");
		helpMessage.append("Available commands:\n");
		helpMessage.append("\t exit - publishes any work, cleans up temporary directories and quits this command line tool\n");
        helpMessage.append("\t discard - won't publishes local changes, cleans up temporary directories and quits this command line tool\n");
		helpMessage.append("\t help - prints this message\n");
		helpMessage.append("\t list-repo - list available repositories\n");
		helpMessage.append("\t list-org-units - list available organizational units\n");
		helpMessage.append("\t list-deployment - list available deployments\n");
		helpMessage.append("\t create-org-unit - creates new organizational unit\n");
		helpMessage.append("\t remove-org-unit - remove existing organizational unit\n");
		helpMessage.append("\t add-deployment - add new deployment unit\n");
		helpMessage.append("\t remove-deployment - remove existing deployment\n");
		helpMessage.append("\t create-repo - creates new git repository\n");
		helpMessage.append("\t remove-repo - remove existing repository from config only\n");
		helpMessage.append("\t add-repo-org-unit - add repository to the organizational unit\n");
		helpMessage.append("\t remove-repo-org-unit - remove repository from the organizational unit\n");
		helpMessage.append("\t add-group-repo - add group(s) to repository\n");
		helpMessage.append("\t remove-group-repo - remove group(s) from repository\n");
		helpMessage.append("\t add-group-org-unit - add group(s) to organizational unit\n");
		helpMessage.append("\t remove-group-org-unit - remove group(s) from organizational unit\n");
        helpMessage.append("\t add-group-project - add group(s) to project\n");
        helpMessage.append("\t remove-group-project - remove group(s) from project\n");
        helpMessage.append("\t push-changes - pushes changes to upstream repository (only online mode)\n");
		helpMessage.append("\t fetch-changes - fetches changes from upstream repository (only online mode)");
		helpMessage.append("\t list-project-details - list detailed information about project inside repository");
		return helpMessage.toString();
	}

}
