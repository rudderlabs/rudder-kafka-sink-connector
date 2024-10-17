package com.rudderstack.kafka.connect.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

final class VersionTest {
    @Test
    void shouldGetVersionFromVersionFile() {
        assertNotNull(Version.getVersion());
    }
}
