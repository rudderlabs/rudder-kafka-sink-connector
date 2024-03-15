package com.rudderstack.kafka.connect.config;

import java.io.IOException;
import com.rudderstack.kafka.connect.utils.ResourceUtil;

public final class Version {
    private static final String VERSION_FILE = "version.txt";

    private static final String CONNECTOR_VERSION = ResourceUtil.getResourceFileAsString(VERSION_FILE);

    public static String getVersion() {
        return CONNECTOR_VERSION;
    }

    private Version() {
    }
}