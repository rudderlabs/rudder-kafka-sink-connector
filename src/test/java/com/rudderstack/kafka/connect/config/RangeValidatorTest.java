package com.rudderstack.kafka.connect.config;

import org.apache.kafka.common.config.ConfigException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class RangeValidatorTest {

    @ParameterizedTest
    @CsvSource({
            "5, true",   // Within range
            "15, false", // Out of range
            "null, true", // Null value
            "10, true",  // Max value
            "1, true",   // Min value
    })
    void testRangeValidator(String input, boolean shouldPass) {
        RangeValidator<Integer> validator = new RangeValidator<>(1, 10);
        Integer value = "null".equals(input) ? null : parseInteger(input);

        if (shouldPass) {
            assertDoesNotThrow(() -> validator.ensureValid("test", value));
        } else {
            assertThrows(ConfigException.class, () -> validator.ensureValid("test", value));
        }
    }

    private Integer parseInteger(String input) {
        try {
            return Integer.valueOf(input);
        } catch (NumberFormatException e) {
            return null; // Handle non-integer inputs
        }
    }
}
