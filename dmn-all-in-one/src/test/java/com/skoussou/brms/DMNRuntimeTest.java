package com.skoussou.brms;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.dmn.core.api.DMNContext;
import org.kie.dmn.core.api.DMNFactory;
import org.kie.dmn.core.api.DMNModel;
import org.kie.dmn.core.api.DMNResult;
import org.kie.dmn.core.api.DMNRuntime;

import static org.junit.Assert.*;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;

import java.math.BigDecimal;
import java.util.Arrays;

import org.junit.Ignore;
import org.junit.Test;

import com.skoussou.brms.util.DMNRuntimeUtil;
import com.skoussou.brms.util.KieHelper;


/**
 * Test DMN Scenarios
 * @author skousou@gmail.com
 *
 */
public class DMNRuntimeTest {

	protected DMNRuntime createRuntime( String resourceName ) {
		KieServices ks = KieServices.Factory.get();
		KieContainer kieContainer = KieHelper.getKieContainer(
				ks.newReleaseId( "org.kie", "dmn-test", "1.0" ),
				ks.getResources().newClassPathResource( resourceName, DMNRuntimeTest.class ) );

		DMNRuntime runtime = kieContainer.newKieSession().getKieRuntime( DMNRuntime.class );
		assertNotNull( runtime );
		return runtime;
	}

    
    @Test
    public void testCompleteMarinaPermit_SingleDMN() {
    	DMNRuntime runtime = DMNRuntimeUtil.createRuntime( Arrays.asList("Permissions-needed-one-file.dmn"), this.getClass() );
        
        runtime.getModels().stream().map(s -> s.getName()).forEach(System.out::println);
        
        DMNModel dmnModel = runtime.getModel( "http://www.trisotech.com/definitions/_67c4b33e-d0fb-4b10-93c9-fb9759c61b72", "Permissions-needed-one-file" );
        assertThat( dmnModel, notNullValue() );

        
        System.out.println("*********** DMN MODEL VALIDATIONS MESSAGES ***********");
        System.out.println(dmnModel.getMessages());
        System.out.println("******************************************************");
        
        DMNContext context = DMNFactory.newContext();
        context.set( "Marina offers mooring", true );
        context.set( "Length is more than 4 meters", true );
        context.set( "Intended for commercial use", false );
        
        context.set("less than 10 moorings", true);
        context.set("Moorings for seaworthy ships", true);
        context.set("More than 50 moorings", false);
        
        DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        DMNContext result = dmnResult.getContext();
        
        assertThat( result.get( "Exploting a marina is applicable" ), is(true) );
        assertThat( result.get( "Decision permit exploiting a marina" ), is(true) );
        assertThat( result.get( "Permission is needed" ), is("Permit needed") );
    
    }
	
    @Test
    @Ignore("Import functionality not ready yet and some XML error from Trisotech DMN editor")
    public void testCompleteMarinaPermit_withImports() {
    	DMNRuntime runtime = DMNRuntimeUtil.createRuntime( Arrays.asList("Marina-applicability.dmn","Marina-permit.dmn","Permissions-needed-with-import.dmn"), this.getClass() );
        
        runtime.getModels().stream().map(s -> s.getName()).forEach(System.out::println);
        
        DMNModel dmnModel = runtime.getModel( "https://github.com/droolsjbpm/kie-dmn", "Permissions-needed" );
        assertThat( dmnModel, notNullValue() );

        
        System.out.println("*********** DMN MODEL VALIDATIONS MESSAGES ***********");
        System.out.println(dmnModel.getMessages());
        System.out.println("******************************************************");
        
        DMNContext context = DMNFactory.newContext();
        context.set( "Marina offers mooring", true );
        context.set( "Length is more than 4 meters", true );
        context.set( "Intended for commercial use", false );
        
        context.set("less than 10 moorings", true);
        context.set("Moorings for seaworthy ships", true);
        context.set("More than 50 moorings", false);
        
        DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        DMNContext result = dmnResult.getContext();
        
        assertThat( result.get( "Permission results" ), is(true) );
        
    }
	
