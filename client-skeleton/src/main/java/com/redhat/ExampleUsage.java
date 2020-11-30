package com.redhat;


import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Arrays;


//import org.kie.server.api.model.definition.QueryParam;
//import static org.kie.server.api.model.definition.QueryParam.list;
//import static org.kie.server.api.model.definition.QueryParam.equalsTo;
//import org.jbpm.services.api.query.model.QueryParam;

//import static org.jbpm.services.api.query.model.QueryParam.list;
//import static org.jbpm.services.api.query.model.QueryParam.equalsTo;
import org.jbpm.services.api.model.UserTaskInstanceWithVarsDesc;
import org.kie.server.api.model.definition.QueryFilterSpec;
import org.kie.server.api.model.definition.SearchQueryFilterSpec;
//import org.kie.server.api.model.definition.SearchQueryFilterSpec;

import org.kie.server.api.model.definition.QueryParam;
import static org.kie.server.api.util.QueryParamFactory.equalsTo;
import static org.kie.server.api.util.QueryParamFactory.list;
import static org.kie.server.api.util.QueryParamFactory.onlyActiveTasks;
import static org.kie.server.api.util.QueryParamFactory.onlyCompletedTasks;


public class ExampleUsage extends AbstractKieServerConnector {

    // mvn clean compile exec:java -Dexec.mainClass="com.redhat.ExampleUsage"
    public static void main(String[] args) {

        System.setProperty("org.kie.server.bypass.auth.user", "true");
        ExampleUsage client = new ExampleUsage();

        // Getting Running Containers
        //listRunningProjects(client);

        // Getting Running Containers
        //listProcesses(client, "stelios-retail-credit");

        // Retrieve Process Definition Details (roles, variables, nodes)
        //getProcessDefinition(client, "stelios-retail-credit", "SteliosRetailCredit.LoansApplicationHT");

        // Start new Process Instance (no variables)
        //startProcess(client, "stelios-retail-credit", "SteliosRetailCredit.LoansApplicationHT");
        //Map<String, Object> processVars = new HashMap<String, Object>(){{put("pHumanEyeGroup","hr");}};
        //startProcess(client, "stelios-retail-credit", "SteliosRetailCredit.LoansApplicationHT", processVars);


        //Find Active Tasks by Task Value
        //listActiveHTByVariables(client, null);
        listCompletedHTByVariables(client, null);
        //listHTByVariablesValues(client, null);

        //addPotOwnerOfTask(client, "stelios-retail-credit", 69L, "brokerUser", null);
        //addPotOwnerOfTask(client, "stelios-retail-credit", 69L, null, "broker");


//               String bypassSetting = System.getProperty("org.kie.server.bypass.auth.user");
//               System.out.println("Checking org.kie.server.bypass.auth.user="+bypassSetting+" was correctly set to true");
//               claimTask(client, "stelios-retail-credit", 73L, "hrUser");

        // PART - 1
        //client.getProcessClient().startProcess("CONTAINER_ID", "PROCESS_ID");

        // PART - 2
        // System.out.println("==========================================================================================");
        // System.out.println("About to claim TASK [3] in KieContainer ==> mortgage-process_1.0.8-SNAPSHOT logged in as [pamAdmin] on behalf of [brokerUser]");
        // String bypassSetting = System.getProperty("org.kie.server.bypass.auth.user");
        // System.out.println("Checking org.kie.server.bypass.auth.user="+bypassSetting+" was correctly set to true");
        // System.out.println("==========================================================================================");

        // client.getTaskClient().claimTask("mortgage-process_1.0.8-SNAPSHOT", 3L, "brokerUser");


    }

    private static void listRunningProjects(ExampleUsage client) {
        System.out.println("================== LIST OF CONTAINERS ========================================================================");
        System.out.println(client.getServicesClient().listContainers());
    }

    private static void listProcesses(ExampleUsage client, String containerId) {
        System.out.println("================== LIST OF PROCESSES ========================================================================");
        client.getQueryClient().findProcessesByContainerId(containerId, 0, 10);
    }

    // Process queries in API
    private static void listActiveHTByVariables(ExampleUsage client, Map<String, String> vars) {
        System.out.println("================== PROCESS QUERIES - [ACTIVE] TASKS BY VARS ========================================================================");        org.kie.server.api.model.definition.SearchQueryFilterSpec spec = new org.kie.server.api.model.definition.SearchQueryFilterSpec();
        //spec.setAttributesQueryParams(list(org.kie.server.api.util.QueryParamFactory.onlyActiveTasks(), equalsTo(PROCESS_ATTR_DEFINITION_ID, PROCESS_ID_USERTASK)));

        List<QueryParam> taskVariables = list(onlyActiveTasks(), equalsTo("tImportantVarIn", "Level-3"));
        //spec.setTaskVariablesQueryParams(list(org.kie.server.api.util.QueryParamFactory.onlyActiveTasks(), equalsTo("tExpenseFormCorrelationKey", "expenseForm-050")));
        //List<org.jbpm.services.api.query.model.QueryParam> taskVariables = list(equalsTo("tExpenseFormCorrelationKey", "expenseForm-050"));
        spec.setTaskVariablesQueryParams(taskVariables);
        client.getQueryClient().queryUserTaskByVariables(spec, 0, 10);
    }

