package com.rudderstack.kafka.connect;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Properties;

public class AvroMessageProducer {

    // Define the Avro schema as a String
    private static final String USER_SCHEMA = "{"
            + "\"type\":\"record\","
            + "\"name\":\"OrderDetail\","
            + "\"namespace\":\"io.confluent.tutorial\","
            + "\"fields\":["
            + "    {\"name\":\"number\", \"type\":\"long\", \"doc\":\"The order number.\"},"
            + "    {\"name\":\"shipping_address\", \"type\":\"string\", \"doc\":\"The shipping address.\"},"
            + "    {\"name\":\"subtotal\", \"type\":\"double\", \"doc\":\"The amount without shipping cost and tax.\"},"
            + "    {\"name\":\"shipping_cost\", \"type\":\"double\", \"doc\":\"The shipping cost.\"},"
            + "    {\"name\":\"tax\", \"type\":\"double\", \"doc\":\"The applicable tax.\"},"
            + "    {\"name\":\"grand_total\", \"type\":\"double\", \"doc\":\"The order grand total.\"},"
            + "    {\"name\":\"title\", \"type\":\"string\", \"default\":\"\", \"doc\":\"The title of the order.\"},"
            + "    {\"name\":\"content\", \"type\":\"string\", \"default\":\"\", \"doc\":\"The content of the order.\"}"
            + "]"
            + "}";

    public static void main(String[] args) {
        // Load the schema from the string
        Schema schema = new Schema.Parser().parse(USER_SCHEMA);

        // Set producer properties
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092"); // Kafka broker address
        props.put("key.serializer", KafkaAvroSerializer.class.getName());
        props.put("value.serializer", KafkaAvroSerializer.class.getName());
        props.put("schema.registry.url", "http://localhost:8081"); // Schema Registry address

        // Create Kafka producer
        KafkaProducer<String, GenericRecord> producer = new KafkaProducer<>(props);

        // Create a GenericRecord based on the schema
        GenericRecord orderDetail = new GenericData.Record(schema);
        orderDetail.put("number", 12345L);
        orderDetail.put("shipping_address", "1234 Elm St, Springfield");
        orderDetail.put("subtotal", 100.0);
        orderDetail.put("shipping_cost", 10.0);
        orderDetail.put("tax", 5.0);
        orderDetail.put("grand_total", 115.0);
        orderDetail.put("title", "Order for Electronics");
        orderDetail.put("content", "This order includes a laptop and accessories.");

        // Create a ProducerRecord
        ProducerRecord<String, GenericRecord> record = new ProducerRecord<>("rudder-avro-events", orderDetail);

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

        // Close the producer
        producer.close();
    }
}

