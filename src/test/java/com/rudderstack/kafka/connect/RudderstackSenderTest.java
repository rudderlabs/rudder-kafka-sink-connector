package com.rudderstack.kafka.connect;

import com.rudderstack.kafka.connect.config.RudderSinkConfig;
import com.rudderstack.sdk.java.analytics.RudderAnalytics;
import com.rudderstack.sdk.java.analytics.messages.TrackMessage;
import org.apache.kafka.common.record.TimestampType;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.sink.SinkRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

final class RudderstackSenderTest {

    private RudderstackSender sender;
    private RudderAnalytics mockAnalytics;

    @BeforeEach
    void setUp() throws Exception {
        RudderSinkConfig mockConfig = mock(RudderSinkConfig.class);
        when(mockConfig.writeKey()).thenReturn("testKey");
        when(mockConfig.dataPlaneUrl()).thenReturn("testUrl");

        sender = new RudderstackSender(mockConfig);

        Field analyticsField = RudderstackSender.class.getDeclaredField("analytics");
        analyticsField.setAccessible(true);
        mockAnalytics = mock(RudderAnalytics.class);
        analyticsField.set(sender, mockAnalytics);
    }

    @Test
    void shouldInitializeAnalyticsWhenConstructorCalled() {
        assertNotNull(mockAnalytics, "Analytics should be initialized");
    }

    @Test
    void shouldEnqueueSinkRecordWithStruct() {
        Schema schema = SchemaBuilder.struct().name("TestSchema")
                .field("id", Schema.INT32_SCHEMA)
                .field("name", Schema.STRING_SCHEMA)
                .field("email", Schema.STRING_SCHEMA)
                .build();

        Struct msg = new Struct(schema)
                .put("id", 1)
                .put("name", "Josh")
                .put("email", "josh@example.com");

        SinkRecord sinkRecord = new SinkRecord("test-topic", 0, null, "key1", schema, msg, 0);

        sender.send(List.of(sinkRecord));

        ArgumentCaptor<TrackMessage.Builder> argumentCaptor = ArgumentCaptor.forClass(TrackMessage.Builder.class);
        verify(mockAnalytics, times(1)).enqueue(argumentCaptor.capture());

        TrackMessage trackMessage = argumentCaptor.getValue().build();
        assertEquals("key1", trackMessage.userId());
        assertNotNull(trackMessage.properties());
        assertEquals("Josh", trackMessage.properties().get("name"));
        assertEquals("josh@example.com", trackMessage.properties().get("email"));
        assertEquals("test-topic", Objects.requireNonNull(trackMessage.context()).get("topic"));
    }

    @Test
    void shouldEnqueueSinkRecordWithPlainValue() throws Exception {
        Schema schema = SchemaBuilder.struct().name("TestSchema").build();

        Date eventTimestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse("2024-03-14T12:05:55Z");
        SinkRecord sinkRecord = new SinkRecord("test-topic", 0, null, null, schema, "hello world", 0, eventTimestamp.getTime(), TimestampType.CREATE_TIME);

        sender.send(List.of(sinkRecord));

        ArgumentCaptor<TrackMessage.Builder> argumentCaptor = ArgumentCaptor.forClass(TrackMessage.Builder.class);
        verify(mockAnalytics, times(1)).enqueue(argumentCaptor.capture());

        TrackMessage trackMessage = argumentCaptor.getValue().build();
        assertEquals("0", trackMessage.userId());
        assertNotNull(trackMessage.properties());
        assertEquals("hello world", trackMessage.properties().get("value"));
        assertEquals("test-topic", Objects.requireNonNull(trackMessage.context()).get("topic"));
        assertEquals(eventTimestamp, trackMessage.timestamp());
    }
}
