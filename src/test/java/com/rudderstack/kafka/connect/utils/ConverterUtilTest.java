package com.rudderstack.kafka.connect.utils;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.sink.SinkRecord;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class ConverterUtilTest {

    @Test
    void testConvertStructToMapWithValidStruct() {
        org.apache.kafka.connect.data.Schema schema = SchemaBuilder.struct()
                .field("field1", org.apache.kafka.connect.data.Schema.STRING_SCHEMA)
                .field("field2", org.apache.kafka.connect.data.Schema.INT32_SCHEMA)
                .build();

        Struct struct = new Struct(schema)
                .put("field1", "value1")
                .put("field2", 123);

        Map<String, Object> result = ConverterUtil.convertStructToMap(struct);

        assertEquals(2, result.size());
        assertEquals("value1", result.get("field1"));
        assertEquals(123, result.get("field2"));
    }

    @Test
    void testConvertStructToMapWithNullStruct() {
        Map<String, Object> result = ConverterUtil.convertStructToMap(null);
        assertTrue(result.isEmpty());
    }

    @Test
    void testConvertGenericRecordToMapWithValidRecord() {
        Schema schema = Schema.createRecord("TestRecord", null, null, false);
        Schema.Field field1 = new Schema.Field("field1", Schema.create(Schema.Type.STRING), null, null);
        Schema.Field field2 = new Schema.Field("field2", Schema.create(Schema.Type.INT), null, null);
        schema.setFields(java.util.Arrays.asList(field1, field2));

        GenericRecord genericRecord = new GenericData.Record(schema);
        genericRecord.put("field1", "value1");
        genericRecord.put("field2", 123);

        Map<String, Object> result = ConverterUtil.convertGenericRecordToMap(genericRecord);

        assertEquals(2, result.size());
        assertEquals("value1", result.get("field1"));
        assertEquals(123, result.get("field2"));
    }

    @Test
    void testConvertGenericRecordToMapWithNullRecord() {
        Map<String, Object> result = ConverterUtil.convertGenericRecordToMap(null);
        assertTrue(result.isEmpty());
    }

    @Test
    void testConvertMapWithValidMap() {
        Map<Integer, String> originalMap = Map.of(1, "one", 2, "two");
        Map<String, Object> result = ConverterUtil.convertMap(originalMap);

        assertEquals(2, result.size());
        assertEquals("one", result.get("1"));
        assertEquals("two", result.get("2"));
    }

    @Test
    void testConvertWithStruct() {
        org.apache.kafka.connect.data.Schema schema = SchemaBuilder.struct()
                .field("field1", org.apache.kafka.connect.data.Schema.STRING_SCHEMA)
                .build();

        Struct struct = new Struct(schema)
                .put("field1", "value1");

        SinkRecord sinkRecord = new SinkRecord("topic", 0, null, null, schema, struct, 0);
        Map<String, Object> result = ConverterUtil.convert(sinkRecord);

        assertEquals(1, result.size());
        assertEquals("value1", result.get("field1"));
    }

    @Test
    void testConvertWithGenericRecord() {
        Schema schema = Schema.createRecord("TestRecord", null, null, false);
        Schema.Field field1 = new Schema.Field("field1", Schema.create(Schema.Type.STRING), null, null);
        schema.setFields(java.util.Collections.singletonList(field1));

        GenericRecord genericRecord = new GenericData.Record(schema);
        genericRecord.put("field1", "value1");

        SinkRecord sinkRecord = new SinkRecord("topic", 0, null, null, null, genericRecord, 0);
        Map<String, Object> result = ConverterUtil.convert(sinkRecord);

        assertEquals(1, result.size());
        assertEquals("value1", result.get("field1"));
    }

    @Test
    void testConvertWithMap() {
        Map<String, String> originalMap = Map.of("key", "value");
        SinkRecord sinkRecord = new SinkRecord("topic", 0, null, null, null, originalMap, 0);
        Map<String, Object> result = ConverterUtil.convert(sinkRecord);

        assertEquals(1, result.size());
        assertEquals("value", result.get("key"));
    }

    @Test
    void testConvertWithPrimitiveValue() {
        SinkRecord sinkRecord = new SinkRecord("topic", 0, null, "key", null, "value", 0);
        Map<String, Object> result = ConverterUtil.convert(sinkRecord);

        assertEquals(1, result.size());
        assertEquals("value", result.get("key"));
    }
}
