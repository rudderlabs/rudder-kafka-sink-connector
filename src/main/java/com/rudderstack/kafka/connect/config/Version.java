package com.rudderstack.kafka.connect.config;

import com.rudderstack.kafka.connect.utils.ResourceUtil;

import java.util.Properties;

public final class Version {

    private static final Properties PROJECT_PROPERTIES = ResourceUtil.getProperties(ResourceUtil.PROJECT_PROPERTIES_FILE_NAME);

    private Version() {
    }

    public static String getVersion() {
        return PROJECT_PROPERTIES.getProperty("version");
    }
}