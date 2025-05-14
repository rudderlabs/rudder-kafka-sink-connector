package com.rudderstack.kafka.connect;

import com.rudderstack.kafka.connect.config.RudderSinkConfig;
import com.rudderstack.kafka.connect.config.Version;
import com.rudderstack.sdk.java.analytics.RudderAnalytics;
import com.rudderstack.sdk.java.analytics.messages.TrackMessage;
import okhttp3.mockwebserver.MockWebServer;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the RudderstackSender class.
 * These tests verify that the sender correctly processes Kafka records
 * and sends them to Rudderstack with the appropriate user agent.
 */
final class RudderstackSenderTest {

    // Test constants
    private static final String TEST_WRITE_KEY = "testKey";
    private static final String TEST_DATA_PLANE_URL = "testUrl";
    private static final String TEST_TOPIC = "test-topic";
    private static final String TEST_TIMESTAMP_STRING = "2024-03-14T12:05:55Z";
    private static final String TEST_TIMESTAMP_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    private RudderstackSender sender;
    private RudderAnalytics mockAnalytics;

    /**
     * Sets up the test environment before each test.
     * Creates a mock RudderSinkConfig and RudderstackSender,
     * then replaces the RudderAnalytics instance with a mock.
     */
    @BeforeEach
    void setUp() throws Exception {
        // Create mock config
        RudderSinkConfig mockConfig = mock(RudderSinkConfig.class);
        when(mockConfig.writeKey()).thenReturn(TEST_WRITE_KEY);
        when(mockConfig.dataPlaneUrl()).thenReturn(TEST_DATA_PLANE_URL);

        // Create sender with mock config
        sender = new RudderstackSender(mockConfig);

        // Replace the real analytics with a mock
        Field analyticsField = RudderstackSender.class.getDeclaredField("analytics");
        analyticsField.setAccessible(true);
        mockAnalytics = mock(RudderAnalytics.class);
        analyticsField.set(sender, mockAnalytics);
    }

    /**
     * Verifies that the RudderstackSender initializes the RudderAnalytics instance.
     */
    @Test
    void shouldInitializeAnalyticsWhenConstructorCalled() {
        assertNotNull(mockAnalytics, "Analytics should be initialized");
    }

    /**
     * Verifies that the RudderstackSender correctly processes a structured record.
     * This test checks that the sender:
     * 1. Extracts the user ID from the record key
     * 2. Converts the struct fields to properties
     * 3. Sets the topic in the context
     */
    @Test
    void shouldEnqueueSinkRecordWithStruct() {
        // Create a test schema and struct
        Schema schema = SchemaBuilder.struct().name("TestSchema")
                .field("id", Schema.INT32_SCHEMA)
                .field("name", Schema.STRING_SCHEMA)
                .field("email", Schema.STRING_SCHEMA)
                .build();

        Struct msg = new Struct(schema)
                .put("id", 1)
                .put("name", "Josh")
                .put("email", "josh@example.com");

        // Create a sink record with the struct
        SinkRecord sinkRecord = new SinkRecord(TEST_TOPIC, 0, null, "key1", schema, msg, 0);

        // Send the record
        sender.send(List.of(sinkRecord));

        // Capture the TrackMessage.Builder passed to enqueue
        ArgumentCaptor<TrackMessage.Builder> argumentCaptor = ArgumentCaptor.forClass(TrackMessage.Builder.class);
        verify(mockAnalytics, times(1)).enqueue(argumentCaptor.capture());

        // Build the track message and verify its properties
        TrackMessage trackMessage = argumentCaptor.getValue().build();
        assertEquals("key1", trackMessage.userId(), "User ID should be set from record key");

        // Verify properties after checking they're not null
        var properties = trackMessage.properties();
        assertNotNull(properties, "Properties should not be null");
        assertEquals("Josh", properties.get("name"), "Name property should match struct field");
        assertEquals("josh@example.com", properties.get("email"), "Email property should match struct field");

        // Verify context after checking it's not null
        var context = trackMessage.context();
        assertNotNull(context, "Context should not be null");
        assertEquals(TEST_TOPIC, context.get("topic"), "Topic should be set in context");
    }

