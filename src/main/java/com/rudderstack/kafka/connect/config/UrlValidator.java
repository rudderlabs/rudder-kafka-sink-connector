package com.rudderstack.kafka.connect.config;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigException;

import java.net.URI;
import java.net.URISyntaxException;

class UrlValidator implements ConfigDef.Validator {

    @Override
    public void ensureValid(final String name, final Object value) {
        if (value == null) {
            throw new ConfigException(name, null, "can't be null");
        }
        if (!(value instanceof String)) {
            throw new ConfigException(name, value, "must be string");
        }
        try {
            URI uri = new URI((String) value);
            if (uri.getScheme() == null || uri.getHost() == null) {
                throw new ConfigException(name, value, "malformed URL");
            }
        } catch (final URISyntaxException e) {
            throw new ConfigException(name, value, "malformed URL");
        }
    }
}