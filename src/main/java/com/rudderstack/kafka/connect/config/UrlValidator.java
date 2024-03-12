package com.rudderstack.kafka.connect.config;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigException;

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
            new URL((String) value);
        } catch (final MalformedURLException e) {
            throw new ConfigException(name, value, "malformed URL");
        }
    }

    @Override
    public String toString() {
        return "HTTPS URL";
    }

}