    // Process queries in API
    private static void listCompletedHTByVariables(ExampleUsage client, Map<String, String> vars) {

        System.out.println("================== PROCESS QUERIES - [COMPLETED] TASKS BY VARS ========================================================================");

        org.kie.server.api.model.definition.SearchQueryFilterSpec spec = new org.kie.server.api.model.definition.SearchQueryFilterSpec();
        //spec.setAttributesQueryParams(list(org.kie.server.api.util.QueryParamFactory.onlyActiveTasks(), equalsTo(PROCESS_ATTR_DEFINITION_ID, PROCESS_ID_USERTASK)));

        List<QueryParam> taskVariables = list(onlyCompletedTasks(), equalsTo("tImportantVarIn", "Level-3"));
        //spec.setTaskVariablesQueryParams(list(org.kie.server.api.util.QueryParamFactory.onlyActiveTasks(), equalsTo("tExpenseFormCorrelationKey", "expenseForm-050")));
        //List<org.jbpm.services.api.query.model.QueryParam> taskVariables = list(equalsTo("tExpenseFormCorrelationKey", "expenseForm-050"));
        spec.setTaskVariablesQueryParams(taskVariables);
        client.getQueryClient().queryUserTaskByVariables(spec, 0, 10);
    }

    private static void listHTByVariablesValues(ExampleUsage client, Map<String, String> vars) {
        System.out.println("================== CUSTOM QUERIES - TASKS BY VARS VALUE ========================================================================");
//        org.kie.server.api.model.definition.SearchQueryFilterSpec spec = new org.kie.server.api.model.definition.SearchQueryFilterSpec();
//        //spec.setAttributesQueryParams(list(org.kie.server.api.util.QueryParamFactory.onlyActiveTasks(), equalsTo(PROCESS_ATTR_DEFINITION_ID, PROCESS_ID_USERTASK)));
//
//        List<QueryParam> taskVariables = list(onlyCompletedTasks(), equalsTo("tImportantVarIn", "Level-3"));
//        //spec.setTaskVariablesQueryParams(list(org.kie.server.api.util.QueryParamFactory.onlyActiveTasks(), equalsTo("tExpenseFormCorrelationKey", "expenseForm-050")));
//        //List<org.jbpm.services.api.query.model.QueryParam> taskVariables = list(equalsTo("tExpenseFormCorrelationKey", "expenseForm-050"));
//        spec.setTaskVariablesQueryParams(taskVariables);

         /*<T> List<T> query(String queryName, String mapper, Integer page, Integer pageSize, Class<T> resultType);
         <T> List<T> query(String queryName, String mapper, String orderBy, Integer page, Integer pageSize, Class<T> resultType);
         <T> List<T> query(String queryName, String mapper, QueryFilterSpec filterSpec, Integer page, Integer pageSize, Class<T> resultType);
         <T> List<T> query(String queryName, String mapper, String builder, Map<String, Object> parameters, Integer page, Integer pageSize, Class<T> resultType);
         <T> List<T> query(String containerId, String queryName, String mapper, String builder, Map<String, Object> parameters, Integer page, Integer pageSize, Class<T> resultType);
*/
        client.getQueryClient().query("jbpmHumanTasksWithVariables", "UserTasksWithVariables", 0, 10, UserTaskInstanceWithVarsDesc.class);
    }

    private static void getProcessDefinition(ExampleUsage client, String containerId, String processId) {
        System.out.println("================== LIST OF PROCESSES ========================================================================");
        client.getProcessClient().getProcessDefinition(containerId, processId);

    }

    private static void startProcess(ExampleUsage client, String containerId, String processId) {
        System.out.println("================== START INSTANCE of PROCESS [" + processId + "] in BUSINESS PROJECT[" + containerId + "] ========================================================================");
        client.getProcessClient().startProcess(containerId, processId);
    }

    private static void startProcess(ExampleUsage client, String containerId, String processId, Map<String, Object> variables) {
        System.out.println("================== START INSTANCE of PROCESS [" + processId + "] in BUSINESS PROJECT[" + containerId + "] ========================================================================");
        client.getProcessClient().startProcess(containerId, processId, variables);
    }


    private static void addPotOwnerOfTask(ExampleUsage client, String containerId, Long taskId, String userId, String ownerGroup) {
        List<String> owners = Arrays.asList(ownerGroup);
        System.out.println("================== Adding POT Owner User [" + userId + "] and/or group [" + ownerGroup + "] for Task [" + containerId + "->" + taskId + "] ========================================================================");
        client.getTaskClient().nominateTask(containerId, taskId, userId, owners);
    }

    private static void claimTask(ExampleUsage client, String containerId, Long taskId, String userId) {
        System.out.println("================== CLAIM TASK [" + taskId + "] as [" + userId + "] ========================================================================");
        client.getTaskClient().claimTask(containerId, taskId, userId);
    }

}
