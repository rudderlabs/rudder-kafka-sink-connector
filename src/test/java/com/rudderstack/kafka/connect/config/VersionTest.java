package com.rudderstack.kafka.connect.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class VersionTest {
    @Test
    void shouldGetVersionFromPropertiesFile() {
        String expectedVersion = "unknown";
        String actualVersion = Version.getVersion();
        assertEquals(expectedVersion, actualVersion);
    }
}
