package com.rudderstack.kafka.connect;

import com.rudderstack.kafka.connect.config.RudderSinkConfig;
import com.rudderstack.kafka.connect.config.Version;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.sink.SinkConnector;
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
public class RudderstackSinkConnector extends SinkConnector {
    private static final Logger log = LoggerFactory.getLogger(RudderstackSinkConnector.class);

    private Map<String, String> configProps;
    private RudderSinkConfig config;

    public RudderstackSinkConnector() {
        // required by Kafka Connect
    }

    @Override
    public void start(Map<String, String> props) {
        Objects.requireNonNull(props);
        this.configProps = Collections.unmodifiableMap(props);
        this.config = new RudderSinkConfig(props);
        if (log.isInfoEnabled()) {
            log.info("Starting connector {}", config.connectorName());
        }
    }

    @Override
    public Class<? extends Task> taskClass() {
        return RudderstackSinkTask.class;
    }

    @Override
    public List<Map<String, String>> taskConfigs(int maxTasks) {
        return Collections.nCopies(maxTasks, Map.copyOf(configProps));
    }

    @Override
    public void stop() {
        // Nothing to do.
        if (log.isInfoEnabled()) {
            log.info("Stopping connector {}", config.connectorName());
        }
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
