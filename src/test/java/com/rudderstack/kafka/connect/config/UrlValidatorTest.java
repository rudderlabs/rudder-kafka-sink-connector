package com.rudderstack.kafka.connect.config;

import org.apache.kafka.common.config.ConfigException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class UrlValidatorTest {

    private final UrlValidator validator = new UrlValidator();

    @ParameterizedTest
    @CsvSource({
            "https://www.example.com, true",        // Valid URL
            "null, false",                          // Null URL
            "https://www.example.com/s=^, false",   // Bad URL
            "https://, false",                      // Bad URL
            "invalid-url, false",                   // NOT URL
            "1, false"                              // Invalid type
    })
    void testUrlValidation(String url, boolean isValid) {
        if ("null".equals(url)) {
            url = null;
        }
        final String finalUrl = url;

        if (isValid) {
            assertDoesNotThrow(() -> validator.ensureValid("url", finalUrl));
        } else {
            assertThrows(ConfigException.class, () -> validator.ensureValid("url", finalUrl));
        }
    }

    @Test
    void testUrlValidationForNonString() {
        assertThrows(ConfigException.class, () -> validator.ensureValid("url", 1));
    }
}
