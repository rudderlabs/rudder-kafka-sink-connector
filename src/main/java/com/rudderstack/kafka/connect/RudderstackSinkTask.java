package com.rudderstack.kafka.connect;

import com.rudderstack.kafka.connect.config.RudderSinkConfig;
import com.rudderstack.kafka.connect.config.Version;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * The RudderSinkTask class is responsible for handling the sink task for the
 * RudderStack Kafka Connect Sink connector.
 * It receives records from Kafka and sends them to RudderStack.
 */
public class RudderstackSinkTask extends SinkTask {
    private static final Logger log = LoggerFactory.getLogger(RudderstackSinkTask.class);

    private RudderstackSender sender;

    @Override
    public String version() {
        return Version.getVersion();
    }

    @Override
    public void start(Map<String, String> props) {
        Objects.requireNonNull(props);
        final var config = new RudderSinkConfig(props);
        sender = new RudderstackSender(config);
        if (Objects.nonNull(config.kafkaRetryBackoffMs())) {
            context.timeout(config.kafkaRetryBackoffMs());
        }
    }

    @Override
    public void put(Collection<SinkRecord> records) {
        log.debug("Received {} records", records.size());
        sender.send(records);
    }

    @Override
    public void stop() {
        // Nothing to do.
    }
}
