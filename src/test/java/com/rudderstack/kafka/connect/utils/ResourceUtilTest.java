package com.rudderstack.kafka.connect.utils;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertTrue;

final class ResourceUtilTest {

    @ParameterizedTest
    @ValueSource(strings = {"unknown.txt"})
    @NullSource
    void shouldReturnEmptyPropertiesForInvalidFileNames(String fileName) {
        var properties = ResourceUtil.getProperties(fileName);
        assertTrue(properties.isEmpty(), "Properties should be empty for file: " + fileName);
    }
}
