package com.skoussou.brms;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.Results;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.builder.IncrementalResults;
import org.kie.internal.builder.InternalKieBuilder;

import com.skoussou.brms.KJARBuilder;
import com.skoussou.brms.KJARBuilder.CustomKBase;

/**
 * Testing KJAR creation
 * @author skousou@gmail.com
 *
 */
public class KJARBuilderTest {

	@Test
	public void testBuildingKJARWithDependencies() {
		KJARBuilder kjarBuilder = new KJARBuilder();
		
		// Create CENTRAL KJAR non-KieContainer
		String GAVCENTRAL = "com.skoussou.brms:automated-central-kjar:1.0.0";
		CustomKBase centralKBase = kjarBuilder.new CustomKBase(KJARBuilder.CENTRAL_KIEBASE_NAME, Arrays.asList(KJARBuilder.DROOLS_CENTRAL_PACKAGE_NAME), null, false, true);
		List dependencies = new ArrayList<String>();
		Map resources = new HashMap<String, String>();
		resources.put("drl0.drl", KJARBuilder.getCentralRule(KJARBuilder.DROOLS_CENTRAL_PACKAGE_NAME));
		// kjarBuilder.createNewKJAR(GAVCENTRAL, KJAR_RESOURCES_PATH+KJAR_DROOLS_DEFAULT_NAMESPACE_PATH+KJAR_DROOLS_CENTRAL_NAMESPACE_PATH, dependencies, resources, centralKBase);
		kjarBuilder.createNewKJAR(GAVCENTRAL, "src/main/resources/com/skoussou/brms/central/", dependencies, resources, centralKBase);
//		kjarBuilder.createKieContainerAndExecute(GAVCENTRAL, DROOLS_CENTRAL_PACKAGE_NAME, KSESSION_TAG+CENTRAL_KIEBASE_NAME, "GO");


		
		// Create WEST PROVINCE KJAR non-KieContainer
		String GAVPROVINCE = "com.skoussou.brms:automated-province-kjar:1.0.0";
		CustomKBase provinceKBase = kjarBuilder.new  CustomKBase(KJARBuilder.WEST_PROVINCE_KIEBASE_NAME, Arrays.asList(KJARBuilder.DROOLS_WEST_PROVINCE_PACKAGE_NAME), null, false, true);
		List provicnceDependencies = new ArrayList<String>();
		Map provicnceResources = new HashMap<String, String>();
		provicnceResources.put("drl0.drl", KJARBuilder.getProvinceRule(KJARBuilder.DROOLS_WEST_PROVINCE_PACKAGE_NAME));

		// kjarBuilder.createNewKJAR(GAVPROVINCE, KJAR_RESOURCES_PATH+KJAR_DROOLS_DEFAULT_NAMESPACE_PATH+KJAR_DROOLS_PROVINCE_WEST_NAMESPACE_PATH, provicnceDependencies, provicnceResources, provinceKBase);
		kjarBuilder.createNewKJAR(GAVPROVINCE, "src/main/resources/com/skoussou/brms/province/west/", provicnceDependencies, provicnceResources, provinceKBase);
//		kjarBuilder.createKieContainerAndExecute(GAVPROVINCE, DROOLS_WEST_PROVINCE_PACKAGE_NAME, KSESSION_TAG+WEST_PROVINCE_KIEBASE_NAME, "GO");

		
		
		// Create WEST PROVINCE KJAR non-KieContainer
		String GAVLOCAL = "com.skoussou.brms:automated-hague-kjar:1.0.0";
		CustomKBase localKBase = kjarBuilder.new  CustomKBase(KJARBuilder.HAGUE_KIEBASE_NAME, 
				Arrays.asList(KJARBuilder.DROOLS_HAGUE_LOCAL_PACKAGE_NAME, KJARBuilder.DROOLS_CENTRAL_PACKAGE_NAME, KJARBuilder.DROOLS_WEST_PROVINCE_PACKAGE_NAME), 
				//Arrays.asList(DROOLS_HAGUE_LOCAL_PACKAGE_NAME),
				Arrays.asList(KJARBuilder.CENTRAL_KIEBASE_NAME, KJARBuilder.WEST_PROVINCE_KIEBASE_NAME),
				false, 
				true);
		List localDependencies = Arrays.asList("com.skoussou.brms:automated-central-kjar:1.0.0", "com.skoussou.brms:automated-province-kjar:1.0.0");
		Map localeResources = new HashMap<String, String>();
		localeResources.put("drl0.drl", KJARBuilder.getRule(KJARBuilder.DROOLS_HAGUE_LOCAL_PACKAGE_NAME));

		// kjarBuilder.createNewKJAR(GAVPROVINCE, KJAR_RESOURCES_PATH+KJAR_DROOLS_DEFAULT_NAMESPACE_PATH+KJAR_DROOLS_LOCAL_NAMESPACE_PATH, localDependencies, provilocaleResourcescnceResources, localKBase);
		kjarBuilder.createNewKJAR(GAVLOCAL, "src/main/resources/com/skoussou/brms/local/hague/", localDependencies, localeResources, localKBase);
		
		kjarBuilder.createKieContainerAndExecute(GAVLOCAL, KJARBuilder.DROOLS_HAGUE_LOCAL_PACKAGE_NAME, KJARBuilder.KSESSION_TAG+KJARBuilder.HAGUE_KIEBASE_NAME, "GO");
	}
	
