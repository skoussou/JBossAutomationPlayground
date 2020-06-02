package com.redhat;

import java.util.HashMap;
import java.util.Map;
import org.jbpm.process.workitem.core.AbstractLogOrThrowWorkItemHandler;
import org.jbpm.process.workitem.core.util.RequiredParameterValidator;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;
import org.jbpm.process.workitem.core.util.Wid;
import org.jbpm.process.workitem.core.util.WidParameter;
import org.jbpm.process.workitem.core.util.WidResult;
import org.jbpm.process.workitem.core.util.service.WidAction;
import org.jbpm.process.workitem.core.util.service.WidAuth;
import org.jbpm.process.workitem.core.util.service.WidService;
import org.jbpm.process.workitem.core.util.WidMavenDepends;

@Wid(widfile="DMNRemoteRestDecisionWorkItemDefinitions.wid", name="DMNRemoteRestDecisionWorkItemDefinitions",
        displayName="DMNRemoteRestDecisionWorkItemDefinitions",
        defaultHandler="mvel: new com.redhat.DMNRemoteRestDecisionWorkItemWorkItemHandler()",
        documentation = "dmn-remote-decision-wih/index.html",
        category = "dmn-remote-decision-wih",
        icon = "DMNRemoteRestDecisionWorkItemDefinitions.png",
        parameters={
            @WidParameter(name="SampleParam", required = true),
            @WidParameter(name="SampleParamTwo", required = true)
        },
        results={
            @WidResult(name="SampleResult")
        },
        mavenDepends={
            @WidMavenDepends(group="com.redhat", artifact="dmn-remote-decision-wih", version="7.30.0.Final-redhat-00003")
        },
        serviceInfo = @WidService(category = "dmn-remote-decision-wih", description = "${description}",
                keywords = "",
                action = @WidAction(title = "Sample Title"),
                authinfo = @WidAuth(required = true, params = {"SampleParam", "SampleParamTwo"},
                        paramsdescription = {"SampleParam", "SampleParamTwo"},
                        referencesite = "referenceSiteURL")
        )
)
public class DMNRemoteRestDecisionWorkItemWorkItemHandler extends AbstractLogOrThrowWorkItemHandler {

    public void executeWorkItem(WorkItem workItem,
                                WorkItemManager manager) {
        try {
            RequiredParameterValidator.validate(this.getClass(),
               workItem);

            // sample parameters
            String sampleParam = (String) workItem.getParameter("SampleParam");
            String sampleParamTwo = (String) workItem.getParameter("SampleParamTwo");

            // complete workitem impl...

            // return results
            String sampleResult = "sample result";
            Map<String, Object> results = new HashMap<String, Object>();
            results.put("SampleResult", sampleResult);


            manager.completeWorkItem(workItem.getId(), results);
        } catch(Throwable cause) {
            handleException(cause);
        }
    }

    @Override
    public void abortWorkItem(WorkItem workItem,
                              WorkItemManager manager) {
        // stub
    }
}


