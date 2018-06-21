package com.skoussou.brms;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.core.command.assertion.AssertEquals;
import org.drools.core.command.impl.GenericCommand;
import org.drools.core.command.runtime.BatchExecutionCommandImpl;
import org.drools.core.command.runtime.rule.InsertObjectCommand;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.command.BatchExecutionCommand;
import org.kie.api.conf.EqualityBehaviorOption;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.internal.command.CommandFactory;

/**
 * 
 * @author stkousso@redhat.com
 *
 */
public class KJARBuilder {

	private KieServices kieServices;
	public KieServices getKieServices() {
		return kieServices;
	}


	public void setKieServices(KieServices kieServices) {
		this.kieServices = kieServices;
	}

	private HashMap<String, KieContainer> containerMap = new HashMap<String, KieContainer>();

	protected static final String KJAR_RESOURCES_PATH = "src/main/resources/";
	protected static final String KJAR_DROOLS_DEFAULT_NAMESPACE_PATH = "com/skoussou/brms/";
	protected static final String KJAR_DROOLS_LOCAL_NAMESPACE_PATH = "local/hague/";
	protected static final String KJAR_DROOLS_CENTRAL_NAMESPACE_PATH = "central/";
	protected static final String KJAR_DROOLS_PROVINCE_WEST_NAMESPACE_PATH = "province/west";

	protected static final String HAGUE_KIEBASE_NAME = "theHagueKBase";
	protected static final String CENTRAL_KIEBASE_NAME = "centralKBase";
	protected static final String WEST_PROVINCE_KIEBASE_NAME = "westProvinceKBase";

	
	protected static final String DROOLS_DEFAULT_PACKAGE_NAME = "com.skoussou.brms";
	protected static final String DROOLS_CENTRAL_PACKAGE_NAME = "com.skoussou.brms.central";
	protected static final String DROOLS_WEST_PROVINCE_PACKAGE_NAME = "com.skoussou.brms.province.west";
	protected static final String DROOLS_HAGUE_LOCAL_PACKAGE_NAME = "com.skoussou.brms.local.hague";
	
	protected static final String COLON = ":";
	protected static final String KSESSION_TAG = "ksession";

