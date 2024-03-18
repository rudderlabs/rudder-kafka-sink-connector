package com.rudderstack.kafka.connect.config;

import java.util.Map;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;

public final class RudderSinkConfig extends AbstractConfig {

    private static final String RUDDER_GROUP = "Rudderstack";
    private static final String RUDDER_DATA_PLANE_URL_CONFIG = "rudder.data.plane.url";
    private static final String RUDDER_WRITE_KEY_CONFIG = "rudder.write.key";
    public static final String CONNECTOR_NAME_CONFIG = "name";
    public static final String CONNECTOR_NAME_DEFAULT = "rudderstack-kafka-connect-sink";
    public static final String KAFKA_RETRY_BACKOFF_MS_CONFIG = "kafka.retry.backoff.ms";

    public RudderSinkConfig(ConfigDef definition, Map<?, ?> originals) {
        super(definition, originals);
    }

    public RudderSinkConfig(final Map<String, String> properties) {
        super(configDef(), properties);
    }

    private static void addRudderConfigGroup(final ConfigDef configDef) {
        int groupCounter = 0;
        configDef.define(
            RUDDER_DATA_PLANE_URL_CONFIG,
            ConfigDef.Type.STRING,
            ConfigDef.NO_DEFAULT_VALUE,
            new UrlValidator(),
            ConfigDef.Importance.HIGH,
            "The Rudderstack Data plane URL to send data to.",
            RUDDER_GROUP,
            groupCounter++,
            ConfigDef.Width.LONG,
            RUDDER_DATA_PLANE_URL_CONFIG
        );
        configDef.define(
            RUDDER_WRITE_KEY_CONFIG,
            ConfigDef.Type.STRING,
            ConfigDef.NO_DEFAULT_VALUE,
            null,
            ConfigDef.Importance.HIGH,
            "The Connector name.",
            RUDDER_GROUP,
            groupCounter++,
            ConfigDef.Width.LONG,
            RUDDER_WRITE_KEY_CONFIG
        );
        configDef.define(
            CONNECTOR_NAME_CONFIG,
            ConfigDef.Type.STRING,
            CONNECTOR_NAME_DEFAULT,
            null,
            ConfigDef.Importance.HIGH,
            "The Rudderstack kafka source write key.",
            RUDDER_GROUP,
            groupCounter++,
            ConfigDef.Width.LONG,
            CONNECTOR_NAME_CONFIG
        );
        configDef.define(
                KAFKA_RETRY_BACKOFF_MS_CONFIG,
                ConfigDef.Type.LONG,
                null,
                new RangeValidator<Long>(0L, 24*60*60*1000L),
                ConfigDef.Importance.MEDIUM,
                "The retry backoff duration in milliseconds."
                    + "This is to notify it to retry delivering a batch of messages"
                    + "or performing recovery in the event of temporary failures.",
                RUDDER_GROUP,
                groupCounter++,
                ConfigDef.Width.NONE,
                KAFKA_RETRY_BACKOFF_MS_CONFIG
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
