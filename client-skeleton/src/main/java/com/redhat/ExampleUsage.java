package com.redhat;

public class ExampleUsage extends AbstractKieServerConnector {

	public static void main(String[] args) {

                System.setProperty("org.kie.server.bypass.auth.user", "true");
		ExampleUsage client = new ExampleUsage();
		//client.getProcessClient().startProcess("CONTAINER_ID", "PROCESS_ID");

                System.out.println("==========================================================================================");
                System.out.println("About to claim TASK [3] in KieContainer ==> mortgage-process_1.0.8-SNAPSHOT logged in as [pamAdmin] on behalf of [brokerUser]");
		String bypassSetting = System.getProperty("org.kie.server.bypass.auth.user");
                System.out.println("Checking org.kie.server.bypass.auth.user="+bypassSetting+" was correctly set to true");
                System.out.println("==========================================================================================");

                client.getTaskClient().claimTask("mortgage-process_1.0.8-SNAPSHOT", 3L, "brokerUser");
	}

}
