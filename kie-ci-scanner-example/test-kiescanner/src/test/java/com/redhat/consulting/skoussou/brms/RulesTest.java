package com.redhat.consulting.skoussou.brms;

import java.util.ArrayList;
import java.util.List;

import org.jboss.brms.fact.LocationFact;
import org.jboss.brms.fact.PersonFact;
import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieScanner;
import org.kie.api.builder.ReleaseId;
import org.kie.api.command.Command;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.internal.command.CommandFactory;

public class RulesTest {

	private static KieContainer kieContainer = null;
	private static KieContainer kieContainerSim = null;

	private static ReleaseId releaseId;
	private static ReleaseId releaseIdSim;

	private static KieScanner kScanner;
	private static KieScanner kScannerSim;

	@BeforeClass
	public static void setup() {
		// create a kie service where you will include your container
		KieServices kieServices = KieServices.Factory.get();

		// newReleaseId parameters groupId, artifactId, versionId
		releaseId = kieServices.newReleaseId("org.jboss.pmenon.brms", "simple-rules", "LATEST");
		releaseIdSim = kieServices.newReleaseId("org.jboss.pmenon.brms", "simple-rules-sim", "LATEST");

		// create a kie container based in the latest kjar version 
		kieContainer = kieServices.newKieContainer(releaseId);
		kieContainerSim = kieServices.newKieContainer(releaseIdSim);

		kScanner = kieServices.newKieScanner(kieContainer);
		kScannerSim = kieServices.newKieScanner(kieContainerSim);
		// this will create the kie container on demand 
//		kScanner.scanNow();
	}

	@Test
	public void shouldfireNoRules() throws InterruptedException {
        System.out.println("Starting Rules");
        for (int i = 0; i < 100; i++) {

            kScanner.scanNow();
            kScannerSim.scanNow();

//            if (i % 2 == 0) {
            	System.out.println("Scanner active - firing ["+i+"] round of tests");
            	
            	System.out.println("RELEASE RULES EXECUTION");
            	KieBase kbase = kieContainer.getKieBase();
            	KieSession ksession = kbase.newKieSession();
//            	ksession.insert("test");
//            	ksession.fireAllRules();
            	fireRule(ksession);
            	System.out.println("-----------------------------------------------------------");            	
            	System.out.println("SIMULATION RULES EXECUTION");
            	KieBase kbaseSim = kieContainerSim.getKieBase();
            	KieSession ksessionSim = kbaseSim.newKieSession();
//            	ksessionSim.insert("test");
//            	ksessionSim.fireAllRules();
            	fireRule(ksessionSim);
            	System.out.println("============================================================");            	
            	
//            }

            Thread.sleep(10000);
        }
        System.out.println("Ending Rules");
	}
	
	private void fireRule(KieSession kieSession) {
	// this will create a stateless kie session 
//			StatelessKieSession kieSession = kieContainer.newStatelessKieSession();

			RulesFiredListernerHelper rulesFiredListener = new RulesFiredListernerHelper();
			kieSession.addEventListener(rulesFiredListener);

			PersonFact person = new PersonFact();
			person.setName("Paulo Menon");
//			person.setAge(20);
//			LocationFact location = new LocationFact();
//			location.setPostcode("GU1");

			// use of command interface is recommend to insert more than one fact objects 
			List<Command> commandList = new ArrayList<>();

			commandList.add(CommandFactory.newInsert(person));
//			commandList.add(CommandFactory.newInsert(location));
			
			// execute method will fire all rules automatic for you
//			kieSession.execute(CommandFactory.newBatchExecution(commandList));
			kieSession.insert(person);
			kieSession.fireAllRules();

//			Assert.assertEquals("One rule should be fired! ", 1, rulesFiredListener.getNrOfRulesFired());
}
	
//	@Test
//	public void shouldFiredSecondRule() {
//		// this will create a stateless kie session 
//		StatelessKieSession kieSession = kieContainer.newStatelessKieSession();
//
//		RulesFiredListernerHelper rulesFiredListener = new RulesFiredListernerHelper();
//		kieSession.addEventListener(rulesFiredListener);
//
//		PersonFact person = new PersonFact();
//		person.setName("Joao");
//		person.setAge(20);
//		LocationFact location = new LocationFact();
//		location.setPostcode("GU1");
//
//		// use of command interface is recommend to insert more than one fact objects 
//		List<Command> commandList = new ArrayList<>();
//
//		commandList.add(CommandFactory.newInsert(person));
//		commandList.add(CommandFactory.newInsert(location));
//		
//		// execute method will fire all rules automatic for you
//		kieSession.execute(CommandFactory.newBatchExecution(commandList));
//
//		Assert.assertEquals("One rule should be fired! ", 1, rulesFiredListener.getNrOfRulesFired());
//	}
//	

//
//	@SuppressWarnings("unchecked")
//	@Test
//	public void shouldFiredFistRule() {
//		// first-rule
//		StatelessKieSession kieSession = kieContainer.newStatelessKieSession();
//
//		RulesFiredListernerHelper rulesFiredListener = new RulesFiredListernerHelper();
//		kieSession.addEventListener(rulesFiredListener);
//
//		PersonFact person = new PersonFact();
//		person.setName("Paulo Menon");
//
//		kieSession.execute(CommandFactory.newInsert(person));
//
//		Assert.assertEquals("first-rule", rulesFiredListener.getRulesFiredName());
//	}
//
//	@Test
//	public void NoRulesFired() {
//		StatelessKieSession kieSession = kieContainer.newStatelessKieSession();
//
//		RulesFiredListernerHelper rulesFiredListener = new RulesFiredListernerHelper();
//		kieSession.addEventListener(rulesFiredListener);
//		
//		PersonFact person = new PersonFact();
//		person.setName("Mario");
//		
//		kieSession.execute(CommandFactory.newInsert(person));
//		
//		
//		Assert.assertEquals("No rule should be fired! ", 0, rulesFiredListener.getNrOfRulesFired());
//	}
	
	

}