	public static void main(String[] args) {	
		KJARBuilder kjarBuilder = new KJARBuilder();
			
		// Create CENTRAL KJAR non-KieContainer
		String GAVCENTRAL = "com.skoussou.brms:automated-central-kjar:1.0.0";
		CustomKBase centralKBase = kjarBuilder.new CustomKBase(CENTRAL_KIEBASE_NAME, Arrays.asList(DROOLS_CENTRAL_PACKAGE_NAME), null, false, true);
		List dependencies = new ArrayList<String>();
		Map resources = new HashMap<String, String>();
		resources.put("drl0.drl", getCentralRule(DROOLS_CENTRAL_PACKAGE_NAME));
		// kjarBuilder.createNewKJAR(GAVCENTRAL, KJAR_RESOURCES_PATH+KJAR_DROOLS_DEFAULT_NAMESPACE_PATH+KJAR_DROOLS_CENTRAL_NAMESPACE_PATH, dependencies, resources, centralKBase);
		kjarBuilder.createNewKJAR(GAVCENTRAL, "src/main/resources/com/skoussou/brms/central/", dependencies, resources, centralKBase);
//		kjarBuilder.createKieContainerAndExecute(GAVCENTRAL, DROOLS_CENTRAL_PACKAGE_NAME, KSESSION_TAG+CENTRAL_KIEBASE_NAME, "GO");


		
		// Create WEST PROVINCE KJAR non-KieContainer
		String GAVPROVINCE = "com.skoussou.brms:automated-province-kjar:1.0.0";
		CustomKBase provinceKBase = kjarBuilder.new  CustomKBase(WEST_PROVINCE_KIEBASE_NAME, Arrays.asList(DROOLS_WEST_PROVINCE_PACKAGE_NAME), null, false, true);
		List provicnceDependencies = new ArrayList<String>();
		Map provicnceResources = new HashMap<String, String>();
		provicnceResources.put("drl0.drl", getProvinceRule(DROOLS_WEST_PROVINCE_PACKAGE_NAME));

		// kjarBuilder.createNewKJAR(GAVPROVINCE, KJAR_RESOURCES_PATH+KJAR_DROOLS_DEFAULT_NAMESPACE_PATH+KJAR_DROOLS_PROVINCE_WEST_NAMESPACE_PATH, provicnceDependencies, provicnceResources, provinceKBase);
		kjarBuilder.createNewKJAR(GAVPROVINCE, "src/main/resources/com/skoussou/brms/province/west/", provicnceDependencies, provicnceResources, provinceKBase);
//		kjarBuilder.createKieContainerAndExecute(GAVPROVINCE, DROOLS_WEST_PROVINCE_PACKAGE_NAME, KSESSION_TAG+WEST_PROVINCE_KIEBASE_NAME, "GO");

		
		
		// Create WEST PROVINCE KJAR non-KieContainer
		String GAVLOCAL = "com.skoussou.brms:automated-hague-kjar:1.0.0";
		CustomKBase localKBase = kjarBuilder.new  CustomKBase(HAGUE_KIEBASE_NAME, 
				Arrays.asList(DROOLS_HAGUE_LOCAL_PACKAGE_NAME, DROOLS_CENTRAL_PACKAGE_NAME, DROOLS_WEST_PROVINCE_PACKAGE_NAME), 
				//Arrays.asList(DROOLS_HAGUE_LOCAL_PACKAGE_NAME),
				Arrays.asList(CENTRAL_KIEBASE_NAME, WEST_PROVINCE_KIEBASE_NAME),
				false, 
				true);
		List localDependencies = Arrays.asList("com.skoussou.brms:automated-central-kjar:1.0.0", "com.skoussou.brms:automated-province-kjar:1.0.0");
		Map localeResources = new HashMap<String, String>();
		localeResources.put("drl0.drl", getRule(DROOLS_HAGUE_LOCAL_PACKAGE_NAME));

		// kjarBuilder.createNewKJAR(GAVPROVINCE, KJAR_RESOURCES_PATH+KJAR_DROOLS_DEFAULT_NAMESPACE_PATH+KJAR_DROOLS_LOCAL_NAMESPACE_PATH, localDependencies, provilocaleResourcescnceResources, localKBase);
		kjarBuilder.createNewKJAR(GAVLOCAL, "src/main/resources/com/skoussou/brms/local/hague/", localDependencies, localeResources, localKBase);
		kjarBuilder.createKieContainerAndExecute(GAVLOCAL, DROOLS_HAGUE_LOCAL_PACKAGE_NAME, KSESSION_TAG+HAGUE_KIEBASE_NAME, "GO");

		
		
		// OKD WORKING SINGLE KJAR
		//String GAV = "com.skoussou.brms:automated-central-kjar:1.0.0";
		// Create KieContainer
//		kjarBuilder.createNewKieContainer(GAV);
//		// Execute Rules on KieContainer
//		kjarBuilder.executeRulesOnewKieContainer(GAV, "GO");
	}
	
	        
	public int executeRulesOneKieContainer(String GAV, String... facts) {
		KieContainer container = containerMap.get(GAV);

		//	List<GenericCommand<?>> commandList = new ArrayList<GenericCommand<?>>();
		//	Arrays.asList(facts).stream().forEach(fact -> commandList.add(new InsertObjectCommand(fact)));
		//	BatchExecutionCommand batchCommand = new BatchExecutionCommandImpl(commandList);
		KieSession ksession = container.newKieSession( "ksessiontheHagueKBase" );

		ksession.insert( "GO" );
		int rulesFired = ksession.fireAllRules();

		//	ExecutionResults results = ksession.execute(batchCommand);

		ksession.dispose();
		return rulesFired;
	}
	
