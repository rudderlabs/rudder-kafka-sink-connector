package com.rudderstack.kafka.connect.utils;

import com.rudderstack.kafka.connect.config.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public final class ResourceUtil {
    private static final Logger log = LoggerFactory.getLogger(ResourceUtil.class);


    public static String getResourceFileAsString( String fileName)  {
        try (InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream(fileName)) {
            if (is == null) return null;
            try (InputStreamReader isr = new InputStreamReader(is);
                 BufferedReader reader = new BufferedReader(isr)) {
                return reader.lines().collect(Collectors.joining(System.lineSeparator()));
            }
        } catch (Exception e) {
            log.warn("Failed to read resource {}: {}", fileName, e.getMessage());
            return null;
        }
    }
}