	@Test
	public void testBuildingKJARWithDependencies_SingleKieBuilder(){
		long startTime = System.currentTimeMillis();

		
		/* *************************************************************************************************** */
		/* CENTRAL KIE MODULE                                                                                  */
		/* *************************************************************************************************** */
		// KJAR ID
		String[] GAV_DETAILS = null;
		ReleaseId releaseId = null;
		// Dependencies
		List<String> dependencies = new ArrayList<String>();
		
		KJARBuilder kjarBuilder = new KJARBuilder();
		// Single KieServices for KJARBuilder
		kjarBuilder.setKieServices(KieServices.Factory.get());
		
		// Create CENTRAL KJAR non-KieContainer
		String GAVCENTRAL = "com.skoussou.brms:automated-central-kjar:1.0.0";
		CustomKBase centralKBase = kjarBuilder.new CustomKBase(KJARBuilder.CENTRAL_KIEBASE_NAME, Arrays.asList(KJARBuilder.DROOLS_CENTRAL_PACKAGE_NAME), null, false, true);	
		
		// GAVCENTRAL KJAR ID
		GAV_DETAILS = GAVCENTRAL.split(KJARBuilder.COLON);
		releaseId = kjarBuilder.getKieServices().newReleaseId(GAV_DETAILS[0], GAV_DETAILS[1], GAV_DETAILS[2]);	
		// GAVCENTRAL Dependencies
        // none
		Map rulesResources = new HashMap<String, String>();
		rulesResources.put("drl0.drl", KJARBuilder.getCentralRule(KJARBuilder.DROOLS_CENTRAL_PACKAGE_NAME));
		// GAVCENTRAL KJAR Dependencies
		ArrayList<ReleaseId> kjarDependencies = new ArrayList<ReleaseId>();
		if (dependencies != null) {
			for (String dependentGAV : dependencies) {
				String[] DEPENDENT_GAV_DETAILS = dependentGAV.split(KJARBuilder.COLON);
				kjarDependencies.add(kjarBuilder.getKieServices().newReleaseId(DEPENDENT_GAV_DETAILS[0], DEPENDENT_GAV_DETAILS[1], DEPENDENT_GAV_DETAILS[2]));
			}
		}
				
		KieFileSystem kFileSystem = kjarBuilder.createKieModuleFiles2(releaseId, "src/main/resources/com/skoussou/brms/central/", kjarDependencies, rulesResources, centralKBase);
		
		//LOGGER.info("KieBuiler building all...");
		KieBuilder kbuilder = kjarBuilder.getKieServices().newKieBuilder(kFileSystem);
		kbuilder.buildAll();
		
		if (kbuilder.getResults().hasMessages(Message.Level.ERROR)) {
			System.out.println("Build time ERRORS: " + kbuilder.getResults().toString());			
		}

		System.out.println("KieBuilder run for "+kbuilder.getKieModule().getReleaseId());
		System.out.println("KieBuilder all sucessful: \n" + kbuilder.getResults().toString());
		
		/* *************************************************************************************************** */
		/* PROVINCE KIE MODULE                                                                                 */
		/* *************************************************************************************************** */
		
		// Create WEST PROVINCE KJAR non-KieContainer
		String GAVPROVINCE = "com.skoussou.brms:automated-province-kjar:1.0.0";
		GAV_DETAILS = GAVPROVINCE.split(KJARBuilder.COLON);
		ReleaseId province_ReleaseId = kjarBuilder.getKieServices().newReleaseId(GAV_DETAILS[0], GAV_DETAILS[1], GAV_DETAILS[2]);	
		
		CustomKBase provinceKBase = kjarBuilder.new  CustomKBase(KJARBuilder.WEST_PROVINCE_KIEBASE_NAME, Arrays.asList(KJARBuilder.DROOLS_WEST_PROVINCE_PACKAGE_NAME), null, false, true);
		List<String> provicnceDependencies = new ArrayList<String>();
		Map provicnceResources = new HashMap<String, String>();
		provicnceResources.put("drl0.drl", KJARBuilder.getProvinceRule(KJARBuilder.DROOLS_WEST_PROVINCE_PACKAGE_NAME));
			
		// PROVINCE KJAR Dependencies
		ArrayList<ReleaseId> provinceKjarDependencies = new ArrayList<ReleaseId>();
		if (provicnceDependencies != null) {
			for (String dependentGAV : provicnceDependencies) {
				String[] DEPENDENT_GAV_DETAILS = dependentGAV.split(KJARBuilder.COLON);
				provinceKjarDependencies.add(kjarBuilder.getKieServices().newReleaseId(DEPENDENT_GAV_DETAILS[0], DEPENDENT_GAV_DETAILS[1], DEPENDENT_GAV_DETAILS[2]));
			}
		}
		
		KieFileSystem provincekFileSystem = kjarBuilder.createKieModuleFiles2(province_ReleaseId, "src/main/resources/com/skoussou/brms/province/west/", provinceKjarDependencies, provicnceResources, provinceKBase);
		
		//LOGGER.info("KieBuiler building all...");
		KieBuilder provincekbuilder = kjarBuilder.getKieServices().newKieBuilder(kFileSystem);
		provincekbuilder = kjarBuilder.getKieServices().newKieBuilder(provincekFileSystem);
		provincekbuilder.buildAll();
		
		if (kbuilder.getResults().hasMessages(Message.Level.ERROR)) {
			System.out.println("Build time ERRORS: " + kbuilder.getResults().toString());			
		}

		System.out.println("KieBuilder run for "+kbuilder.getKieModule().getReleaseId());
		System.out.println("KieBuilder all sucessful: \n" + kbuilder.getResults().toString());


		/* *************************************************************************************************** */
		/* MUNICPALITY KIE MODULE                                                                                 */
		/* *************************************************************************************************** */
		
		// Create MUNICIPALITY OF HAGUE non-KieContainer
		String GAVLOCAL = "com.skoussou.brms:automated-hague-kjar:1.0.0";
		GAV_DETAILS = GAVLOCAL.split(KJARBuilder.COLON);
		ReleaseId municipality_ReleaseId = kjarBuilder.getKieServices().newReleaseId(GAV_DETAILS[0], GAV_DETAILS[1], GAV_DETAILS[2]);	

		CustomKBase localKBase = kjarBuilder.new  CustomKBase(KJARBuilder.HAGUE_KIEBASE_NAME, 
				Arrays.asList(KJARBuilder.DROOLS_HAGUE_LOCAL_PACKAGE_NAME, KJARBuilder.DROOLS_CENTRAL_PACKAGE_NAME, KJARBuilder.DROOLS_WEST_PROVINCE_PACKAGE_NAME), 
				//Arrays.asList(DROOLS_HAGUE_LOCAL_PACKAGE_NAME),
				Arrays.asList(KJARBuilder.CENTRAL_KIEBASE_NAME, KJARBuilder.WEST_PROVINCE_KIEBASE_NAME),
				false, 
				true);		
		List<String> localDependencies = Arrays.asList("com.skoussou.brms:automated-central-kjar:1.0.0", "com.skoussou.brms:automated-province-kjar:1.0.0");
		Map localResources = new HashMap<String, String>();
		provicnceResources.put("drl0.drl", KJARBuilder.getProvinceRule(KJARBuilder.DROOLS_HAGUE_LOCAL_PACKAGE_NAME));
			
		// PROVINCE KJAR Dependencies
		ArrayList<ReleaseId> localkjarDependencies = new ArrayList<ReleaseId>();
		if (localDependencies != null) {
			for (String dependentGAV : localDependencies) {
				String[] DEPENDENT_GAV_DETAILS = dependentGAV.split(KJARBuilder.COLON);
				localkjarDependencies.add(kjarBuilder.getKieServices().newReleaseId(DEPENDENT_GAV_DETAILS[0], DEPENDENT_GAV_DETAILS[1], DEPENDENT_GAV_DETAILS[2]));
			}
		}
				
		KieFileSystem localkFileSystem = kjarBuilder.createKieModuleFiles2(municipality_ReleaseId, "src/main/resources/com/skoussou/brms/local/hague/", localkjarDependencies, localResources, localKBase);
		
		//LOGGER.info("KieBuiler building all...");
		KieBuilder localkbuilder = kjarBuilder.getKieServices().newKieBuilder(kFileSystem);
		localkbuilder = kjarBuilder.getKieServices().newKieBuilder(localkFileSystem);
		localkbuilder.buildAll();
		
		if (kbuilder.getResults().hasMessages(Message.Level.ERROR)) {
			System.out.println("Build time ERRORS: " + kbuilder.getResults().toString());			
		}

		System.out.println("KieBuilder run for "+kbuilder.getKieModule().getReleaseId());
		System.out.println("KieBuilder all sucessful: \n" + kbuilder.getResults().toString());
		
		
		/* *************************************************************************************************** */
		/* EXECUTING                                                                                           */
		/* *************************************************************************************************** */
		
		//	kjarBuilder.createKieContainerAndExecute(GAVLOCAL, KJARBuilder.DROOLS_HAGUE_LOCAL_PACKAGE_NAME, KJARBuilder.KSESSION_TAG+KJARBuilder.HAGUE_KIEBASE_NAME, "GO");

		// Option 1 - directly from kbuilder.getKieServices
		KieContainer hagueKieContainer = kjarBuilder.getKieServices().newKieContainer(municipality_ReleaseId);
		
		KieSession ksession = hagueKieContainer.newKieSession( KJARBuilder.KSESSION_TAG+KJARBuilder.HAGUE_KIEBASE_NAME );
        ksession.insert( "GO" );
        System.out.println("**********Option 1 - directly from kjarBuilder*************");
        ksession.fireAllRules();
        System.out.println("***********************");
        ksession.dispose();
		
	}
	
	
	@Test
	public void testBuildingKJAR() {
		String GAV = "com.skoussou.brms:automated-kjar:1.0.0";

		KJARBuilder kjarBuilder = new KJARBuilder();
		KieContainer kContainer= kjarBuilder.createNewKieContainer(GAV);
		
		assertNotNull(kContainer.getKieBase(KJARBuilder.HAGUE_KIEBASE_NAME));
		assertTrue(kContainer.getKieBaseNames().contains(KJARBuilder.HAGUE_KIEBASE_NAME));
	}
	
