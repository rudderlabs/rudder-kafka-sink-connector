package com.rudderstack.kafka.connect.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Properties;

public final class ResourceUtil {
    public static final String PROJECT_PROPERTIES_FILE_NAME = "project.properties";
    private static final Logger log = LoggerFactory.getLogger(ResourceUtil.class);

    private ResourceUtil() {
    }

    public static Properties getProperties(String fileName) {
        Properties appProps = new Properties();
        try (InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream(fileName)) {
            if (is != null) {
                appProps.load(is);
            }
        } catch (Exception e) {
            log.warn("Failed to read resource {}: {}", fileName, e.getMessage());
        }
        return appProps;
    }
}
