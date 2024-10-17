package com.rudderstack.kafka.connect.config;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;

import java.util.Map;

public final class RudderSinkConfig extends AbstractConfig {

    public static final String CONNECTOR_NAME_CONFIG = "name";
    public static final String CONNECTOR_NAME_DEFAULT = "rudderstack-kafka-connect-sink";
    public static final String KAFKA_RETRY_BACKOFF_MS_CONFIG = "kafka.retry.backoff.ms";
    private static final String RUDDER_DATA_PLANE_URL_CONFIG = "rudder.data.plane.url";
    private static final String RUDDER_WRITE_KEY_CONFIG = "rudder.write.key";

    public RudderSinkConfig(ConfigDef definition, Map<?, ?> originals) {
        super(definition, originals);
    }

    public RudderSinkConfig(final Map<String, String> properties) {
        super(configDef(), properties);
    }

    private static void addRudderConfigGroup(final ConfigDef configDef) {
        configDef.define(
                RUDDER_DATA_PLANE_URL_CONFIG,
                ConfigDef.Type.STRING,
                ConfigDef.NO_DEFAULT_VALUE,
                new UrlValidator(),
                ConfigDef.Importance.HIGH,
                "The Rudderstack Data plane URL to send data to."
        );
        configDef.define(
                RUDDER_WRITE_KEY_CONFIG,
                ConfigDef.Type.STRING,
                ConfigDef.NO_DEFAULT_VALUE,
                null,
                ConfigDef.Importance.HIGH,
                "The Connector name."
        );
        configDef.define(
                CONNECTOR_NAME_CONFIG,
                ConfigDef.Type.STRING,
                CONNECTOR_NAME_DEFAULT,
                null,
                ConfigDef.Importance.HIGH,
                "The Rudderstack kafka source write key."
        );
        configDef.define(
                KAFKA_RETRY_BACKOFF_MS_CONFIG,
                ConfigDef.Type.LONG,
                null,
                new RangeValidator<>(0L, 24 * 60 * 60 * 1000L),
                ConfigDef.Importance.MEDIUM,
                "The retry backoff duration in milliseconds."
                        + "This is to notify it to retry delivering a batch of messages"
                        + "or performing recovery in the event of temporary failures."
        );
    }

    public static ConfigDef configDef() {
        final ConfigDef configDef = new ConfigDef();
        addRudderConfigGroup(configDef);
        return configDef;
    }

    public String dataPlaneUrl() {
        return this.getString(RUDDER_DATA_PLANE_URL_CONFIG);
    }

    public String writeKey() {
        return this.getString(RUDDER_WRITE_KEY_CONFIG);
    }

    public String connectorName() {
        return this.getString(CONNECTOR_NAME_CONFIG);
    }

    public Long kafkaRetryBackoffMs() {
        return this.getLong(KAFKA_RETRY_BACKOFF_MS_CONFIG);
    }
}