	@Test
	public void testBuildingKJARFiringRules() {
		String GAV = "com.skoussou.brms:automated-kjar:1.0.0";

		KJARBuilder kjarBuilder = new KJARBuilder();
		kjarBuilder.createNewKieContainer(GAV);
		
		assertEquals( 1, kjarBuilder.executeRulesOneKieContainer(GAV, "GO") );
	}
	
	@Test
    public void testIncrementalCompilationWithIncludes() throws Exception {
        String drl1 = "global java.util.List list\n" +
                "rule R1 when\n" +
                " $s : String() " +
                "then\n" +
                " list.add( \"a\" + $s );" +
                "end\n";

        String drl2 = "global java.util.List list\n" +
                "rule R1 when\n" +
                " $s : String() " +
                "then\n" +
                " list.add( \"b\" + $s );" +
                "end\n";

        ReleaseId releaseId = KieServices.Factory.get().newReleaseId( "org.test", "test", "1.0.0-SNAPSHOT" );
        KieServices ks = KieServices.Factory.get();

        KieModuleModel kproj = ks.newKieModuleModel();
        KieBaseModel kieBaseModel1 = kproj.newKieBaseModel( "KBase1" )
                .addPackage( "org.pkg1" );
        kieBaseModel1.newKieSessionModel( "KSession1" );
        KieBaseModel kieBaseModel2 = kproj.newKieBaseModel( "KBase2" )
                .addPackage( "org.pkg2" )
                .addInclude( "KBase1" );
        kieBaseModel2.newKieSessionModel( "KSession2" );

        KieFileSystem kfs = ks.newKieFileSystem()
                .generateAndWritePomXML( releaseId )
                .write( "src/main/resources/KBase1/org/pkg1/r1.drl", drl1 )
                .writeKModuleXML( kproj.toXML() );

        KieBuilder kieBuilder = ks.newKieBuilder( kfs );

        kieBuilder.buildAll();
        assertEquals( 0, kieBuilder.getResults().getMessages().size() );

        KieContainer kc = ks.newKieContainer( releaseId );

        KieSession ksession = kc.newKieSession( "KSession2" );
        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list", list );
        ksession.insert( "Foo" );
        ksession.fireAllRules();

        assertEquals( 1, list.size() );
        assertEquals( "aFoo", list.get( 0 ) );
        list.clear();

        kfs.delete( "src/main/resources/KBase1/org/pkg1/r1.drl" );
        kfs.write( "src/main/resources/KBase1/org/pkg1/r2.drl", drl2 );

        IncrementalResults results = ( (InternalKieBuilder) kieBuilder ).incrementalBuild();
        assertEquals( 0, results.getAddedMessages().size() );

        Results updateResults = kc.updateToVersion( releaseId );
        assertEquals( 0, updateResults.getMessages().size() );

        ksession.insert( "Bar" );
        ksession.fireAllRules();

        assertEquals( 2, list.size() );
        assertTrue( list.containsAll( Arrays.asList( "bBar", "bFoo" ) ) );
    }

	
	@Test
    public void testTheKJARCentral() throws Exception {
		KJARBuilder kjarBuilder = new KJARBuilder();
		
		// Create CENTRAL KJAR non-KieContainer
		String GAVCENTRAL = "com.skoussou.brms:automated-central-kjar:1.0.0";
		KJARBuilder.CustomKBase centralKBase = kjarBuilder.new CustomKBase(KJARBuilder.CENTRAL_KIEBASE_NAME, Arrays.asList(KJARBuilder.DROOLS_CENTRAL_PACKAGE_NAME), null, false, true);
		List dependencies = new ArrayList<String>();
		Map resources = new HashMap<String, String>();
		resources.put("drl0.drl", kjarBuilder.getCentralRule(KJARBuilder.DROOLS_CENTRAL_PACKAGE_NAME));
		// kjarBuilder.createNewKJAR(GAVCENTRAL, KJAR_RESOURCES_PATH+KJAR_DROOLS_DEFAULT_NAMESPACE_PATH+KJAR_DROOLS_CENTRAL_NAMESPACE_PATH, dependencies, resources, centralKBase);
		kjarBuilder.createNewKJAR(GAVCENTRAL, "src/main/resources/com/skoussou/brms/central/", dependencies, resources, centralKBase);
		
		kjarBuilder.createKieContainerAndExecute(GAVCENTRAL, KJARBuilder.DROOLS_CENTRAL_PACKAGE_NAME, KJARBuilder.KSESSION_TAG+KJARBuilder.CENTRAL_KIEBASE_NAME, "GO");
	}
	
