package com.rudderstack.kafka.connect.config;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.apache.kafka.common.config.ConfigException;
import org.junit.jupiter.api.Test;

class UrlValidatorTest {

    @Test
    void shouldAcceptValidUrl() {
        UrlValidator validator = new UrlValidator();
        String url = "https://www.example.com";
        assertDoesNotThrow(() -> validator.ensureValid("url", url));
    }

    @Test
    void shouldRejectNullUrl() {
        UrlValidator validator = new UrlValidator();
        String url = null;
        assertThrows(ConfigException.class, () -> validator.ensureValid("url", url));
    }

    @Test
    void shouldRejectInvalidUrl() {
        UrlValidator validator = new UrlValidator();
        assertThrows(ConfigException.class, () -> validator.ensureValid("url", "invalid-url"));
        assertThrows(ConfigException.class, () -> validator.ensureValid("url", 1));
    }
}