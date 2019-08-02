package com.redhat.jbpm.config;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;

import org.jbpm.services.cdi.Selectable;
import org.jbpm.services.cdi.producer.UserGroupInfoProducer;
import org.jbpm.services.task.identity.DefaultUserInfo;
import org.jbpm.services.task.identity.MvelUserGroupCallbackImpl;
import org.kie.api.task.UserGroupCallback;
import org.kie.internal.task.api.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.jbpm.services.runtime.api.impl.JBPMWorkflowServicempl;


/**
 * 
 * @author stkousso
 *
 */
@ApplicationScoped
@Alternative
@Selectable
public class TemporaryMaestroUserInfoProducer implements UserGroupInfoProducer {

	private static final Logger logger = LoggerFactory.getLogger(TemporaryMaestroUserInfoProducer.class);

	//private UserGroupCallback callback = new MvelUserGroupCallbackImpl(true);
	private UserGroupCallback callback = new TemporaryMaestroUserGroupCallback();
	//private UserInfo userInfo = new DefaultUserInfo(true);
	
	@Override
	public UserGroupCallback produceCallback() {
		logger.info("&&&&&&&&&&&&&&&&& ==> SELECTING UserGroupCallback Producer and returning TemporaryMaestroUserGroupCallback");

		return callback;
	}

	@Override
	public UserInfo produceUserInfo() {
		logger.info("&&&&&&&&&&&&&&&&& ==> SELECTING UserInfo Producer and returning null");

		return null;
	}

}