	public void createKieContainerAndExecute(String GAV, String kBasePackage, String kSessionName, String... facts) {
		
		// KJAR ID
		String[] GAV_DETAILS = GAV.split(COLON);
		ReleaseId releaseId = this.kieServices.newReleaseId(GAV_DETAILS[0], GAV_DETAILS[1], GAV_DETAILS[2]);
		
		//ReleaseId releaseId = kieServices.newReleaseId("com.skoussou.brms","automated-hague-kjar","1.0.0");
		
		KieContainer container = kieServices.newKieContainer(releaseId);

		System.out.println("container created: with kiebase size of: " + container.getKieBaseNames().size());
		System.out.println("container created: with KieBase \""+kSessionName+"\" Packages: " + container.getKieBase().getKiePackages());	
		//System.out.println("container created: with KieBase \""+kSessionName+"\" Rule: " + container.getKieBase().getRule(kBasePackage, "ruleCentral"));
		
		//KieSession ksession = container.newKieSession( "ksessiontheHagueKBase" );
		KieSession ksession = container.newKieSession( kSessionName );
        ksession.insert( "GO" );
        System.out.println("***********************");
        ksession.fireAllRules();
        System.out.println("***********************");

        ksession.dispose();
	}

	
//	public synchronized KieContainer createNewKieContainer(String assessmentIdentifier, String ruleVersion) throws RuleServiceException {
	public synchronized KieContainer createNewKieContainer(String GAV) {
		long startTime = System.currentTimeMillis();

		kieServices = KieServices.Factory.get();

		String[] GAV_DETAILS = GAV.split(COLON);
		
		Map<String, String> kieBases = new HashMap();
		kieBases.put(HAGUE_KIEBASE_NAME, DROOLS_HAGUE_LOCAL_PACKAGE_NAME);//new HashMap( { put(HAGUE_KIEBASE_NAME, DROOLS_HAGUE_LOCAL_PACKAGE_NAME); });
		Map<String, String> ruleFiles = new HashMap();
		ruleFiles.put("first.drl", getRule(DROOLS_HAGUE_LOCAL_PACKAGE_NAME));
		
		//ReleaseId releaseId = kieServices.newReleaseId("com.skoussou.brms", "automated-kjar", "1.0.0");
		ReleaseId releaseId = kieServices.newReleaseId(GAV_DETAILS[0], GAV_DETAILS[1], GAV_DETAILS[2]);
		
		ArrayList<ReleaseId> dependencies = new ArrayList<ReleaseId>();
		dependencies.add(kieServices.newReleaseId("org.kie", "kie-api", "6.3.0.Final-redhat-5"));
		dependencies.add(kieServices.newReleaseId("com.skoussou.brms", "laws-model", "1.0.0"));

		newKieBuilder(kieBases, releaseId, dependencies, ruleFiles);

		//KieModule kmodule = kieServices.getRepository().getKieModule(releaseId);
		KieContainer container = kieServices.newKieContainer(releaseId);
		
		containerMap.put(GAV, container);
		
//		
//		// enable stream mode for CEP
//		final KieBaseConfiguration kieBaseConf = kieServices.newKieBaseConfiguration();
//		kieBaseConf.setOption(eventProcessingMode);
//		
//		LOGGER.debug("container created: with kiebase size of: " + container.getKieBaseNames().size());	
//		LOGGER.info("createContainer:  ... Duration "
//	                + (System.currentTimeMillis()-startTime) + "ms");
//		
//		containerMap.put(cacheKey,container);
//		//TODO implement a proper cache and limit the size
		
		System.out.println("container created: with kiebase size of: " + container.getKieBaseNames().size());
		System.out.println("container created: with KieBase \"theHagueKBase\" Packages: " + container.getKieBase(HAGUE_KIEBASE_NAME).getKiePackages());	
		System.out.println("container created: with KieBase \"theHagueKBase\" Rule: " + container.getKieBase(HAGUE_KIEBASE_NAME).getRule(DROOLS_HAGUE_LOCAL_PACKAGE_NAME, "rule6"));	

		System.out.println("createContainer:  ... Duration " + (System.currentTimeMillis()-startTime) + "ms");
//		
		
		return container;

	}
	
