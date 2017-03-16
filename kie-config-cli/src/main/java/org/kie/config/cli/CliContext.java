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
package org.kie.config.cli;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.kie.config.cli.support.InputReader;

public class CliContext {

	private Weld weld;
	private WeldContainer container;
	private InputReader input;
    private Map<String, String> parameters = new HashMap<String, String>();
    
    /** @author stkousso Adding capability to read commands from a file */
    private LinkedList<String> commands = new LinkedList<String>();
	
	protected CliContext(Weld weld, WeldContainer container, InputReader input, LinkedList<String> commands2) {
		this.weld = weld;
		this.container = container;
		this.input = input;
		this.commands = commands2;
	}
	public Weld getWeld() {
		return weld;
	}
	public void setWeld(Weld weld) {
		this.weld = weld;
	}
	public WeldContainer getContainer() {
		return container;
	}
	public void setContainer(WeldContainer container) {
		this.container = container;
	}
	public InputReader getInput() {
		return input;
	}
	public void setInput(InputReader input) {
		this.input = input;
	}
	public LinkedList<String> getCommands() {
		return commands;
	}
	public void setCommands(LinkedList<String> commands) {
		this.commands = commands;
	}
	public static CliContext buildContext(InputReader input, LinkedList<String> commands) {
		
		Weld weld = new Weld();
		WeldContainer container = weld.initialize();
		
		CliContext context = new CliContext(weld, container, input, commands);
		
		return context;
	}

    public String getParameter(String name) {
        return this.parameters.get(name);
    }

    public void addParameter(String name, String value) {
        this.parameters.put(name, value);
    }

}
