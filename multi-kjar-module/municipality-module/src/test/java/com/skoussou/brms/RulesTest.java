package com.skoussou.brms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.drools.core.command.impl.GenericCommand;
import org.drools.core.command.runtime.BatchExecutionCommandImpl;
import org.drools.core.command.runtime.rule.AgendaGroupSetFocusCommand;
import org.drools.core.command.runtime.rule.InsertObjectCommand;
import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieScanner;
import org.kie.api.builder.ReleaseId;
import org.kie.api.command.BatchExecutionCommand;
import org.kie.api.command.Command;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.internal.command.CommandFactory;

import com.skoussou.brms.util.LoggingAgendaEventListener;
import com.skoussou.brms.util.RulesFiredListernerHelper;

import com.skoussou.brms.Question;
import com.skoussou.brms.BRMSEngineResults;

public class RulesTest {

	private static StatelessKieSession kSession;
	//private static KieSession kSession;


	/*
	 * Setup KieBase
	 */
	//@BeforeClass
	public static void setup(String kieBaseName) {
		KieServices ks = KieServices.Factory.get();
		KieContainer kContainer = ks.getKieClasspathContainer();
		// Stateful KieSession
		//kSession = kContainer.getKieBase("localKBase").newKieSession();

		
		//kSession = kContainer.newStatelessKieSession();
		kSession = kContainer.getKieBase(kieBaseName).newStatelessKieSession();
		kSession.addEventListener(new LoggingAgendaEventListener());
	}
	
	@Test
	public void testRules_WithLocality_Building() {
		setup("localLawsKBase");

		BuildingStructureActivity activity = new BuildingStructureActivity(ActivityType.BUILDING_STRUCTURE, 
				ActionType.BUILDING, new Size(101));

		// Stateless Session with Commands API
		List<GenericCommand<?>> commandList = new ArrayList<GenericCommand<?>>();
		commandList.add(new InsertObjectCommand(activity));
		commandList.add(new InsertObjectCommand(new ArrayList()));
		//commandList.add(new AgendaGroupSetFocusCommand("bulding-activity-applicability"));
		commandList.add(new AgendaGroupSetFocusCommand("applicability"));
		BatchExecutionCommand batchCommand = new BatchExecutionCommandImpl(commandList);
		kSession.execute(batchCommand);
		
		// Statefull Session API
//		kSession.insert(activity);
//		//kSession.insert(buildingsize);
//		kSession.getAgenda().getAgendaGroup("bulding-activity-applicability").setFocus();
//		kSession.fireAllRules();


	}
	
	@Test
	public void testRules_WithLocality_Multiples() {
		setup("localLawsKBase");

		BuildingStructureActivity activityBuildings = new BuildingStructureActivity(ActivityType.BUILDING_STRUCTURE, 
				ActionType.BUILDING, new Size(51));
		ExploitingMarinaActivity activityMoorings = new ExploitingMarinaActivity(ActivityType.EXPLOITING_MARINA, 
				null, new Moorings(150, 0));		

		// Stateless Session with Commands API
		List<GenericCommand<?>> commandList = new ArrayList<GenericCommand<?>>();
		commandList.add(new InsertObjectCommand(activityBuildings));
		commandList.add(new InsertObjectCommand(activityMoorings));

		commandList.add(new AgendaGroupSetFocusCommand("applicability"));
		BatchExecutionCommand batchCommand = new BatchExecutionCommandImpl(commandList);
		kSession.execute(batchCommand);
		
		// Statefull Session API
//		kSession.insert(activity);
//		//kSession.insert(buildingsize);
//		kSession.getAgenda().getAgendaGroup("bulding-activity-applicability").setFocus();
//		kSession.fireAllRules();
	}
	
	@Test
	public void testRules_WithLocality_Multiples_withExecResults() {
		setup("localLawsKBase");

		BuildingStructureActivity activityBuildings = new BuildingStructureActivity(ActivityType.BUILDING_STRUCTURE, 
				ActionType.BUILDING, new Size(51));
		ExploitingMarinaActivity activityMoorings = new ExploitingMarinaActivity(ActivityType.EXPLOITING_MARINA, 
				null, new Moorings(150, 0));		


		
		// Stateless Session with Commands API      
        List<Command<?>> commands = new ArrayList<Command<?>>();
        commands.add(new InsertObjectCommand(activityBuildings));
        commands.add(new InsertObjectCommand(activityMoorings));
        commands.add(new AgendaGroupSetFocusCommand("applicability"));
        commands.add(CommandFactory.newInsert(new BRMSEngineResults(), "engineResults"));
        BatchExecutionCommand cmds = CommandFactory.newBatchExecution( commands );	
		
		ExecutionResults results = kSession.execute(cmds);
		//HashMap<String, Object> factIds = (HashMap<String, Object>) results.getIdentifiers();
		Collection<String> identifiers = results.getIdentifiers();
		System.out.println("< *************************** >");
//		identifiers.forEach(id -> System.out.println(id + ":" + results.getFactHandle(id)));
		//identifiers.forEach(id -> System.out.println(id + " : " + ((BRMSEngineResults) results.getValue(id)).toString()));
		for (String id : identifiers) {
			BRMSEngineResults brmsREsult = (BRMSEngineResults) results.getValue(id);
			System.out.println(id + " : " + brmsREsult.toString());
		}

		//factIds.forEach(id -> System.out.println(id));
		
		// Statefull Session API
//		kSession.insert(activity);
//		//kSession.insert(buildingsize);
//		kSession.getAgenda().getAgendaGroup("bulding-activity-applicability").setFocus();
//		kSession.fireAllRules();


	}
	
	@Test
	public void testRules_determineQuestions() {
		setup("localLawsKBase");

		String go = new String("5");
		Question q1 = new Question("Questions", "Marina OffersOfferings");
		Question q2 = new Question("Questions", "Length is more than 4 meters");
		Question q3 = new Question("Questions", "Intended Commercial Use");
		Question q4 = new Question("Intended Commercial Use", "APPLICABILITY For MARINA");
		Question q5 = new Question("Length is more than 4 meters", "APPLICABILITY For MARINA");
		Question q6 = new Question("Marina OffersOfferings", "APPLICABILITY For MARINA");

		// Stateless Session with Commands API
		List<GenericCommand<?>> commandList = new ArrayList<GenericCommand<?>>();
		commandList.add(new InsertObjectCommand("5"));
		commandList.add(new InsertObjectCommand(go));
		commandList.add(new InsertObjectCommand(q1));
		commandList.add(new InsertObjectCommand(q2));
		commandList.add(new InsertObjectCommand(q3));
		commandList.add(new InsertObjectCommand(q4));
		commandList.add(new InsertObjectCommand(q5));
		commandList.add(new InsertObjectCommand(q6));

		//commandList.add(new InsertObjectCommand(new ArrayList()));
		commandList.add(new AgendaGroupSetFocusCommand("questiions-marina-exploitation"));
		BatchExecutionCommand batchCommand = new BatchExecutionCommandImpl(commandList);
		kSession.execute(batchCommand);
		
		// Statefull Session API
//		kSession.insert(activity);
//		//kSession.insert(buildingsize);
//		kSession.getAgenda().getAgendaGroup("bulding-activity-applicability").setFocus();
//		kSession.fireAllRules();


	}

}