	private void newKieBuilder(Map<String, String> kieBaseDefinitions, ReleaseId kjarGAV, ArrayList<ReleaseId> kjarDependencies, Map<String, String> rulesResources) {

		KieFileSystem kFileSystem = createKieModuleFiles(kieBaseDefinitions, kjarGAV, kjarDependencies, rulesResources);
		//LOGGER.info("KieBuiler building all...");
		KieBuilder kbuilder = kieServices.newKieBuilder(kFileSystem);
		
		
		kbuilder.buildAll();
		
		if (kbuilder.getResults().hasMessages(Message.Level.ERROR)) {
			System.out.println("Build time ERRORS: " + kbuilder.getResults().toString());			
		}

//		LOGGER.info("KieBuilder all sucessful: \n" + kbuilder.getResults().toString());
		System.out.println("KieBuilder run for "+kbuilder.getKieModule().getReleaseId());
		System.out.println("KieBuilder all sucessful: \n" + kbuilder.getResults().toString());
	}

	
	private KieFileSystem createKieModuleFiles(Map<String, String> kieBaseDefinitions, ReleaseId kjarGAV, ArrayList<ReleaseId> kjarDependencies, Map<String, String> rulesResources) {
		
			
		KieModuleModel kModuleModel = kieServices.newKieModuleModel();
		
		ArrayList<KieBaseModel> kiebases = new ArrayList<KieBaseModel>();
			
		for (String key : kieBaseDefinitions.keySet()) {
			
			KieBaseModel kieBaseModel = kModuleModel.newKieBaseModel(key)
					.addPackage(kieBaseDefinitions.get(key))
					.setDefault(true)
	                .setEqualsBehavior( EqualityBehaviorOption.EQUALITY )
	                .setEventProcessingMode( EventProcessingOption.CLOUD );
			
//			kieBaseModel1.newKieSessionModel("ksession-test").setDefault(true)
//			.setType(KieSessionModel.KieSessionType.STATEFUL)
//			.setClockType( ClockTypeOption.get("realtime") )
//			.newWorkItemHandlerModel("Log", "new org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler()");
			
			kieBaseModel.newKieSessionModel(KSESSION_TAG+key).setDefault(true)
            .setType(KieSessionModel.KieSessionType.STATEFUL)
            .setClockType( ClockTypeOption.get("realtime")).setFileLogger("drools-logger", 5000, true);
			
			kiebases.add(kieBaseModel);
			
		}
		// kmodule.xml, pom.xml, drool resources
		System.out.println("kmodule.xml : --> \n"+kModuleModel.toXML());
		String pomXML = getPom(kjarGAV, kjarDependencies.get(0), kjarDependencies.get(1)); // TODO Hardcoded
		// dependencies this point onwards
//		new ReleaseIdImpl("org.kie", "kie-api", "6.3.0.Final-redhat-5"),
//		new ReleaseIdImpl("com.skoussou.brms", "laws-model", "1.0.0"));

		
		KieFileSystem kFileSystem = kieServices.newKieFileSystem()
				.writeKModuleXML(kModuleModel.toXML())
				.writePomXML(pomXML);
		
		// drl files
		for (String key : rulesResources.keySet()) {
			kFileSystem.write(KJAR_RESOURCES_PATH+KJAR_DROOLS_DEFAULT_NAMESPACE_PATH+"local/hague/" + key, rulesResources.get(key));
			//kFileSystem.write("src/main/resources/com/skoussou/brms/local/hague/"+key, rulesResources.get(key));
		}
		
		
//        if (extraResources != null) {
//	        for (Map.Entry<String, String> entry : extraResources.entrySet()) {
//				kfs.write(entry.getKey(), ResourceFactory.newByteArrayResource(entry.getValue().getBytes()));
//			}
//        }
		
		return kFileSystem;
	}
	
	/**
	 * 
	 * @param GAV
	 * @param kjarResourcesPath
	 * @param dependenciesGAV
	 * @param rulesResources
	 * @param kbaseDefinitions
	 */
	protected void createNewKJAR(String GAV, String kjarResourcesPath, List<String> dependenciesGAV, Map<String, String> rulesResources, CustomKBase... kbaseDefinitions) {
		long startTime = System.currentTimeMillis();

		kieServices = KieServices.Factory.get();

		// KJAR ID
		String[] GAV_DETAILS = GAV.split(COLON);
		//ReleaseId releaseId = kieServices.newReleaseId("com.skoussou.brms", "automated-kjar", "1.0.0");
		ReleaseId releaseId = kieServices.newReleaseId(GAV_DETAILS[0], GAV_DETAILS[1], GAV_DETAILS[2]);
		
		// KJAR Dependencies
		ArrayList<ReleaseId> kjarDependencies = new ArrayList<ReleaseId>();
		if (dependenciesGAV != null) {
			for (String dependentGAV : dependenciesGAV) {
				String[] DEPENDENT_GAV_DETAILS = GAV.split(COLON);
				kjarDependencies.add(kieServices.newReleaseId(DEPENDENT_GAV_DETAILS[0], DEPENDENT_GAV_DETAILS[1], DEPENDENT_GAV_DETAILS[2]));
			}
		}
		
		
		KieFileSystem kFileSystem = createKieModuleFiles2(releaseId, kjarResourcesPath, kjarDependencies, rulesResources, kbaseDefinitions);
		
		//LOGGER.info("KieBuiler building all...");
		KieBuilder kbuilder = kieServices.newKieBuilder(kFileSystem);
		kbuilder.buildAll();
		
		if (kbuilder.getResults().hasMessages(Message.Level.ERROR)) {
			System.out.println("Build time ERRORS: " + kbuilder.getResults().toString());			
		}

//		LOGGER.info("KieBuilder all sucessful: \n" + kbuilder.getResults().toString());
		System.out.println("KieBuilder run for "+kbuilder.getKieModule().getReleaseId());
		System.out.println("KieBuilder all sucessful: \n" + kbuilder.getResults().toString());

	}
	
