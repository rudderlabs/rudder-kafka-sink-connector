package com.rudderstack.kafka.connect.config;

import java.util.Objects;
import java.util.List;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigException;

public class RangeValidator<T extends Number> implements ConfigDef.Validator {
    private final T min;
    private final T max;

    public RangeValidator(T min, T max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public void ensureValid(String name, Object value) {
        if (Objects.isNull(value)) {
            return;
        }
        if (!(value instanceof Number)) {
            throw new ConfigException(name, value, "Value must be a number");
        }
        final var longValue = ((Number)value).longValue();
        if (longValue < this.min.longValue()) {
            throw new ConfigException(name, value, "Value must be at least " + this.min);
        } else if (longValue > this.max.longValue()) {
            throw new ConfigException(name, value,
                    "Value must be no more than " + this.max);
        }
    }
}
