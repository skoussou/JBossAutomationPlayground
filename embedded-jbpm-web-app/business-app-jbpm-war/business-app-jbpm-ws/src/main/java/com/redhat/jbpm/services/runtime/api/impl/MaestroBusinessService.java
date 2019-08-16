package com.redhat.jbpm.services.runtime.api.impl;

import javax.interceptor.Interceptors;

import com.redhat.jbpm.config.TemporaryMaestroUserGroupCallbackInterceptorImpl;

//@Interceptors({ LogInterceptorImpl.class, MaestroUserGroupCallbackInterceptorImpl.class })
@Interceptors({ TemporaryMaestroUserGroupCallbackInterceptorImpl.class })
public class MaestroBusinessService {

}
