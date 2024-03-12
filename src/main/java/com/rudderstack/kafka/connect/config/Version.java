package com.rudderstack.kafka.connect.config;

import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Version {
    private static final Logger log = LoggerFactory.getLogger(Version.class);

    private static final String PROPERTIES_FILENAME = "rudder-kafka-sink-connector-version.properties";

    static final String CONNECTOR_VERSION;

    static {
        final Properties props = new Properties();
        try (final InputStream resourceStream = Version.class.getClassLoader()
                .getResourceAsStream(PROPERTIES_FILENAME)) {
            props.load(resourceStream);
        } catch (final Exception e) {
            log.warn("Error while loading {}: {}", PROPERTIES_FILENAME, e.getMessage());
        }
        CONNECTOR_VERSION = props.getProperty("version", "unknown").trim();
    }

    public static String getVersion() {
        return CONNECTOR_VERSION;
    }

    private Version() {
    }
}