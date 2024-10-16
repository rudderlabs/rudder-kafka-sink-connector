package com.rudderstack.kafka.connect;

import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTaskContext;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class RudderstackSinkTaskTest {

    @Test
    public void shouldStartTask() {
        Map<String, String> props = new HashMap<>();
        props.put("rudder.data.plane.url", "https://example.com");
        props.put("rudder.write.key", "write_key");
        props.put("name", "connector_name");

        RudderstackSinkTask task = new RudderstackSinkTask();
        task.initialize(mock(SinkTaskContext.class));
        assertDoesNotThrow(() -> task.start(props));
        // Start with backoff
        props.put("kafka.retry.backoff.ms", "1000");
        assertDoesNotThrow(() -> task.start(props));
        assertDoesNotThrow(task::stop);
    }

    @Test
    public void shouldPutRecords() throws Exception {
        RudderstackSinkTask task = new RudderstackSinkTask();
        // Use reflection to access private fields
        Field senderField = RudderstackSinkTask.class.getDeclaredField("sender");
        senderField.setAccessible(true);
        RudderstackSender mockSender = mock(RudderstackSender.class);
        senderField.set(task, mockSender);
        var records = List.of(mock(SinkRecord.class));
        task.put(records);
        verify(mockSender, times(1)).send(records);
    }

    @Test
    void shouldReturnVersion() {
        assertNotNull(new RudderstackSinkTask().version());
    }
}
