package com.rudderstack.kafka.connect;

import org.apache.kafka.common.config.ConfigDef;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class RudderSinkConnectorTest {

    // starts successfully with valid configuration
    @Test
    void shouldStartConnectorWithValidConfiguration() {
        Map<String, String> props = new HashMap<>();
        props.put("rudder.data.plane.url", "https://example.com");
        props.put("rudder.write.key", "write_key");
        props.put("name", "connector_name");
        props.put("kafka.retry.backoff.ms", "1000");

        RudderSinkConnector connector = new RudderSinkConnector();
        connector.start(props);
        ConfigDef configDef =  connector.config();
        assertNotNull(configDef);
        assertTrue(configDef.configKeys().containsKey("name"));
        List<Map<String, String>> configMaps = connector.taskConfigs(1);
        assertEquals(props, configMaps.get(0));
        assertDoesNotThrow(connector::stop);
    }

    @Test
    void shouldReturnVersion() {
        assertNotNull(new RudderSinkConnector().version());
    }

    @Test
    void shouldReturnTaskClass() {
        RudderSinkConnector connector = new RudderSinkConnector();
        assertEquals(RudderSinkTask.class, connector.taskClass());
    }
}