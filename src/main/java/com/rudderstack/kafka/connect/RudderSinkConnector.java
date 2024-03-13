package com.rudderstack.kafka.connect;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.sink.SinkConnector;

import com.rudderstack.kafka.connect.config.RudderSinkConfig;
import com.rudderstack.kafka.connect.config.Version;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * The RudderSinkConnector class is a Kafka Connect SinkConnector implementation
 * that sends data to Rudderstack.
 */
public class RudderSinkConnector extends SinkConnector {
    private static final Logger log = LoggerFactory.getLogger(RudderSinkConnector.class);

    private Map<String, String> configProps;
    private RudderSinkConfig config;

    public RudderSinkConnector() {
        // required by Kafka Connect
    }

    @Override
    public void start(Map<String, String> props) {
        Objects.requireNonNull(props);
        this.configProps = Collections.unmodifiableMap(props);
        this.config = new RudderSinkConfig(props);
        log.info("Starting connector {}", config.connectorName());
    }

    @Override
    public Class<? extends Task> taskClass() {
        return RudderSinkTask.class;
    }

    @Override
    public List<Map<String, String>> taskConfigs(int maxTasks) {
        return Collections.nCopies(maxTasks, Map.copyOf(configProps));
    }

    @Override
    public void stop() {
        // Nothing to do.
        log.info("Stopping connector {}", config.connectorName());
    }

    @Override
    public ConfigDef config() {
        return RudderSinkConfig.configDef();
    }

    @Override
    public String version() {
        return Version.getVersion();
    }
}