	@Test
    public void testTheKJARProvince() throws Exception {
		KJARBuilder kjarBuilder = new KJARBuilder();
		
		// Create WEST PROVINCE KJAR non-KieContainer
		String GAVPROVINCE = "com.skoussou.brms:automated-province-kjar:1.0.0";
		KJARBuilder.CustomKBase provinceKBase = kjarBuilder.new  CustomKBase(KJARBuilder.WEST_PROVINCE_KIEBASE_NAME, Arrays.asList(KJARBuilder.DROOLS_WEST_PROVINCE_PACKAGE_NAME), null, false, true);
		List provicnceDependencies = new ArrayList<String>();
		Map provicnceResources = new HashMap<String, String>();
		provicnceResources.put("drl0.drl", kjarBuilder.getProvinceRule(KJARBuilder.DROOLS_WEST_PROVINCE_PACKAGE_NAME));

		// kjarBuilder.createNewKJAR(GAVPROVINCE, KJAR_RESOURCES_PATH+KJAR_DROOLS_DEFAULT_NAMESPACE_PATH+KJAR_DROOLS_PROVINCE_WEST_NAMESPACE_PATH, provicnceDependencies, provicnceResources, provinceKBase);
		kjarBuilder.createNewKJAR(GAVPROVINCE, "src/main/resources/com/skoussou/brms/province/west/", provicnceDependencies, provicnceResources, provinceKBase);

		kjarBuilder.createKieContainerAndExecute(GAVPROVINCE, KJARBuilder.DROOLS_WEST_PROVINCE_PACKAGE_NAME, KJARBuilder.KSESSION_TAG+KJARBuilder.WEST_PROVINCE_KIEBASE_NAME, "GO");
	}
	
