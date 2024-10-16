package com.rudderstack.kafka.connect;

import com.rudderstack.kafka.connect.config.RudderSinkConfig;
import com.rudderstack.kafka.connect.utils.ConverterUtil;
import com.rudderstack.sdk.java.analytics.RudderAnalytics;
import com.rudderstack.sdk.java.analytics.messages.TrackMessage;
import org.apache.kafka.connect.sink.SinkRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This class is responsible for sending records to Rudderstack.
 */
public class RudderstackSender {

    private static final Logger log = LoggerFactory.getLogger(RudderstackSender.class);

    private final RudderAnalytics analytics;

    public RudderstackSender(final RudderSinkConfig config) {
        this.analytics = RudderAnalytics
                .builder(config.writeKey())
                .setDataPlaneUrl(config.dataPlaneUrl())
                .setGZIP(true)
                .build();
    }

    /**
     * Computes the user ID for a given SinkRecord.
     *
     * @param sinkRecord the SinkRecord for which to compute the user ID
     * @return the computed user ID as a String
     */
    private String computeUserID(SinkRecord sinkRecord) {
        Object key = sinkRecord.key();
        if (key != null) {
            return key.toString();
        }
        return String.valueOf(sinkRecord.kafkaPartition());
    }

    private Date computeTimestamp(SinkRecord sinkRecord) {
        Long timestamp = sinkRecord.timestamp();
        if (timestamp == null) {
            return new Date();
        }
        return new Date(timestamp);
    }

    /**
     * Sends a collection of SinkRecords to Rudderstack.
     * This method sends the provided collection of SinkRecords to Rudderstack for
     * further processing.
     * Each SinkRecord is converted to a TrackMessage and enqueued in the
     * RudderstackAnalytics instance.
     *
     * @param records the collection of SinkRecords to send
     */
    public void send(final Collection<SinkRecord> records) {
        log.info("Sending {} records", records.size());
        // Send the records to Rudderstack
        for (final SinkRecord sinkRecord : records) {
            var messageBuilder = TrackMessage.builder("Kafka Record");
            Map<String, Object> context = new LinkedHashMap<>();
            context.put("topic", sinkRecord.topic());
            messageBuilder.timestamp(computeTimestamp(sinkRecord));
            messageBuilder.userId(computeUserID(sinkRecord));
            messageBuilder.context(context);
            messageBuilder.properties(ConverterUtil.convert(sinkRecord));
            this.analytics.enqueue(messageBuilder);
        }
    }
}
