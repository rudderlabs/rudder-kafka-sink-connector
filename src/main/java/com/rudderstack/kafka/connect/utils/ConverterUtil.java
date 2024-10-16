package com.rudderstack.kafka.connect.utils;

import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.sink.SinkRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class ConverterUtil {
    private static final Logger log = LoggerFactory.getLogger(ConverterUtil.class);

    private ConverterUtil() {
    }

    /**
     * Converts a Struct to a Map<String, Object>
     *
     * @param struct the Struct object to be converted
     * @return a Map with field names as keys and field values as values
     */
    public static Map<String, Object> convertStructToMap(Struct struct) {
        Map<String, Object> resultMap = new HashMap<>();

        if (struct == null) {
            return resultMap; // Return an empty map if struct is null
        }

        Schema schema = struct.schema(); // Get the schema for the Struct

        // Iterate over all the fields in the Struct
        for (Field field : schema.fields()) {
            String fieldName = field.name(); // Get field name
            Object fieldValue = struct.get(field); // Get field value
            resultMap.put(fieldName, fieldValue); // Add to the result map
        }

        return resultMap;
    }

    /**
     * Converts a Struct to a Map<String, Object>
     *
     * @param record the GenericRecord to be converted
     * @return a Map with field names as keys and field values as values
     */
    public static Map<String, Object> convertGenericRecordToMap(GenericRecord record) {
        Map<String, Object> resultMap = new HashMap<>();

        if (record == null) {
            return resultMap; // Return an empty map if struct is null
        }

        org.apache.avro.Schema schema = record.getSchema(); // Get the schema for the Struct

        // Iterate over all the fields in the Struct
        for (org.apache.avro.Schema.Field field : schema.getFields()) {
            String fieldName = field.name(); // Get field name
            Object fieldValue = record.get(fieldName); // Get field value
            resultMap.put(fieldName, fieldValue); // Add to the result map
        }

        return resultMap;
    }

    public static Map<String, Object> convertMap(Map<?, ?> originalMap) {
        Map<String, Object> resultMap = new HashMap<>();

        for (Map.Entry<?, ?> entry : originalMap.entrySet()) {
            Object key = entry.getKey();
            Object value = entry.getValue();

            // Convert the key to String, keep the value as it is
            String stringKey = String.valueOf(key);  // Converts key to String (null-safe)
            resultMap.put(stringKey, value);         // Add to result map
        }

        return resultMap;
    }

    /**
     * Converts any sink record to a map
     *
     * @param record SinkRecord
     * @return Map
     */
    public static Map<String, Object> convert(SinkRecord record) {
        Object value = record.value();
        Object key = record.key();

        if (value instanceof Struct struct) {
            return convertStructToMap(struct);
        } else if (value instanceof GenericRecord genericRecord) {
            return convertGenericRecordToMap(genericRecord);
        } else if (value instanceof Map<?, ?> map) {
            // Handle JSON type which will likely be a Map
            return convertMap(map);
        } else {
            log.warn("The type of the record value: {} is not supported, so it will be retained in its original form.", value.getClass().getName());
            // Handle primitive types (e.g., String, Integer)
            if (key != null) {
                return Collections.singletonMap(key.toString(), value);
            } else {
                return Collections.singletonMap("value", value);
            }
        }
    }
}
