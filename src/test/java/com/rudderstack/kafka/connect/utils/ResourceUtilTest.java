package com.rudderstack.kafka.connect.utils;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNull;

final class ResourceUtilTest {

    @Test
    void shouldResultFileContentsWhenFileExists() {
        ResourceUtil.getResourceFileAsString("version.txt");
    }
    @Test
    void shouldReturnNullWhenFileNotExisting() {
        assertNull(ResourceUtil.getResourceFileAsString("unknown.txt"));
    }

    @Test
     void shouldReturnNullWhenFileNameIsNull() throws IOException {
        assertNull(ResourceUtil.getResourceFileAsString(null));
    }
}
