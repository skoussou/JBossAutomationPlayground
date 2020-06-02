package com.redhat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kie.api.KieServices;
import org.kie.api.command.BatchExecutionCommand;
import org.kie.api.command.Command;
import org.kie.api.command.KieCommands;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNResult;
import org.kie.internal.runtime.Cacheable;
import org.kie.server.api.exception.KieServicesException;
import org.kie.server.api.marshalling.MarshallingFormat;
import org.kie.server.api.model.ServiceResponse;
import org.kie.server.client.DMNServicesClient;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.KieServicesConfiguration;
import org.kie.server.client.KieServicesFactory;
import org.kie.server.client.RuleServicesClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Work item handler to support remote execution of DMN Decisions via KIE Server. <br/>
 *
 * Following is a list of supported data inputs:
 * <ul>
 *     <li>Language - DMN (optional and defaults to DRL)</li>
 *     <li>ContainerId - container id (or alias) to be targeted on remote KIE Server - mandatory</li>
 * </ul>
Following are data inputs specific to DMN:
 * <ul>
 *     <li>Namespace - DMN namespace to be used - mandatory</li>
 *     <li>Model - DMN model to be used - mandatory</li>
 *     <li>Decision - DMN decision name to be used - optional</li>
 * </ul>
 *
 * All other data inputs will be used as facts inserted into decision service.<br/>
 * Results returned will be then put back into the data outputs. <br/>
 * <br/>
 * DMN handling receives all data from DMNResult.<br/>
 */
public class RemoteDMNTaskHandler implements Cacheable, WorkItemHandler {

    private static final Logger logger = LoggerFactory.getLogger(RemoteDMNTaskHandler.class);

    protected static final String DMN_LANG = "DMN";

    protected KieCommands commandsFactory = KieServices.get().getCommands();
    private KieServicesClient client;

    public RemoteDMNTaskHandler(String serverUrl, String userName, String password, ClassLoader classLoader) {
        // expand from system property if given otherwise use the same value
        serverUrl = System.getProperty(serverUrl, serverUrl);
        logger.debug("KieServerClient configured for server url(s) {} and username {}", serverUrl, userName);
        KieServicesConfiguration configuration = KieServicesFactory.newRestConfiguration(serverUrl, userName, password);
        configuration.setMarshallingFormat(MarshallingFormat.XSTREAM);
        this.client =  KieServicesFactory.newKieServicesClient(configuration, classLoader);
    }

    @Override
    public void executeWorkItem(WorkItem workItem, WorkItemManager workItemManager) {
        Map<String, Object> parameters = new HashMap<>(workItem.getParameters());
        String containerId = (String) parameters.remove("ContainerId");
        if (containerId == null || containerId.isEmpty()) {
            throw new IllegalArgumentException("Container ID is required for remote BusinessRuleTask");
        }

        String language = (String) parameters.remove("Language");
        if (language == null) {
            language = DMN_LANG;
        }


        Map<String, Object> results = new HashMap<>();

        logger.info("Facts to be inserted into working memory {}", parameters);

        if (DMN_LANG.equalsIgnoreCase(language)) {
            String namespace = (String) parameters.remove("Namespace");
            String model = (String) parameters.remove("Model");
            String decision = (String) parameters.remove("Decision");
            DMNServicesClient dmnClient = client.getServicesClient(DMNServicesClient.class);

            DMNContext dmnContext = dmnClient.newContext();

            for (Map.Entry<String, Object> entry : parameters.entrySet()) {
               // Trying to cast to the correct type 
               Class<? extends Object> objectclass = entry.getValue().getClass();
                //dmnContext.set(entry.getKey(), entry.getValue());
                dmnContext.set(entry.getKey(), objectclass.cast(entry.getValue()));
            }

            ServiceResponse<DMNResult> evaluationResult = null;
            if (decision != null) {
                evaluationResult = dmnClient.evaluateDecisionByName(containerId, namespace, model, decision, dmnContext);
            } else {
                evaluationResult = dmnClient.evaluateAll(containerId, namespace, model, dmnContext);
            }

            DMNResult dmnResult = evaluationResult.getResult();

            results.putAll(dmnResult.getContext().getAll());
        } else {
            throw new IllegalArgumentException("Not supported language type " + language);
        }
        logger.debug("Facts retrieved from working memory {}", results);
        workItemManager.completeWorkItem(workItem.getId(), results);

    }

    @Override
    public void abortWorkItem(WorkItem workItem, WorkItemManager workItemManager) {
        // no-op
    }

    @Override
    public void close() {

    }

}