    @Test
    public void testMarinaPermit() {
        //DMNRuntime runtime = DMNRuntimeUtil.createRuntime( "MarinaExploitationPermit.dmn", this.getClass() );
    	DMNRuntime runtime = DMNRuntimeUtil.createRuntime( Arrays.asList("MarinaExploitationPermit.dmn","NotificationsTest3.dmn"), this.getClass() );
        
        runtime.getModels().stream().map(s -> s.getName()).forEach(System.out::println);
        
        DMNModel dmnModel = runtime.getModel( "https://github.com/droolsjbpm/kie-dmn", "marina-exploitation-rules" );
        assertThat( dmnModel, notNullValue() );

        
        System.out.println("*********** DMN MODEL VALIDATIONS MESSAGES ***********");
        System.out.println(dmnModel.getMessages());
        System.out.println("******************************************************");


        DMNContext context = DMNFactory.newContext();
        context.set( "noOfMoorings", new BigDecimal(9) );
        context.set( "isForSeaWorthyShip",  true);
        DMNResult dmnResult = runtime.evaluateAll( dmnModel, context );
        DMNContext result = dmnResult.getContext();
        assertThat( result.get( "Marina Exploitation Status" ), is( "Exploiting For less 10 Seaworthy Ship" ) );
        
        assertThat( result.get( "Marina Permit Status" ), is( "Marina Permit Required" ) );

        
        context.set( "noOfMoorings", new BigDecimal(51) );
        context.set( "isForSeaWorthyShip",  false);
        dmnResult = runtime.evaluateAll( dmnModel, context );
        result = dmnResult.getContext();
        assertThat( result.get( "Marina Exploitation Status" ), is( "Exploiting For more 50 Non-Seaworthy Ship" ) );
        
        context.set( "noOfMoorings", new BigDecimal(51) );
        context.set( "isForSeaWorthyShip",  true);        
        dmnResult = runtime.evaluateAll( dmnModel, context );
        result = dmnResult.getContext();
        assertThat( result.get( "Marina Exploitation Status" ), nullValue() );
        
        
    }
    
    
    @Test
    public void testNotificationsApproved3() {
        DMNRuntime runtime = DMNRuntimeUtil.createRuntime( "NotificationsTest3.dmn", this.getClass() );
        DMNModel dmnModel = runtime.getModel( "https://github.com/droolsjbpm/kie-dmn", "building-structure-rules" );
        assertThat( dmnModel, notNullValue() );

        
        System.out.println("*********** DMN MODEL VALIDATIONS MESSAGES ***********");
        System.out.println(dmnModel.getMessages());
        System.out.println("******************************************************");


        DMNContext context = DMNFactory.newContext();
        context.set( "existingActivityApplicability", true );
        context.set( "Distance", new BigDecimal(9999) );
        context.set( "willIncreaseTraffic", true );

        DMNResult dmnResult = runtime.evaluateAll( dmnModel, context );

        DMNContext result = dmnResult.getContext();

        assertThat( result.get( "Notification Status" ), is( "Notification to Province Approved" ) );
        assertThat( result.get( "Permit Status" ), is( "Building Activity Province Permit Required" ) );
    }
    
    
    @Test
    public void testNotificationsApproved2() {
        DMNRuntime runtime = DMNRuntimeUtil.createRuntime( "NotificationsTest2.dmn", this.getClass() );
        DMNModel dmnModel = runtime.getModel( "https://github.com/droolsjbpm/kie-dmn", "building-structure-rules" );
        assertThat( dmnModel, notNullValue() );

        DMNContext context = DMNFactory.newContext();
        context.set( "existingActivityApplicability", true );
        context.set( "Distance", new BigDecimal(9999) );
        context.set( "willIncreaseTraffic", true );

        DMNResult dmnResult = runtime.evaluateAll( dmnModel, context );

        DMNContext result = dmnResult.getContext();

        assertThat( result.get( "Notification Status" ), is( "Notification to Province Approved" ) );
    }
    
    @Test
    public void testNotificationsDeclined2() {
        DMNRuntime runtime = DMNRuntimeUtil.createRuntime( "NotificationsTest2.dmn", this.getClass() );
        DMNModel dmnModel = runtime.getModel( "https://github.com/droolsjbpm/kie-dmn", "building-structure-rules" );
        assertThat( dmnModel, notNullValue() );

        DMNContext context = DMNFactory.newContext();
        context.set( "existingActivityApplicability", true );
        context.set( "Distance", new BigDecimal(10001) );
        context.set( "willIncreaseTraffic", true );

        DMNResult dmnResult = runtime.evaluateAll( dmnModel, context );

        DMNContext result = dmnResult.getContext();

        assertThat( result.get( "Notification Status" ), is( "Notification to Province Declined" ) );
    }
    	
	@Test
	public void testSimpleEvaluateAll() {
		DMNRuntime runtime = createRuntime( "0001-input-data-string.dmn" );
		DMNModel dmnModel = runtime.getModel( "https://github.com/droolsjbpm/kie-dmn", "0001-input-data-string" );
		assertThat( dmnModel, notNullValue() );

		DMNContext context = DMNFactory.newContext();
		context.set( "Full Name", "John Doe" );

		DMNResult dmnResult = runtime.evaluateAll( dmnModel, context );

		assertThat( dmnResult.getDecisionResults().size(), is(1) );
		assertThat( dmnResult.getDecisionResultByName( "Greeting Message" ).getResult(), is( "Hello John Doe" ) );

		DMNContext result = dmnResult.getContext();

		assertThat( result.get( "Greeting Message" ), is( "Hello John Doe" ) );
	}
	
    @Test
    public void testSimpleDecisionTableUniqueHitPolicy() {
        DMNRuntime runtime = DMNRuntimeUtil.createRuntime( "0004-simpletable-U.dmn", this.getClass() );
        DMNModel dmnModel = runtime.getModel( "https://github.com/droolsjbpm/kie-dmn", "0004-simpletable-U" );
        assertThat( dmnModel, notNullValue() );

        DMNContext context = DMNFactory.newContext();
        context.set( "Age", new BigDecimal( 18 ) );
        context.set( "RiskCategory", "Medium" );
        context.set( "isAffordable", true );

        DMNResult dmnResult = runtime.evaluateAll( dmnModel, context );

        DMNContext result = dmnResult.getContext();

        assertThat( result.get( "Approval Status" ), is( "Approved" ) );
    }

    @Test
    public void testSimpleDecisionTableUniqueHitPolicySatisfies() {
        DMNRuntime runtime = DMNRuntimeUtil.createRuntime( "0004-simpletable-U.dmn", this.getClass() );
        DMNModel dmnModel = runtime.getModel( "https://github.com/droolsjbpm/kie-dmn", "0004-simpletable-U" );
        assertThat( dmnModel, notNullValue() );

        DMNContext context = DMNFactory.newContext();
        context.set( "Age", new BigDecimal( 18 ) );
        context.set( "RiskCategory", "ASD" );
        context.set( "isAffordable", false );

        DMNResult dmnResult = runtime.evaluateAll( dmnModel, context );

        DMNContext result = dmnResult.getContext();

        assertThat( result.get( "Approval Status" ), nullValue() );
        assertTrue( dmnResult.getMessages().size() > 0 );
    }
}
