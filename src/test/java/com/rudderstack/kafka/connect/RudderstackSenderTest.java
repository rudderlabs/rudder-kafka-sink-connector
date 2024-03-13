package com.rudderstack.kafka.connect;

import com.rudderstack.kafka.connect.config.RudderSinkConfig;
import com.rudderstack.sdk.java.analytics.RudderAnalytics;

import com.rudderstack.sdk.java.analytics.messages.MessageBuilder;
import com.rudderstack.sdk.java.analytics.messages.TrackMessage;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Field;
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

        Struct msg2 = new Struct(schema)
                .put("id", 2)
                .put("name", "john")
                .put("email", "john@example.com");

        // Create a new SinkRecord
        SinkRecord record1 = new SinkRecord("test-topic", 0, null, "key1", schema, msg1, 0);
        SinkRecord record2 = new SinkRecord("test-topic", 0, null, "key2", schema, msg2, 0);

        sender.send(List.of(record1, record2));
        ArgumentCaptor<TrackMessage.Builder> argumentCaptor = ArgumentCaptor.forClass(TrackMessage.Builder.class);
        verify(mockAnalytics, times(2)).enqueue(argumentCaptor.capture());
        List<TrackMessage.Builder> trackMessageBuilders = argumentCaptor.getAllValues();
        TrackMessage trackMessage1 = trackMessageBuilders.get(0).build();
        assertEquals("key1", trackMessage1.userId());
        assertNotNull(trackMessage1.properties());
        assertTrue(trackMessage1.properties().containsKey("email"));
        assertNotNull(trackMessage1.context());
        assertTrue(trackMessage1.context().containsKey("topic"));
        TrackMessage trackMessage2 = trackMessageBuilders.get(1).build();
        assertEquals("key2", trackMessage2.userId());
        assertNotNull(trackMessage2.properties());
        assertTrue(trackMessage2.properties().containsKey("name"));
    }
}