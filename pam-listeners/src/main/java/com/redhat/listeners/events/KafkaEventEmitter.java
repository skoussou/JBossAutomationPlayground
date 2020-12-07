package com.redhat.listeners.events;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.jbpm.persistence.api.integration.EventCollection;
import org.jbpm.persistence.api.integration.EventEmitter;
import org.jbpm.persistence.api.integration.InstanceView;
import org.jbpm.persistence.api.integration.base.BaseEventCollection;
import org.jbpm.persistence.api.integration.model.CaseInstanceView;
import org.jbpm.persistence.api.integration.model.ProcessInstanceView;
import org.jbpm.persistence.api.integration.model.TaskInstanceView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Calendar;
/**
 *
 *  @author skoussou
 *
 * Basic Kafka Listener implementation of EventEmitter that simply pushes out data to
 * Kafka Topic. It performs all the operation in the background thread but does not
 * do any intermediate data persistence, meaning it can result in data lost in case of server
 * crashes.
 *
 * This event emitter expects following parameters to configure itself - via system properties
 * <ul>
 *  <li>bootstrap.servers - list of brokers used for bootstrapping knowledge about the rest of the cluster. Format: host1:port1,host2:port2</li>
 *  <li>kafka.emitter.client.name - The kafka client name </id>
 *  <li>key.serializer - Serializer class for key that implements the org.apache.kafka.common.serialization.Serializer interface.</li>
 *  <li>value.serializer - Serializer class for key that implements the org.apache.kafka.common.serialization.Serializer interface.</li>
 *  <li>kafka.emitter.topic.name - The topic to emmit events to.</li>
 * </ul>
 *
 * NOTE: Optional authentication is a BASIC authentication.
 */

public class KafkaEventEmitter implements EventEmitter {

    private static final Logger logger = LoggerFactory.getLogger(KafkaEventEmitter.class);

    private String bootstrapServers = System.getProperty("bootstrap.servers", "localhost:9092");
    private String clientId = System.getProperty("kafka.emitter.client.name ", "rhpam-events-emitter");
    private String keySerializerClass = System.getProperty("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    private String valueSerializerClass = System.getProperty("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    private String topicName = System.getProperty("kafka.emitter.topic.name", "rhpam-topic");

    private Producer<String, String> producer;

    private ObjectMapper mapper = new ObjectMapper();

    private ExecutorService executor;


    private void sendMessage(String key, String taskEvent) {

        try {

            producer.send(new ProducerRecord(topicName,
                    key,
                    taskEvent))
                    .get();


        } catch (Exception exp) {
            logger.error("Handler error", exp);
        }
    }


    public KafkaEventEmitter() {

        Properties config = new Properties();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        //config.put(ProducerConfig.CLIENT_ID_CONFIG, clientId);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializerClass);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializerClass);

        executor = buildExecutorService();
        try {
            producer = new KafkaProducer<String, String>(config);
        } finally {
        }

    }

    public void deliver(Collection<InstanceView<?>> data) {
        // no-op
    }

    public void apply(Collection<InstanceView<?>> data) {

        logger.info("KafkaEventEmitter: starting to apply");
        if (data.isEmpty()) {
            return;
        }

        executor.execute(() -> {
            StringBuilder content = new StringBuilder();

            logger.info("KafkaEventEmitter: starting to execute");

            for (InstanceView<?> view : data) {
                try {
                    String json = mapper.writeValueAsString(view);

                    String index = "jbpm";
                    String type = "unknown";
                    String id = "";
                    String key = "";

                    if (view instanceof ProcessInstanceView) {
                        index = "processes";
                        type = "process";
                        id = ((ProcessInstanceView) view).getCompositeId();
                        key = ((ProcessInstanceView) view).getId()+String.valueOf(Calendar.getInstance().getTimeInMillis());
                    } else if (view instanceof TaskInstanceView) {
                        index = "tasks";
                        type = "task";
                        id = ((TaskInstanceView) view).getCompositeId();
                        key = ((TaskInstanceView) view).getId()+String.valueOf(Calendar.getInstance().getTimeInMillis());
                    } else if (view instanceof CaseInstanceView) {
                        index = "cases";
                        type = "case";
                        id = ((CaseInstanceView) view).getCompositeId();
                        key = ((CaseInstanceView) view).getId()+String.valueOf(Calendar.getInstance().getTimeInMillis());
                    }

                    content.append("{ \"index\" : { \"_index\" : \"" + index + "\", \"_type\" : \"" + type + "\", \"_id\" : \"" + id + "\" } }\n");
                    content.append(json);
                    content.append("\n");

                    logger.info("KafkaEventEmitter: sendMessage: "+content.toString());

                    sendMessage(key, content.toString());


                } catch (JsonProcessingException e) {
                    logger.error("Error while serializing {} to JSON", view, e);
                }
            }

            try {

            } catch (Exception e) {
                logger.error("Unexpected exception while sending data to ElasticSearch", e);
            }
        });
    }

    public void drop(Collection<InstanceView<?>> data) {
        // no-op
    }

    public EventCollection newCollection() {
        return new BaseEventCollection();
    }

    @Override
    public void close() {
        producer.close();
        executor.shutdown();
        logger.info("Kafka Event emitter closed successfully");
    }

    private Producer<String, String> buildKafkaProducer() {
        Properties config = new Properties();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.CLIENT_ID_CONFIG, clientId);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializerClass);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializerClass);

        try {
            producer = new KafkaProducer<String, String>(config);
            return producer;
        } finally {
        }
    }


    protected ExecutorService buildExecutorService() {
        return Executors.newCachedThreadPool();
    }
}
