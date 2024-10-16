package com.rudderstack.kafka.connect.config;

import org.apache.kafka.common.config.ConfigException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class UrlValidatorTest {

    private UrlValidator validator;

    @BeforeEach
    void setUp() {
        validator = new UrlValidator();
    }

    @ParameterizedTest
    @CsvSource({
            "https://www.example.com, true",
            "http://localhost:8080, true",
            "ftp://example.com, true",
            "invalid-url, false",
            "http://, false",
            "http:///, false",
    })
    void testUrlValidation(String url, boolean isValid) {
        if (isValid) {
            assertDoesNotThrow(() -> validator.ensureValid("url", url));
        } else {
            assertThrows(ConfigException.class, () -> validator.ensureValid("url", url));
        }
    }

    @Test
    void shouldRejectNullUrl() {
        assertThrows(ConfigException.class, () -> validator.ensureValid("url", null));
    }

    @Test
    void shouldRejectNonStringUrl() {
        assertThrows(ConfigException.class, () -> validator.ensureValid("url", 123));
        assertThrows(ConfigException.class, () -> validator.ensureValid("url", new Object()));
    }
}
