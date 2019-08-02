package com.redhat.jbpm.config;

import java.util.List;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;


/**
 * Interceptor pour la gestion du MaestroUserGroupCallback intervenant pour les méthodes utilisant le callback Maestro --> les 2 derniers
 * arguments de la méthode doivent être l'idDemandeur et la liste des groupes du demandeur
 *
 * @author plvignot
 *
 */
@Interceptor
@MaestroUserGroupCallBackInterceptor
public class TemporaryMaestroUserGroupCallbackInterceptorImpl {

	@Inject
	private TemporaryMaestroUserGroupCallback userGroupStorage;
	
    /**
     * Méthode principale de l'interceptor
     *
     * @param pContext
     * @return
     * @throws Exception
     */
    // CHECKSTYLE:OFF Illegal Throws Justification: signature de l'intercepteur
    @AroundInvoke
    public Object gestionMaestroUserGroupCallback(
            final InvocationContext pContext) throws Exception {// NOPMD
        // Justification
        // :
        // signature de
        // l'intercepteur
        // CHECKSTYLE:ON Illegal Throws
        final Object returnObject;
        if (pContext.getParameters().length >= 2) {
            returnObject = methodCallWithMaestroUserGroupCallback(pContext);
        } else {
            returnObject = pContext.proceed();
        }
        return returnObject;
    }

    /**
     * Methode alimentant et nettoyant le MaestroUserGroupCallBackStorage avant et après l'appel d'une méthode pContext.proceed
     *
     * @param pContext
     * @return
     * @throws Exception
     */
    // CHECKSTYLE:OFF Illegal Throws Justification: signature de l'intercepteur
    @SuppressWarnings("unchecked")
    private Object methodCallWithMaestroUserGroupCallback(
            final InvocationContext pContext) throws Exception { // NOPMD

        final int index = pContext.getParameters().length - 2;
        final String userId = (String) pContext.getParameters()[index];

        final List<String> groupIds = (List<String>) pContext.getParameters()[pContext.getParameters().length - 1];


        // Justification
        // :
        // signature de
        // l'intercepteur
        // CHECKSTYLE:ON Illegal Throws
//        MaestroUserGroupCallbackStorage.pushThreadUserGroups(userId, groupIds);
        
        userGroupStorage.pushThreadUserGroups(userId, groupIds);
        
        final Object returnObject = pContext.proceed();
        userGroupStorage.cleanThreadUserGroups();
        return returnObject;
    }
}