	protected KieFileSystem createKieModuleFiles2(ReleaseId kjarGAV, String kjarResourcesPath, ArrayList<ReleaseId> kjarDependencies,
			Map<String, String> rulesResources, CustomKBase... kbaseDefinitions) {
		
		KieModuleModel kModuleModel = kieServices.newKieModuleModel();

		ArrayList<KieBaseModel> kiebases = new ArrayList<KieBaseModel>();
		
		if (kbaseDefinitions != null) {
			ArrayList<CustomKBase> kieBaseDefinitions = new ArrayList<CustomKBase>(Arrays.asList(kbaseDefinitions));
			for (CustomKBase kbase : kieBaseDefinitions) {
//				String packages = "";
//				for (String pkg : kbase.getPackages()) {
//					packages = packages+pkg+",";
//							
//				}
//				packages.substring(0,packages.length()-1);
//				
//				String includedKBases = "";
//				for (String ikbase : kbase.getIncludedKBases())) {
//					includedKBases = packages+includedKBases+",";
//							
//				}
//				includedKBases.substring(0,includedKBases.length()-1);
				
				KieBaseModel kieBaseModel = kModuleModel.newKieBaseModel(kbase.getKBase())
						//.addPackage(packages)
						.setDefault(true)
						.setEqualsBehavior( EqualityBehaviorOption.EQUALITY )
						.setEventProcessingMode( EventProcessingOption.CLOUD );
				if (kbase.getPackages() != null) {
					for (String pkg : kbase.getPackages()) {
						kieBaseModel.addPackage(pkg);
					}
				}
				if (kbase.getIncludedKBases() != null) {
					for (String kBaseName : kbase.getIncludedKBases()) {
						kieBaseModel.addInclude(kBaseName);
					}
				}


				if (kbase.isStatefulKSession()) {
					kieBaseModel.newKieSessionModel(KSESSION_TAG+kbase.getKBase()).setDefault(true)
					.setType(KieSessionModel.KieSessionType.STATEFUL)
					.setClockType( ClockTypeOption.get("realtime")).setFileLogger("drools-logger", 5000, true);
				} else {
					kieBaseModel.newKieSessionModel(KSESSION_TAG+kbase.getKBase()).setDefault(true)
					.setType(KieSessionModel.KieSessionType.STATELESS)
					.setClockType( ClockTypeOption.get("realtime")).setFileLogger("drools-logger", 5000, true);					
				}

				kiebases.add(kieBaseModel);

			}
		}
		
		// kmodule.xml, pom.xml, dependencies
		System.out.println("kmodule.xml : --> \n"+kModuleModel.toXML());
		String pomXML = getPom2(kjarGAV, kjarDependencies); 
		
		KieFileSystem kFileSystem = kieServices.newKieFileSystem()
				.writeKModuleXML(kModuleModel.toXML())
				.writePomXML(pomXML);
		
		// drl files
		for (String key : rulesResources.keySet()) {
			System.out.println("Adding Rules File "+key+" in KJAR Location "+kjarResourcesPath + key);
			kFileSystem.write(kjarResourcesPath + key, rulesResources.get(key));

//			kFileSystem.write(KJAR_RESOURCES_PATH+KJAR_DROOLS_DEFAULT_NAMESPACE_PATH+"local/hague/" + key, rulesResources.get(key));
			//kFileSystem.write("src/main/resources/com/skoussou/brms/local/hague/"+key, rulesResources.get(key));
		}

		return kFileSystem;
	}




