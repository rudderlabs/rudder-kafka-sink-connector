package com.rudderstack.kafka.connect.config;

import java.util.Properties;

import com.rudderstack.kafka.connect.utils.ResourceUtil;

public final class Version {

    private static final Properties PROJECT_PROPERTIES = ResourceUtil.getProperties(ResourceUtil.PROJECT_PROPERTIES_FILE_NAME);

    public static String getVersion() {
        return PROJECT_PROPERTIES.getProperty("version");
    }

    private Version() {
    }
}