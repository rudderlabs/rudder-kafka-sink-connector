package com.rudderstack.kafka.connect.config;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class VersionTest {
    @Test
    void shouldGetVersionFromVersionFile() {
        assertNotNull(Version.getVersion());
    }
}