	protected static String getRule(String packageName) {
        String s = "" +
                   "package "+packageName+"; \n\n" +
                   //"import org.drools.example.api.namedkiesession.Message \n\n" +
                   "rule ruleLocal when \n" +
                   "    String(this == \"GO\") \n" +

                   //"    Message(text == \"What's the problem?\") \n" +
                   "then\n" +
                   //"    insert( new Message(\"HAL\", \"I think you know what the problem is just as well as I do.\" ) ); \n" +
                   "    System.out.println(\"LOCAl RULE FIRED\");\n"+
                   "end \n";

        System.out.println("<----------- RULE TO BE ADDED TO KIEBASE ----------> ");
        System.out.println(s);
        System.out.println("<--------------------------------------------------> ");

        return s;
    }
	
	protected static String getProvinceRule(String packageName) {
        String s = "" +
                   "package "+packageName+"; \n\n" +
                   //"import org.drools.example.api.namedkiesession.Message \n\n" +
                   "rule ruleProvince when \n" +
                   "    String(this == \"GO\") \n" +

                   //"    Message(text == \"What's the problem?\") \n" +
                   "then\n" +
                   //"    insert( new Message(\"HAL\", \"I think you know what the problem is just as well as I do.\" ) ); \n" +
                   "    System.out.println(\"PROVINCE RULE FIRED\");\n"+
                   "end \n";

        System.out.println("<----------- RULE TO BE ADDED TO KIEBASE ----------> ");
        System.out.println(s);
        System.out.println("<--------------------------------------------------> ");

        return s;
    }
	
	protected static String getCentralRule(String packageName) {
        String s = "" +
                   "package "+packageName+"; \n\n" +
                   //"import org.drools.example.api.namedkiesession.Message \n\n" +
                   "rule ruleCentral when \n" +
                   "    String(this == \"GO\") \n" +

                   //"    Message(text == \"What's the problem?\") \n" +
                   "then\n" +
                   //"    insert( new Message(\"HAL\", \"I think you know what the problem is just as well as I do.\" ) ); \n" +
                   "    System.out.println(\"Central RULE FIRED\");\n"+
                   "end \n";

        System.out.println("<----------- RULE TO BE ADDED TO KIEBASE ----------> ");
        System.out.println(s);
        System.out.println("<--------------------------------------------------> ");

        return s;
    }
	
    protected String getPom(ReleaseId releaseId, ReleaseId... dependencies) {
        String pom =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd\">\n" +
                "  <modelVersion>4.0.0</modelVersion>\n" +
                "\n" +
                "  <groupId>" + releaseId.getGroupId() + "</groupId>\n" +
                "  <artifactId>" + releaseId.getArtifactId() + "</artifactId>\n" +
                "  <version>" + releaseId.getVersion() + "</version>\n" +
                "\n";
        if (dependencies != null && dependencies.length > 0) {
            pom += "<dependencies>\n";
            for (ReleaseId dep : dependencies) {
                pom += "<dependency>\n";
                pom += "  <groupId>" + dep.getGroupId() + "</groupId>\n";
                pom += "  <artifactId>" + dep.getArtifactId() + "</artifactId>\n";
                pom += "  <version>" + dep.getVersion() + "</version>\n";

                pom += "</dependency>\n";
            }
            pom += "</dependencies>\n";
        }
        pom += "</project>";
        
        System.out.println("\n ------------------------ \n "+pom+"\n ------------------------------\n");
        return pom;
    }
    
	
	private String getPom2(ReleaseId releaseId, ArrayList<ReleaseId> dependencies) {
        String pom =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd\">\n" +
                "  <modelVersion>4.0.0</modelVersion>\n" +
                "\n" +
                "  <groupId>" + releaseId.getGroupId() + "</groupId>\n" +
                "  <artifactId>" + releaseId.getArtifactId() + "</artifactId>\n" +
                "  <version>" + releaseId.getVersion() + "</version>\n" +
                "\n";
        if (dependencies != null && dependencies.size() > 0) {
            pom += "<dependencies>\n";
            for (ReleaseId dep : dependencies) {
                pom += "<dependency>\n";
                pom += "  <groupId>" + dep.getGroupId() + "</groupId>\n";
                pom += "  <artifactId>" + dep.getArtifactId() + "</artifactId>\n";
                pom += "  <version>" + dep.getVersion() + "</version>\n";

                pom += "</dependency>\n";
            }
            pom += "</dependencies>\n";
        }
        pom += "</project>";
        
        System.out.println("\n ------------------------ \n "+pom+"\n ------------------------------\n");
        return pom;
	}
	