	@Test
    public void testTheKJARLogic() throws Exception {
        // DROOLS-462
		
		String drl0 = "package "+"com.skoussou.brms.local.hague"+"; \n\n" +
	                   "rule rule6 when\n" +
	                   "  String(this == \"GO\") \n" +
	                   "then\n" +
	                   "  System.out.println(\"FOUND GO\");\n"+
	                   "end\n";


//        String drl0 = "global java.util.List list\n" +
//                "rule R1 when\n" +
//                " $s : String() " +
//                "then\n" +
//                " list.add( \"a\" + $s );" +
//                "end\n";
//
//        String drl2 = "global java.util.List list\n" +
//                "rule R1 when\n" +
//                " $s : String() " +
//                "then\n" +
//                " list.add( \"b\" + $s );" +
//                "end\n";

        //ReleaseId releaseId = KieServices.Factory.get().newReleaseId( "com.skoussou.brms", "automated-kjar", "1.0.0" );
        ReleaseId releaseId = KieServices.Factory.get().newReleaseId( "com.skoussou.brms", "automated-kjar", "1.0.0" );
		
        KieServices ks = KieServices.Factory.get();

        KieModuleModel kproj = ks.newKieModuleModel();
        KieBaseModel kieBaseModel1 = kproj.newKieBaseModel( "theHagueKBase" )
        		.setDefault(true)             
        		.addPackage( "com.skoussou.brms.local.hague" );
        kieBaseModel1.newKieSessionModel( "theHagueKBaseKSession1" )
               .setDefault(true);
//        KieBaseModel kieBaseModel2 = kproj.newKieBaseModel( "KBase2" )
//                .addPackage( "org.pkg2" )
//                .addInclude( "KBase1" );
//        kieBaseModel2.newKieSessionModel( "KSession2" );

        KieFileSystem kfs = ks.newKieFileSystem()
                .generateAndWritePomXML( releaseId )
                .write( "src/main/resources/com/skoussou/brms/local/hague/drl0.drl", drl0 )
                .writeKModuleXML( kproj.toXML() );
//        KieFileSystem kfs = ks.newKieFileSystem()
//                .generateAndWritePomXML( releaseId )
//                .write( "src/main/resources/KBase1/org/pkg1/r1.drl", drl1 )
//                .writeKModuleXML( kproj.toXML() );

        KieBuilder kieBuilder = ks.newKieBuilder( kfs );

        kieBuilder.buildAll();
        //System.out.println(kieBuilder.getResults().getMessages());
        //assertEquals( 0, kieBuilder.getResults().getMessages().size() );

        KieContainer kc = ks.newKieContainer( releaseId );

        KieSession ksession = kc.newKieSession( "theHagueKBaseKSession1" );
        List<String> list = new ArrayList<String>();
        ksession.insert( "GO" );
        ksession.fireAllRules();
        
//        KieSession ksession = kc.newKieSession( "KSession2" );
//        List<String> list = new ArrayList<String>();
//        ksession.setGlobal( "list", list );
//        ksession.insert( "Foo" );
//        ksession.fireAllRules();

//        assertEquals( 1, list.size() );
//        assertEquals( "aFoo", list.get( 0 ) );
//        list.clear();
//
//        kfs.delete( "src/main/resources/KBase1/org/pkg1/r1.drl" );
//        kfs.write( "src/main/resources/KBase1/org/pkg1/r2.drl", drl2 );
//
//        IncrementalResults results = ( (InternalKieBuilder) kieBuilder ).incrementalBuild();
//        assertEquals( 0, results.getAddedMessages().size() );
//
//        Results updateResults = kc.updateToVersion( releaseId );
//        assertEquals( 0, updateResults.getMessages().size() );
//
//        ksession.insert( "Bar" );
//        ksession.fireAllRules();
//
//        assertEquals( 2, list.size() );
//        assertTrue( list.containsAll( Arrays.asList( "bBar", "bFoo" ) ) );
    }

}
