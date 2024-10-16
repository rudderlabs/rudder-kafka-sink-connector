package com.rudderstack.kafka.connect.config;

import org.apache.kafka.common.config.ConfigException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class RangeValidatorTest {

    @Test
    void shouldAcceptNumberWithinRange() {
        RangeValidator<Integer> validator = new RangeValidator<>(1, 10);
        assertDoesNotThrow(() -> validator.ensureValid("test", 5));
    }

    @Test
    void shouldRejectNumberNotInRange() {
        RangeValidator<Integer> validator = new RangeValidator<>(1, 10);
        assertThrows(ConfigException.class, () -> validator.ensureValid("test", 15));
    }

    @Test
    void shouldAcceptNullValue() {
        RangeValidator<Integer> validator = new RangeValidator<>(1, 10);
        assertDoesNotThrow(() -> validator.ensureValid("test", null));
    }

    @Test
    void shouldAcceptMaxValue() {
        RangeValidator<Integer> validator = new RangeValidator<>(1, 10);
        assertDoesNotThrow(() -> validator.ensureValid("test", 10));
    }

    @Test
    void shouldAcceptMinValue() {
        RangeValidator<Integer> validator = new RangeValidator<>(1, 10);
        assertDoesNotThrow(() -> validator.ensureValid("test", 1));
    }

    @Test
    void shouldNotAcceptInvalidValues() {
        RangeValidator<Integer> validator = new RangeValidator<>(1, 10);
        assertThrows(ConfigException.class, () -> validator.ensureValid("test", "hello"));
    }
}