	protected class CustomKBase {
		
		String KBase;
		List<String> packages;
		List<String> includedKBases;
		boolean statelessKSession;
		boolean statefulKSession;
		

		
		public CustomKBase(String kBase, List<String> packages, List<String> includedKBases, boolean statelessKSession,
				boolean statefulKSession) {
			super();
			KBase = kBase;
			this.packages = packages;
			this.includedKBases = includedKBases;
			this.statelessKSession = statelessKSession;
			this.statefulKSession = statefulKSession;
		}
		public String getKBase() {
			return KBase;
		}
		public List<String> getPackages() {
			return packages;
		}
		public boolean isStatelessKSession() {
			return statelessKSession;
		}
		public boolean isStatefulKSession() {
			return statefulKSession;
		}
		public List<String> getIncludedKBases() {
			return includedKBases;
		}
		
		
	}
	
//	protected InternalKieModule createKieJar(KieServices ks, ReleaseId releaseId, List<String> resources, Map<String, String> extraResources ) {
//
//
//        KieFileSystem kfs = createKieFileSystemWithKProject(ks);
//        kfs.writePomXML( getPom(releaseId) );
//
//
//        for (String resource : resources) {
//            kfs.write("src/main/resources/KBase-test/" + resource, ResourceFactory.newClassPathResource(resource));
//        }
//        if (extraResources != null) {
//	        for (Map.Entry<String, String> entry : extraResources.entrySet()) {
//				kfs.write(entry.getKey(), ResourceFactory.newByteArrayResource(entry.getValue().getBytes()));
//			}
//        }
//
//        kfs.write("src/main/resources/forms/DefaultProcess.ftl", ResourceFactory.newClassPathResource("repo/globals/forms/DefaultProcess.ftl"));
//
//        KieBuilder kieBuilder = ks.newKieBuilder(kfs);
//        if (!kieBuilder.buildAll().getResults().getMessages().isEmpty()) {
//            for (Message message : kieBuilder.buildAll().getResults().getMessages()) {
//                logger.error("Error Message: ({}) {}", message.getPath(), message.getText());
//            }
//            throw new RuntimeException(
//                    "There are errors builing the package, please check your knowledge assets!");
//        }
//
//        return ( InternalKieModule ) kieBuilder.getKieModule();
//    }
//
//
//
//    protected KieFileSystem createKieFileSystemWithKProject(KieServices ks) {
//        KieModuleModel kproj = ks.newKieModuleModel();
//
//        KieBaseModel kieBaseModel1 = kproj.newKieBaseModel("KBase-test").setDefault(true).addPackage("*")
//                .setEqualsBehavior( EqualityBehaviorOption.EQUALITY )
//                .setEventProcessingMode( EventProcessingOption.STREAM );
//
//
//        kieBaseModel1.newKieSessionModel("ksession-test").setDefault(true)
//                .setType(KieSessionModel.KieSessionType.STATEFUL)
//                .setClockType( ClockTypeOption.get("realtime") )
//                .newWorkItemHandlerModel("Log", "new org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler()");
//
//        kieBaseModel1.newKieSessionModel("ksession-test-2").setDefault(false)
//        .setType(KieSessionModel.KieSessionType.STATEFUL)
//        .setClockType( ClockTypeOption.get("realtime") )
//        .newWorkItemHandlerModel("Log", "new org.jbpm.kie.services.test.objects.KieConteinerSystemOutWorkItemHandler(kieContainer)");
//
//        kieBaseModel1.newKieSessionModel("ksession-test2").setDefault(false)
//        .setType(KieSessionModel.KieSessionType.STATEFUL)
//        .setClockType( ClockTypeOption.get("realtime") );
//
//        KieFileSystem kfs = ks.newKieFileSystem();
//        kfs.writeKModuleXML(kproj.toXML());
//        return kfs;
}