    /**
     * Verifies that the RudderstackSender correctly processes a record with a plain value.
     * This test checks that the sender:
     * 1. Uses the partition as the user ID when key is null
     * 2. Sets the plain value as a property
     * 3. Sets the timestamp from the record
     */
    @Test
    void shouldEnqueueSinkRecordWithPlainValue() throws Exception {
        // Create a simple schema
        Schema schema = SchemaBuilder.struct().name("TestSchema").build();

        // Parse a test timestamp
        Date eventTimestamp = new SimpleDateFormat(TEST_TIMESTAMP_FORMAT).parse(TEST_TIMESTAMP_STRING);

        // Create a sink record with a plain string value and timestamp
        SinkRecord sinkRecord = new SinkRecord(TEST_TOPIC, 0, null, null, schema, "hello world", 0,
                eventTimestamp.getTime(), TimestampType.CREATE_TIME);

        // Send the record
        sender.send(List.of(sinkRecord));

        // Capture the TrackMessage.Builder passed to enqueue
        ArgumentCaptor<TrackMessage.Builder> argumentCaptor = ArgumentCaptor.forClass(TrackMessage.Builder.class);
        verify(mockAnalytics, times(1)).enqueue(argumentCaptor.capture());

        // Build the track message and verify its properties
        TrackMessage trackMessage = argumentCaptor.getValue().build();
        assertEquals("0", trackMessage.userId(), "User ID should be set to partition when key is null");

        // Verify properties after checking they're not null
        var properties = trackMessage.properties();
        assertNotNull(properties, "Properties should not be null");
        assertEquals("hello world", properties.get("value"), "Value property should contain the plain value");

        // Verify context and timestamp
        var context = trackMessage.context();
        assertNotNull(context, "Context should not be null");
        assertEquals(TEST_TOPIC, context.get("topic"), "Topic should be set in context");
        assertEquals(eventTimestamp, trackMessage.timestamp(), "Timestamp should match the record timestamp");
    }

    /**
     * Verifies that the RudderstackSender sets the correct user agent when sending requests.
     * This test uses a MockWebServer to capture the actual HTTP request and verify the User-Agent header.
     */
    @Test
    void shouldSetCorrectUserAgent() throws Exception {
        // Create a simple schema
        Schema schema = SchemaBuilder.struct().name("TestSchema").build();

        // Parse a test timestamp
        Date eventTimestamp = new SimpleDateFormat(TEST_TIMESTAMP_FORMAT).parse(TEST_TIMESTAMP_STRING);

        // Set up a mock web server to capture the HTTP request
        try (MockWebServer mockDataplaneServer = new MockWebServer()) {
            mockDataplaneServer.start();
            String mockDataplaneUrl = mockDataplaneServer.url("/").toString();

            // Create a config that points to our mock server
            RudderSinkConfig mockConfig = mock(RudderSinkConfig.class);
            when(mockConfig.writeKey()).thenReturn(TEST_WRITE_KEY);
            when(mockConfig.dataPlaneUrl()).thenReturn(mockDataplaneUrl);

            // Create a sender with the mock config
            RudderstackSender testSender = new RudderstackSender(mockConfig);

            // Get access to the real analytics instance
            Field analyticsField = RudderstackSender.class.getDeclaredField("analytics");
            analyticsField.setAccessible(true);
            RudderAnalytics testAnalytics = (RudderAnalytics) analyticsField.get(testSender);

            // Create and send a test record
            SinkRecord sinkRecord = new SinkRecord(TEST_TOPIC, 0, null, null, schema, "hello world", 0,
                    eventTimestamp.getTime(), TimestampType.CREATE_TIME);
            testSender.send(List.of(sinkRecord));

            // Flush to ensure the request is sent
            testAnalytics.flush();

            // Get the request and verify the user agent header
            String userAgent = mockDataplaneServer.takeRequest().getHeader("User-Agent");
            String expectedUserAgent = Version.getProjectName() + "/" + Version.getVersion();
            assertEquals(expectedUserAgent, userAgent,
                    "User agent should be set to project name and version");
        }
    }
}
