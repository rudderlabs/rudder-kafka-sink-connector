package com.rudderstack.kafka.connect.utils;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;

final class ResourceUtilTest {

    @Test
    void shouldReturnNullWhenFileNotExisting() {
        var properties = ResourceUtil.getProperties("unknown.txt");
        assertTrue(properties.isEmpty());
    }

    @Test
    void shouldReturnNullWhenFileNameIsNull() throws IOException {
        var properties = ResourceUtil.getProperties(null);
        assertTrue(properties.isEmpty());
    }
}
