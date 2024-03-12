package com.rudderstack.kafka.connect.config;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.apache.kafka.common.config.ConfigException;
import org.junit.jupiter.api.Test;

final class RangeValidatorTest {

    @Test
    void testValidNumberWithinRangeAccepted() {
        RangeValidator<Integer> validator = new RangeValidator<>(1, 10);
        assertDoesNotThrow(() -> validator.ensureValid("test", 5));
    }

    @Test
    void testInvalidNumberWithinRangeNotAccepted() {
        RangeValidator<Integer> validator = new RangeValidator<>(1, 10);
        assertThrows(ConfigException.class, () -> validator.ensureValid("test", 15));
    }

    @Test
    void testNullValueAccepted() {
        RangeValidator<Integer> validator = new RangeValidator<>(1, 10);
        assertDoesNotThrow(() -> validator.ensureValid("test", null));
    }

    @Test
    void testMaxValueAccepted() {
        RangeValidator<Integer> validator = new RangeValidator<>(1, 10);
        assertDoesNotThrow(() -> validator.ensureValid("test", 10));
    }
}