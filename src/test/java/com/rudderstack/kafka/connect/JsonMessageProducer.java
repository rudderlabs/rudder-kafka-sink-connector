package com.rudderstack.kafka.connect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class JsonMessageProducer {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) {
        // Kafka producer configuration settings
        String topicName = "rudder-json-events";  // Set your topic name
        Properties props = new Properties();

        // Set Kafka server details
        props.put("bootstrap.servers", "localhost:9092");
        props.put("key.serializer", StringSerializer.class.getName());
        props.put("value.serializer", StringSerializer.class.getName());

        // Create the Kafka producer
        KafkaProducer<String, String> producer = new KafkaProducer<>(props);

        // Example Map data
        Map<String, Object> map = new HashMap<>();
        map.put("name", "John Doe");
        map.put("age", 30);
        map.put("email", "john.doe@example.com");

        try {
            // Convert the Map to a JSON string
            String jsonString = objectMapper.writeValueAsString(map);

            // Create a ProducerRecord
            ProducerRecord<String, String> record = new ProducerRecord<>(topicName, jsonString);

            // Send the record
            producer.send(record, (RecordMetadata metadata, Exception exception) -> {
                if (exception != null) {
                    exception.printStackTrace();
                } else {
                    System.out.println("Message sent to topic " + metadata.topic() +
                            " with partition " + metadata.partition() +
                            " at offset " + metadata.offset());
                }
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } finally {
            // Close the producer
            producer.close();
        }
    }
}
