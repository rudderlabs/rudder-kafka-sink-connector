package com.rudderstack.kafka.connect.config;

import org.apache.kafka.common.config.ConfigException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UrlValidatorTest {

    private UrlValidator validator;

    private static Stream<Object> provideInvalidUrls() {
        return Stream.of(
                null,          // Test for null value
                123,           // Test for non-string integer
                new Object());   // Test for non-string object
    }

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

    @ParameterizedTest
    @MethodSource("provideInvalidUrls")
    void shouldRejectInvalidUrls(Object value) {
        assertThrows(ConfigException.class, () -> validator.ensureValid("url", value));
    }

}