package com.rudderstack.kafka.connect.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.InputStream;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class ResourceUtilTest {

    @ParameterizedTest
    @ValueSource(strings = {"unknown.txt"})
    @NullSource
    void shouldReturnEmptyPropertiesForInvalidFileNames(String fileName) {
        var properties = ResourceUtil.getProperties(fileName);
        assertTrue(properties.isEmpty(), "Properties should be empty for file: " + fileName);
    }

    @Test
    void shouldAccessProjectPropertiesFile() {
        // Test using ResourceUtil
        var properties = ResourceUtil.getProperties(ResourceUtil.PROJECT_PROPERTIES_FILE_NAME);
        assertFalse(properties.isEmpty(), "Properties should not be empty for project.properties");

        // Test using direct ClassLoader access
        InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream(ResourceUtil.PROJECT_PROPERTIES_FILE_NAME);
        assertNotNull(is, "project.properties should be accessible via ClassLoader");

        // Test loading properties directly
        Properties directProps = new Properties();
        try {
            directProps.load(is);
            assertFalse(directProps.isEmpty(), "Properties loaded directly should not be empty");
        } catch (Exception e) {
            throw new AssertionError("Failed to load properties: " + e.getMessage(), e);
        }
    }
}
