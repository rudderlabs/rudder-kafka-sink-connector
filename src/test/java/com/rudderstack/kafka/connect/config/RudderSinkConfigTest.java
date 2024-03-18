package com.rudderstack.kafka.connect.config;

import java.util.Map;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.apache.kafka.common.config.ConfigException;
import org.junit.jupiter.api.Test;

final class RudderSinkConfigTest {

    @Test
    void shouldCreateConfigWhenPropertiesAreValid() {
        Map<String, String> properties = new HashMap<>();
        properties.put("rudder.data.plane.url", "https://example.com");
        properties.put("rudder.write.key", "write_key");
        properties.put("name", "connector_name");
        properties.put("kafka.retry.backoff.ms", "1000");

        RudderSinkConfig config = new RudderSinkConfig(RudderSinkConfig.configDef(), properties);

        assertEquals("https://example.com", config.dataPlaneUrl());
        assertEquals("write_key", config.writeKey());
        assertEquals("connector_name", config.connectorName());
        assertEquals(1000L, config.kafkaRetryBackoffMs());
    }

    @Test
    void shouldThrowsWhenDataPlaneUrlIsMissing() {
        Map<String, String> properties = new HashMap<>();
        properties.put("rudder.write.key", "write_key");
        properties.put("name", "connector_name");

        assertThrows(ConfigException.class, () -> new RudderSinkConfig(properties));
    }

    @Test
    void shouldThrowWhenWriteKeyIsMissing() {
        Map<String, String> properties = new HashMap<>();
        properties.put("rudder.data.plane.url", "https://example.com");
        properties.put("name", "connector_name");

        assertThrows(ConfigException.class, () -> new RudderSinkConfig(properties));
    }

    @Test
    void shouldThrowWhenKafkaBackoffIsInvalid() {
        Map<String, String> properties = new HashMap<>();
        properties.put("rudder.data.plane.url", "https://example.com");
        properties.put("rudder.write.key", "write_key");
        properties.put("name", "connector_name");
        properties.put("kafka.retry.backoff.ms", "-1");

        assertThrows(ConfigException.class, () -> new RudderSinkConfig(properties));
    }
}