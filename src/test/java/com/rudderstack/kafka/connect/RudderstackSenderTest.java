package com.rudderstack.kafka.connect;

import com.rudderstack.kafka.connect.config.RudderSinkConfig;
import com.rudderstack.sdk.java.analytics.RudderAnalytics;

import com.rudderstack.sdk.java.analytics.messages.TrackMessage;
import org.apache.kafka.common.record.TimestampType;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

final class RudderstackSenderTest {
    @Test
    void shouldInitializeAnalyticsWhenConstructorCalled() throws Exception {
        // Create mock RudderSinkConfig
        RudderSinkConfig mockConfig = mock(RudderSinkConfig.class);
        when(mockConfig.writeKey()).thenReturn("testKey");
        when(mockConfig.dataPlaneUrl()).thenReturn("testUrl");

        // Create RudderstackSender with mock config
        RudderstackSender sender = new RudderstackSender(mockConfig);

        // Use reflection to access private fields
        Field analyticsField = RudderstackSender.class.getDeclaredField("analytics");
        analyticsField.setAccessible(true);
        RudderAnalytics analytics = (RudderAnalytics) analyticsField.get(sender);

        verify(mockConfig, times(1)).dataPlaneUrl();
        verify(mockConfig, times(1)).writeKey();
        
        // Verify that the fields are not null (i.e., they have been initialized)
        assertNotNull(analytics);
    }

    @Test
    void shouldEnqueueSinkRecordsWhenSendCalled() throws Exception {
        // Create mock RudderSinkConfig
        RudderSinkConfig mockConfig = mock(RudderSinkConfig.class);
        when(mockConfig.writeKey()).thenReturn("testKey");
        when(mockConfig.dataPlaneUrl()).thenReturn("testUrl");

        // Create RudderstackSender with mock config
        RudderstackSender sender = new RudderstackSender(mockConfig);

        // Use reflection to access private fields
        Field analyticsField = RudderstackSender.class.getDeclaredField("analytics");
        analyticsField.setAccessible(true);
        RudderAnalytics mockAnalytics = mock(RudderAnalytics.class);
        analyticsField.set(sender, mockAnalytics);

        // Define the schema for the SinkRecord
        Schema schema = SchemaBuilder.struct().name("TestSchema")
                .field("id", Schema.INT32_SCHEMA)
                .field("name", Schema.STRING_SCHEMA)
                .field("email", Schema.STRING_SCHEMA)
                .build();

        // Create a new Struct with the schema
        Struct msg1 = new Struct(schema)
                .put("id", 1)
                .put("name", "Josh")
                .put("email", "josh@example.com");

        // Create a new SinkRecord
        SinkRecord record1 = new SinkRecord("test-topic", 0, null, "key1", schema, msg1, 0);
        Date eventTimestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse("2024-03-14T12:05:55Z");
        SinkRecord record2 = new SinkRecord("test-topic", 0, null, null, schema, "hello world", 0, eventTimestamp.getTime(), TimestampType.CREATE_TIME);

        sender.send(List.of(record1, record2));
        ArgumentCaptor<TrackMessage.Builder> argumentCaptor = ArgumentCaptor.forClass(TrackMessage.Builder.class);
        verify(mockAnalytics, times(2)).enqueue(argumentCaptor.capture());
        List<TrackMessage.Builder> trackMessageBuilders = argumentCaptor.getAllValues();
        TrackMessage trackMessage1 = trackMessageBuilders.get(0).build();
        // Message Key will be used as userId
        assertEquals("key1", trackMessage1.userId());
        assertNotNull(trackMessage1.properties());
        assertEquals("Josh", trackMessage1.properties().get("name"));
        assertEquals("josh@example.com", trackMessage1.properties().get("email"));
        assertTrue(trackMessage1.properties().containsKey("email"));
        assertNotNull(trackMessage1.context());
        assertEquals("test-topic", trackMessage1.context().get("topic"));

        TrackMessage trackMessage2 = trackMessageBuilders.get(1).build();
        // Partition will be used as userId
        assertEquals("0", trackMessage2.userId());
        assertNotNull(trackMessage2.properties());
        assertEquals("hello world", trackMessage2.properties().get("message"));
        assertNotNull(trackMessage2.context());
        assertEquals("test-topic", trackMessage2.context().get("topic"));
        assertEquals(eventTimestamp, trackMessage2.timestamp());
    }
}