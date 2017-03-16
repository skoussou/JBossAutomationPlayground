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

import java.util.Collection;

import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.repositories.Repository;
import org.jboss.weld.environment.se.WeldContainer;
import org.kie.config.cli.CliContext;

public class ListOrganizationalUnitCliCommand extends AbstractCliCommand {

    @Override
    public String getName() {
        return "list-org-units";
    }

    @Override
    public String execute( CliContext context ) {
        StringBuffer result = new StringBuffer();
        WeldContainer container = context.getContainer();

        OrganizationalUnitService organizationalUnitService = container.instance().select( OrganizationalUnitService.class ).get();
        Collection<OrganizationalUnit> groups = organizationalUnitService.getOrganizationalUnits();

        result.append( "Currently available Organizational Units: \n" );
        for ( OrganizationalUnit config : groups ) {
            result.append( "\tOrganizational Unit " + config.getName() + "\n" );
            result.append( "\towner: " + config.getOwner() + "\n" );
            result.append( "\tgroups: " + config.getGroups() + "\n" );
            result.append( "\trepositories: \n" );
            for ( Repository repository : config.getRepositories() ) {
                result.append( "\t\tRepository " + repository.getAlias() + "\n" );
                result.append( "\t\t\t scheme: " + repository.getScheme() + "\n" );
                result.append( "\t\t\t uri: " + repository.getUri() + "\n" );
                result.append( "\t\t\t environment: " + printEnvironment(repository.getEnvironment()) + "\n" );
                result.append( "\t\t\t repository groups: " + repository.getGroups() + "\n" );
            }
        }
        return result.toString();
    }

}
