/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.redhat.listeners;

import java.util.Collection;
import java.util.stream.Collectors;

import org.kie.api.event.process.DefaultProcessEventListener;
import org.kie.api.event.process.SLAViolatedEvent;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.CaseAssignment;
import org.kie.api.task.model.Group;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.User;
import org.kie.internal.runtime.Cacheable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;

import org.jbpm.workflow.instance.impl.WorkflowProcessInstanceImpl;
import org.jbpm.services.api.RuntimeDataService;
import org.jbpm.services.api.model.ProcessInstanceDesc;
import org.jbpm.services.api.service.ServiceRegistry;

import java.io.IOException;

public class RabbitMQEscalationSLAViolationListener extends DefaultProcessEventListener {

  // private static final Logger logger =
  // LoggerFactory.getLogger(EscalateToAdminSLAViolationListener.class);

  public final String EXCHANGE_NAME = "rhpam-exchange";
  public final String QUEUE_ROUTINGKEY = "routingKey2-violation";
  public final String QUEUE_NAME = "pam-rabbitmq-cheques-slaviolation-queue";

  // RabbbitMQ configurations
  private String rabbitmqUsername;
  private String rabbitmqPassword;
  private String rabbitmqHost;
  private String rabbitmqPort;

  private RuntimeDataService runtimeDataService;

  private void retrieveRabbitMQConnectionDetails() {
    /*
     * rabbitmqUsername = System.getProperty("SPRING_RABBITMQ_HOST", "guest");
     * rabbitmqPassword = System.getProperty("SPRING_RABBITMQ_PORT", "guest");
     * rabbitmqHost = System.getProperty("SPRING_RABBITMQ_USERNAME", "127.0.0.1");
     * rabbitmqPort = System.getProperty("SPRING_RABBITMQ_PASSWORD", "5672");
     */
    rabbitmqUsername = System.getenv().getOrDefault("SPRING_RABBITMQ_USERNAME", "pamservice");
    rabbitmqPassword = System.getenv().getOrDefault("SPRING_RABBITMQ_PASSWORD", "pamservice");
    rabbitmqHost = System.getenv().getOrDefault("SPRING_RABBITMQ_HOST", "127.0.0.1");
    rabbitmqPort = System.getenv().getOrDefault("SPRING_RABBITMQ_PORT", "5672");
  }

  @Override
  public void afterSLAViolated(SLAViolatedEvent event) {

    retrieveRabbitMQConnectionDetails();

    runtimeDataService = (RuntimeDataService) ServiceRegistry.get().service(ServiceRegistry.RUNTIME_DATA_SERVICE);

    System.out.println("=====================================");
    System.out.println(" SLA Violtion for PROCESS INSTANCE ID [" + event.getProcessInstance() + "]");
    System.out.println(" Process Instance Description [" + event.getProcessInstance().getId() + "] --> ["
        + runtimeDataService.getProcessInstanceById(event.getProcessInstance().getId()) + "]");
    ProcessInstanceDesc pDesc = runtimeDataService.getProcessInstanceById(event.getProcessInstance().getId());
    System.out.println("     CorrelationKey [" + pDesc.getCorrelationKey() + "]");
    System.out.println("=====================================");

    ConnectionFactory factory = new ConnectionFactory();
    Channel channel = null;
    Connection conn = null;
    try {

      // String remoteURI = String.format("amqps://%s:%s?%s", config.getServiceName(),
      // config.getServicePort(), config.getParameters());
      String remoteURI = String.format("amqp://%s:%s@%s:%s", rabbitmqUsername, rabbitmqPassword, rabbitmqHost,
          rabbitmqPort);

      System.out.println("Sending to RabbitMQ [" + remoteURI + "]");
      // factory.setUri("amqp://guest:guestd@rabbitmq:5672");
      factory.setUri(remoteURI);
      conn = factory.newConnection();

      channel = conn.createChannel();
      channel.exchangeDeclare(EXCHANGE_NAME, "direct", true);
      channel.queueDeclare(QUEUE_NAME, true, false, false, null);
      channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, QUEUE_ROUTINGKEY);

      String id = String.valueOf(event.getProcessInstance());
      String pName = event.getProcessInstance().getProcessName();
      String msg = id + "-" + pName;

      byte[] messageBodyBytes = msg.getBytes();
      channel.basicPublish(EXCHANGE_NAME, QUEUE_ROUTINGKEY, null, messageBodyBytes);

    } catch (Exception e) {
      e.printStackTrace();
    } finally {

      try {
        if (channel != null)
          channel.close();
        if (conn != null)
          conn.close();
      } catch (IOException ioe) {
        ioe.printStackTrace();
      } catch (java.util.concurrent.TimeoutException te) {
        te.printStackTrace();
      }
    }
  }

}
