package com.redhat.listeners.tasks;

import org.kie.api.task.TaskEvent;
import org.kie.api.task.TaskLifeCycleEventListener;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * @author skoussou
 *
 * Listens and sends to rhpam-topic Tasks Actions
 * [beforeTaskClaimedEvent] Task Task [6] Thu Dec 03 12:38:29 GMT 2020
 * [afterTaskClaimedEvent] Task Task [6] Thu Dec 03 12:38:29 GMT 2020
 * [beforeTaskStartedEvent] Task Task [6] Thu Dec 03 12:38:29 GMT 2020
 * [afterTaskStartedEvent] Task Task [6] Thu Dec 03 12:38:29 GMT 2020
 * [beforeTaskCompletedEvent] Task Task [6] Thu Dec 03 12:38:29 GMT 2020
 * [afterTaskCompletedEvent] Task Task [6] Thu Dec 03 12:38:29 GMT 2020
 *
 * ./kafka2.5.0/bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic rhpam-topic --from-beginning
 */
public class KafkaTaskListener implements TaskLifeCycleEventListener {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaTaskListener.class);

    private String bootstrapServers = "localhost:9092";
    private String clientId = "rhpam";
    private String keySerializerClass = "org.apache.kafka.common.serialization.StringSerializer";
    private String valueSerializerClass = "org.apache.kafka.common.serialization.StringSerializer";
    private String topic = "rhpam-topic";

    private Producer<String, String> producer;

    public KafkaTaskListener(String bootstrapServers,
                                String clientId,
                                String keySerializerClass,
                                String valueSerializerClass,
                                String topicName,
                                ClassLoader classLoader) {

        if (topicName != null && !topicName.equals("")){
            topic = topicName;
        }
        Properties config = new Properties();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.CLIENT_ID_CONFIG, clientId);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializerClass);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializerClass);
        // it is needed to change the classloader to KIEURLClassLoader for dependencies to be resolved and then, set it back
        //ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            //Thread.currentThread().setContextClassLoader(classLoader);
            producer = new KafkaProducer<String, String>(config);
        } finally {
            //Thread.currentThread().setContextClassLoader(oldClassLoader);
        }

    }

    public KafkaTaskListener(ClassLoader classLoader) {
        Properties config = new Properties();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.CLIENT_ID_CONFIG, clientId);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializerClass);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializerClass);
        // it is needed to change the classloader to KIEURLClassLoader for dependencies to be resolved and then, set it back
        ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(classLoader);
            producer = new KafkaProducer<String, String>(config);
        } finally {
            Thread.currentThread().setContextClassLoader(oldClassLoader);
        }
    }


    private void sendMessage(String key, String taskEvent) {

        try {

            producer.send(new ProducerRecord(topic,
                    key,
                    taskEvent))
                    .get();


        } catch (Exception exp) {
            LOG.error("Handler error",  exp);
        }
    }



    public void beforeTaskActivatedEvent(TaskEvent event) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        sendMessage(event.getTask().getId()+"-"+event.getEventDate().getTime(), "["+stackTrace[1].getMethodName()+"] Task "+event.getTask().getName()+" ["+event.getTask().getId()+"] "+ event.getEventDate().toString());
    }

    public void beforeTaskClaimedEvent(TaskEvent event) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        sendMessage(event.getTask().getId()+"-"+event.getEventDate().getTime(), "["+stackTrace[1].getMethodName()+"] Task "+event.getTask().getName()+" ["+event.getTask().getId()+"] "+ event.getEventDate().toString());

    }

    public void beforeTaskSkippedEvent(TaskEvent event) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        sendMessage(event.getTask().getId()+"-"+event.getEventDate().getTime(), "["+stackTrace[1].getMethodName()+"] Task "+event.getTask().getName()+" ["+event.getTask().getId()+"] "+ event.getEventDate().toString());

    }

    public void beforeTaskStartedEvent(TaskEvent event) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        sendMessage(event.getTask().getId()+"-"+event.getEventDate().getTime(), "["+stackTrace[1].getMethodName()+"] Task "+event.getTask().getName()+" ["+event.getTask().getId()+"] "+ event.getEventDate().toString());

    }

    public void beforeTaskStoppedEvent(TaskEvent event) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        sendMessage(event.getTask().getId()+"-"+event.getEventDate().getTime(), "["+stackTrace[1].getMethodName()+"] Task "+event.getTask().getName()+" ["+event.getTask().getId()+"] "+ event.getEventDate().toString());

    }

    public void beforeTaskCompletedEvent(TaskEvent event) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        sendMessage(event.getTask().getId()+"-"+event.getEventDate().getTime(), "["+stackTrace[1].getMethodName()+"] Task "+event.getTask().getName()+" ["+event.getTask().getId()+"] "+ event.getEventDate().toString());

    }

    public void beforeTaskFailedEvent(TaskEvent event) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        sendMessage(event.getTask().getId()+"-"+event.getEventDate().getTime(), "["+stackTrace[1].getMethodName()+"] Task "+event.getTask().getName()+" ["+event.getTask().getId()+"] "+ event.getEventDate().toString());

    }

    public void beforeTaskAddedEvent(TaskEvent event) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        sendMessage(event.getTask().getId()+"-"+event.getEventDate().getTime(), "["+stackTrace[1].getMethodName()+"] Task "+event.getTask().getName()+" ["+event.getTask().getId()+"] "+ event.getEventDate().toString());

    }

    public void beforeTaskExitedEvent(TaskEvent event) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        sendMessage(event.getTask().getId()+"-"+event.getEventDate().getTime(), "["+stackTrace[1].getMethodName()+"] Task "+event.getTask().getName()+" ["+event.getTask().getId()+"] "+ event.getEventDate().toString());

    }

    public void beforeTaskReleasedEvent(TaskEvent event) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        sendMessage(event.getTask().getId()+"-"+event.getEventDate().getTime(), "["+stackTrace[1].getMethodName()+"] Task "+event.getTask().getName()+" ["+event.getTask().getId()+"] "+ event.getEventDate().toString());

    }

    public void beforeTaskResumedEvent(TaskEvent event) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        sendMessage(event.getTask().getId()+"-"+event.getEventDate().getTime(), "["+stackTrace[1].getMethodName()+"] Task "+event.getTask().getName()+" ["+event.getTask().getId()+"] "+ event.getEventDate().toString());

    }

    public void beforeTaskSuspendedEvent(TaskEvent event) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        sendMessage(event.getTask().getId()+"-"+event.getEventDate().getTime(), "["+stackTrace[1].getMethodName()+"] Task "+event.getTask().getName()+" ["+event.getTask().getId()+"] "+ event.getEventDate().toString());

    }

    public void beforeTaskForwardedEvent(TaskEvent event) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        sendMessage(event.getTask().getId()+"-"+event.getEventDate().getTime(), "["+stackTrace[1].getMethodName()+"] Task "+event.getTask().getName()+" ["+event.getTask().getId()+"] "+ event.getEventDate().toString());

    }

    public void beforeTaskDelegatedEvent(TaskEvent event) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        sendMessage(event.getTask().getId()+"-"+event.getEventDate().getTime(), "["+stackTrace[1].getMethodName()+"] Task "+event.getTask().getName()+" ["+event.getTask().getId()+"] "+ event.getEventDate().toString());

    }

    public void beforeTaskNominatedEvent(TaskEvent event) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        sendMessage(event.getTask().getId()+"-"+event.getEventDate().getTime(), "["+stackTrace[1].getMethodName()+"] Task "+event.getTask().getName()+" ["+event.getTask().getId()+"] "+ event.getEventDate().toString());

    }

    public void afterTaskActivatedEvent(TaskEvent event) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        sendMessage(event.getTask().getId()+"-"+event.getEventDate().getTime(), "["+stackTrace[1].getMethodName()+"] Task "+event.getTask().getName()+" ["+event.getTask().getId()+"] "+ event.getEventDate().toString());

    }

    public void afterTaskClaimedEvent(TaskEvent event) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        sendMessage(event.getTask().getId()+"-"+event.getEventDate().getTime(), "["+stackTrace[1].getMethodName()+"] Task "+event.getTask().getName()+" ["+event.getTask().getId()+"] "+ event.getEventDate().toString());

    }

    public void afterTaskSkippedEvent(TaskEvent event) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        sendMessage(event.getTask().getId()+"-"+event.getEventDate().getTime(), "["+stackTrace[1].getMethodName()+"] Task "+event.getTask().getName()+" ["+event.getTask().getId()+"] "+ event.getEventDate().toString());

    }

    public void afterTaskStartedEvent(TaskEvent event) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        sendMessage(event.getTask().getId()+"-"+event.getEventDate().getTime(), "["+stackTrace[1].getMethodName()+"] Task "+event.getTask().getName()+" ["+event.getTask().getId()+"] "+ event.getEventDate().toString());

    }

    public void afterTaskStoppedEvent(TaskEvent event) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        sendMessage(event.getTask().getId()+"-"+event.getEventDate().getTime(), "["+stackTrace[1].getMethodName()+"] Task "+event.getTask().getName()+" ["+event.getTask().getId()+"] "+ event.getEventDate().toString());

    }

    public void afterTaskCompletedEvent(TaskEvent event) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        sendMessage(event.getTask().getId()+"-"+event.getEventDate().getTime(), "["+stackTrace[1].getMethodName()+"] Task "+event.getTask().getName()+" ["+event.getTask().getId()+"] "+ event.getEventDate().toString());

    }

    public void afterTaskFailedEvent(TaskEvent event) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        sendMessage(event.getTask().getId()+"-"+event.getEventDate().getTime(), "["+stackTrace[1].getMethodName()+"] Task "+event.getTask().getName()+" ["+event.getTask().getId()+"] "+ event.getEventDate().toString());

    }

    public void afterTaskAddedEvent(TaskEvent event) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        sendMessage(event.getTask().getId()+"-"+event.getEventDate().getTime(), "["+stackTrace[1].getMethodName()+"] Task "+event.getTask().getName()+" ["+event.getTask().getId()+"] "+ event.getEventDate().toString());

    }

    public void afterTaskExitedEvent(TaskEvent event) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        sendMessage(event.getTask().getId()+"-"+event.getEventDate().getTime(), "["+stackTrace[1].getMethodName()+"] Task "+event.getTask().getName()+" ["+event.getTask().getId()+"] "+ event.getEventDate().toString());

    }

    public void afterTaskReleasedEvent(TaskEvent event) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        sendMessage(event.getTask().getId()+"-"+event.getEventDate().getTime(), "["+stackTrace[1].getMethodName()+"] Task "+event.getTask().getName()+" ["+event.getTask().getId()+"] "+ event.getEventDate().toString());

    }

    public void afterTaskResumedEvent(TaskEvent event) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        sendMessage(event.getTask().getId()+"-"+event.getEventDate().getTime(), "["+stackTrace[1].getMethodName()+"] Task "+event.getTask().getName()+" ["+event.getTask().getId()+"] "+ event.getEventDate().toString());

    }

    public void afterTaskSuspendedEvent(TaskEvent event) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        sendMessage(event.getTask().getId()+"-"+event.getEventDate().getTime(), "["+stackTrace[1].getMethodName()+"] Task "+event.getTask().getName()+" ["+event.getTask().getId()+"] "+ event.getEventDate().toString());

    }

    public void afterTaskForwardedEvent(TaskEvent event) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        sendMessage(event.getTask().getId()+"-"+event.getEventDate().getTime(), "["+stackTrace[1].getMethodName()+"] Task "+event.getTask().getName()+" ["+event.getTask().getId()+"] "+ event.getEventDate().toString());

    }

    public void afterTaskDelegatedEvent(TaskEvent event) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        sendMessage(event.getTask().getId()+"-"+event.getEventDate().getTime(), "["+stackTrace[1].getMethodName()+"] Task "+event.getTask().getName()+" ["+event.getTask().getId()+"] "+ event.getEventDate().toString());

    }

    public void afterTaskNominatedEvent(TaskEvent event) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        sendMessage(event.getTask().getId()+"-"+event.getEventDate().getTime(), "["+stackTrace[1].getMethodName()+"] Task "+event.getTask().getName()+" ["+event.getTask().getId()+"] "+ event.getEventDate().toString());
    }


}