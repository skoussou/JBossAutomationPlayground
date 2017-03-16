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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.SshSessionFactory;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryService;
import org.jboss.weld.environment.se.WeldContainer;
import org.jboss.weld.literal.NamedLiteral;
import org.kie.config.cli.CliContext;
import org.kie.config.cli.command.CliCommand;
import org.kie.config.cli.support.GitRepositoryHelperContext;
import org.kie.config.cli.support.InputReader;
import org.kie.config.cli.support.InteractiveUsernamePasswordCredentialsProvider;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystemAlreadyExistsException;

public class CloneGitRepositoryCliCommand implements CliCommand {

    private static final String GIT_LOCAL = "git://system";
    private static final String GIT_DEFAULT_UPSTREAM = "ssh://localhost:8001/system";

    @Override
    public String getName() {
        return "clone-git-repo";
    }

    private static final String SYSTEM_REPO_URL = "SYSTEM_REPO_URL";
    private static final String REPO_USER = "REPO_USER";
    private static final String REPO_PASSWORD = "REPO_PASSWORD";
    
    @Override
    public String execute( CliContext context ) {

        WeldContainer container = context.getContainer();

        IOService ioService = container.instance().select( IOService.class, new NamedLiteral( "configIO" ) ).get();
        
        HashMap<String, String> inputs = new HashMap<String, String>();
        StringBuffer result = new StringBuffer();
        
		try {
			inputs = readInput(context, result, inputs);
		} catch (Exception e) {
			System.out.println("Exception caught gathering facts during command ["+getName()+"]");
			return e.getMessage();
		}
		String systemGitRepoUrl = inputs.get(SYSTEM_REPO_URL);
		String user = inputs.get(REPO_USER);
		String password = inputs.get(REPO_PASSWORD)==null ? "":inputs.get(REPO_PASSWORD);
        
        URI gitUri = URI.create( systemGitRepoUrl );
        if ( gitUri.getScheme().equalsIgnoreCase( "ssh" ) ) {
        	systemGitRepoUrl = gitUri.getScheme() + "://" + user + "@" + gitUri.getHost() + ":" + gitUri.getPort() + gitUri.getPath();
        }

        context.addParameter( "git-upstream", systemGitRepoUrl );
        context.addParameter( "git-local", GIT_LOCAL );

        Map<String, String> env = new HashMap<String, String>();
        env.put( "init", "true" );
        env.put( "origin", systemGitRepoUrl );

        if ( gitUri.getScheme().equalsIgnoreCase( "ssh" ) ) {
            // JGitFileSystemProvider's initialization configures the SshSessionFactory to use a per-session CredentialsProvider
            // expecting public-private keys for the SSH protocol. Revert the SshSessionFactory configuration to use the default
            // Factory that uses the default CredentialsProvider configured below.
            SshSessionFactory.setInstance( null );

            // use special credential provider to support prompt for SSH connections to unknown hosts
            CredentialsProvider.setDefault( new InteractiveUsernamePasswordCredentialsProvider( user, password, context.getInput() ) );
        } else {
            env.put( "username", System.getProperty( "git.user", user ) );
            env.put( "password", System.getProperty( "git.password", password ) );
        }
        try {
            ioService.newFileSystem( URI.create( GIT_LOCAL ), env );
        } catch ( FileSystemAlreadyExistsException e ) {
            ioService.getFileSystem( URI.create( GIT_LOCAL + "?push=" + systemGitRepoUrl + "&force" ) );
        }

        System.setProperty("logged.user", user);

        //Record credentials and base URL details for use in GitRepositoryHelper
        GitRepositoryHelperContext gitRepositoryHelperContext = container.instance().select( GitRepositoryHelperContext.class ).get();
        gitRepositoryHelperContext.setUser( user );
        gitRepositoryHelperContext.setPassword( password );
        gitRepositoryHelperContext.setScheme( gitUri.getScheme() );
        gitRepositoryHelperContext.setHost( gitUri.getHost() );
        gitRepositoryHelperContext.setPort( gitUri.getPort() );

        return "Cloned successfully";
    }
    
    private HashMap<String, String> readInput(CliContext context, StringBuffer result, HashMap<String, String> inputs) throws Exception {
        String alias = null;

        // PROMPT BASED COMMANDS
        if (context.getCommands() == null || context.getCommands().size() == 0) {
            System.out.println( ">>Please specify location of remote git system repository [" + GIT_DEFAULT_UPSTREAM + "]" );

            String systemGitRepoUrl = context.getInput().nextLine();
            if ( systemGitRepoUrl == null || "".equals( systemGitRepoUrl.trim() ) ) {
                systemGitRepoUrl = GIT_DEFAULT_UPSTREAM;
            }
            
            System.out.println( ">>Please enter username:" );
            String user = context.getInput().nextLine();
    		
            String password = "";
            System.out.println( ">>Please enter password:" );
            password = context.getInput().nextLineNoEcho();
             
			inputs.put(SYSTEM_REPO_URL, systemGitRepoUrl);
			inputs.put(REPO_USER, user);
			inputs.put(REPO_PASSWORD, password);

	    // FILE BASED COMMANDS
    	} else {

    		String origin = context.getCommands().pop();
			System.out.println( ">>Remote git system repository: <"+origin+">" );
			inputs.put(SYSTEM_REPO_URL, origin);
    		
    		String user = context.getCommands().pop();
    		System.out.println( ">>Username: <"+user+">" );
			inputs.put(REPO_USER, user);

    		String password = context.getCommands().pop();
    		System.out.println( ">>Password:*****" );
			inputs.put(REPO_PASSWORD, password);

    	}
        
        return inputs;
    }
}
