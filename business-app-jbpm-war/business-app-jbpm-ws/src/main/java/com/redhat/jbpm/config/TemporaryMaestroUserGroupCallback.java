/**
 * Copyright 2015, Red Hat, Inc.
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

package com.redhat.jbpm.config;

import org.kie.api.task.UserGroupCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;

import java.util.ArrayList;
import java.util.List;

/**
 * UserGroupCallback for CNAM P3V0 mixing JBPM Framework User/Groups identification with CNAM's custom manner to provide storage
 * as part of the call
 * 
 * 
 * @author stkousso
 *
 */
@ApplicationScoped
@Alternative
public class TemporaryMaestroUserGroupCallback implements UserGroupCallback {
	
	private static final Logger logger = LoggerFactory.getLogger(TemporaryMaestroUserInfoProducer.class);

	private static final ThreadLocal<UserGroupsInfos> USERGROUPSHOLDER = new ThreadLocal<UserGroupsInfos>();

	public boolean existsUser(String userId) {
		
		logger.warn(" existsUser("+userId+") ignored due to custom CNAM user/group storage");

		// Storage can be null during CREATE/READY states so not using the storage
		// logger.info("@ {"+USERGROUPSHOLDER.get().getUserId() +"--> ["+USERGROUPSHOLDER.get().getUserId().equals(userId)+"]} @");
		
		// always returning true by design unless CNAM change method of user/group capture
		// return USERGROUPSHOLDER.get().getUserId().equals(userId);
		return true;
	}
	
	public boolean existsGroup(String groupId) {

		logger.warn(" existsGroup("+groupId+") ignored due to custom CNAM user/group storage");
		// Storage can be null during CREATE/READY states so not using the storage
		// logger.info("@ {"+USERGROUPSHOLDER.get().getGroupIds() +"--> ["+USERGROUPSHOLDER.get().getGroupIds().contains(groupId)+"]} @");

		// always returning true by design unless CNAM change method of user/group capture
        // return USERGROUPSHOLDER.get().getGroupIds().contains(groupId);	
		return true;
		
	}

	@Override
	public List<String> getGroupsForUser(String userId) {
		// Utilizing custom Storage to extract user groups
		logger.info("@@@@@@@@@@@@@ getGroupsForUser("+userId+") has groups --> {"+TemporaryMaestroUserGroupCallback.getThreadUserGroups(userId)+"} @@@@@@@@@@@@@@@@@@@@@@@@");

		return TemporaryMaestroUserGroupCallback.getThreadUserGroups(userId);		
	}
	
	
    /**
     * Classe static interne pour construire le UserGroupsInfos
     *
     * @author jgros
     *
     */
    private static class UserGroupsInfos {

        /**
         * userID
         */
        private final transient String userId;

        /**
         * Liste de groupId
         */
        private transient List<String> groupids;
    	
//        /**
//         * userID
//         */
//        //private final transient String userId = "stelios";
//    	private transient String userId = "stelios";
//
//        /**
//         * Liste de groupId
//         */
//        private transient List<String> groupids = Arrays.asList(new String[]{"redhat"});

        public UserGroupsInfos(final String user, final List<String> groups) {
            this.userId = user;
            if (groups == null) {
                this.groupids = new ArrayList<String>(0);
            } else {
                this.groupids = groups;
            }
        }

        /**
         * Getter de user
         *
         * @return the user
         */
        public String getUserId() {
            return userId;
        }

        /**
         * Getter de groups
         *
         * @return the groups
         */
        public List<String> getGroupIds() {
            return groupids;
        }

    }

    /**
     * Permet d'obtenir les groupes d'un utilisateur
     *
     * @param userId
     * @return null if no information or information with different userId
     */
    public static List<String> getThreadUserGroups(final String userId) {
        final UserGroupsInfos userGroupsInfos = USERGROUPSHOLDER.get();
        if (userGroupsInfos == null) {
            return null; // No pushUserGroups on this Thread
        }

        final String settedUserId = userGroupsInfos.getUserId();
        if ( (settedUserId == null && userId != null) ||
                (settedUserId != null && !settedUserId.equals(userId))) {
            return null; // bad user ... can't use info
            // should never happen ... maybe a Thread did "set" twice
        }
        // NB: If both null ... we accept it ! (should never happen).
        return userGroupsInfos.getGroupIds();
    }

    /**
     * Methode permettant de push un objet UserGroupsInfos dans le Thread local
     *
     * @param userId
     * @param groupIds
     */
    public static void pushThreadUserGroups(final String userId, final List<String> groupIds) {
        USERGROUPSHOLDER.set(new UserGroupsInfos(userId, groupIds));
    }

    /**
     * Methode permettant de nettoyer le Thread local
     *
     * @param userId
     * @param groupIds
     */
    public static void cleanThreadUserGroups() {
        USERGROUPSHOLDER.set(null);
    }
}
