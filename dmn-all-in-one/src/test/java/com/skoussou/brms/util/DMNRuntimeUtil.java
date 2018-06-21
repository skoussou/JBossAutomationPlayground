/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.skoussou.brms.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.kie.api.KieServices;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.dmn.core.api.DMNRuntime;
import org.kie.dmn.core.api.event.AfterEvaluateBKMEvent;
import org.kie.dmn.core.api.event.AfterEvaluateDecisionEvent;
import org.kie.dmn.core.api.event.AfterEvaluateDecisionTableEvent;
import org.kie.dmn.core.api.event.BeforeEvaluateBKMEvent;
import org.kie.dmn.core.api.event.BeforeEvaluateDecisionEvent;
import org.kie.dmn.core.api.event.BeforeEvaluateDecisionTableEvent;
import org.kie.dmn.core.api.event.DMNRuntimeEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DMNRuntimeUtil {

    public static DMNRuntime createRuntime(final String resourceName, final Class testClass) {
        final KieServices ks = KieServices.Factory.get();
        final KieContainer kieContainer = KieHelper.getKieContainer(
                ks.newReleaseId("org.kie", "dmn-test", "1.0"),
                ks.getResources().newClassPathResource(resourceName, testClass));

        
        final DMNRuntime runtime = kieContainer.newKieSession().getKieRuntime(DMNRuntime.class);
        runtime.addListener( DMNRuntimeUtil.createListener() );
        Assert.assertNotNull(runtime);
        return runtime;
    }

    
    public static DMNRuntime createRuntime(final List<String> resourceNames, final Class testClass) {
        final KieServices ks = KieServices.Factory.get();
       
        ArrayList<Resource> listofResources = new ArrayList<Resource>();

//      ArrayList<Resource> listofResources = resourceNames.stream().forEach(s -> ks.getResources().newClassPathResource(s, testClass)).collect(Collectors.toList());
//        listofResources = resourceNames.stream().map(r -> listofResources.add(ks.getResources().newClassPathResource(r, testClass))).collect( Collectors.toList());

        resourceNames.stream().forEach( r -> listofResources.add(ks.getResources().newClassPathResource(r, testClass)));
        
       
        //List<String> names = cars.stream().map( car -> car.getName() ).collect( Collectors.toList() );

                        
        final KieContainer kieContainer = KieHelper.getKieContainer(
                ks.newReleaseId("org.kie", "dmn-test", "1.0"),
                listofResources,
                testClass);
        
        final DMNRuntime runtime = kieContainer.newKieSession().getKieRuntime(DMNRuntime.class);
        runtime.addListener( DMNRuntimeUtil.createListener() );
        Assert.assertNotNull(runtime);
        return runtime;
    }

    public static DMNRuntimeEventListener createListener() {
        return new DMNRuntimeEventListener() {
            private final Logger logger = LoggerFactory.getLogger(DMNRuntimeEventListener.class);

            @Override
            public void beforeEvaluateDecision(BeforeEvaluateDecisionEvent event) {
                logger.info(event.toString());
                System.out.println(event.toString());
            }

            @Override
            public void afterEvaluateDecision(AfterEvaluateDecisionEvent event) {
                logger.info(event.toString());
                System.out.println(event.toString()+" : "+event.getResult().getDecisionResults().get(0).getResult());
                System.out.println(event.getResult().getDecisionResults().get(0) != null ? event.getResult().getDecisionResults().get(0).getDecisionName() +" : "+event.getResult().getDecisionResults().get(0).getResult() : "NONE FOUND");
                System.out.println(event.toString()+" : "+event.getResult());

            }

            @Override
            public void beforeEvaluateDecisionTable(BeforeEvaluateDecisionTableEvent event) {
               logger.info(event.toString());
               System.out.println(event.toString());
            }

            @Override
            public void afterEvaluateDecisionTable(AfterEvaluateDecisionTableEvent event) {
                logger.info(event.toString());
                System.out.println(event.toString());
                System.out.println(event.toString()+" : "+event.getResult().getDecisionResults().get(0).getResult());
                System.out.println(event.getResult().getDecisionResults().get(0) != null ? event.getResult().getDecisionResults().get(0).getDecisionName() +" : "+event.getResult().getDecisionResults().get(0).getResult() : "NONE FOUND");
                System.out.println(event.toString()+" : "+event.getResult());                
            }

			@Override
			public void afterEvaluateBKM(AfterEvaluateBKMEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void beforeEvaluateBKM(BeforeEvaluateBKMEvent arg0) {
				// TODO Auto-generated method stub
				
			}
        };
    }

    private DMNRuntimeUtil() {
        // No constructor for util class.
    }
}
