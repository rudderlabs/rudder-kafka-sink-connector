package com.rudderstack.kafka.connect;

import java.util.Collection;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Date;

import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.json.JsonConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rudderstack.sdk.java.analytics.RudderAnalytics;
import com.rudderstack.sdk.java.analytics.messages.TrackMessage;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rudderstack.kafka.connect.config.RudderSinkConfig;

/**
 * This class is responsible for sending records to Rudderstack.
 */
public class RudderstackSender {

    private static final Logger log = LoggerFactory.getLogger(RudderstackSender.class);

    private final RudderAnalytics analytics;
    private final JsonConverter jsonConverter;
    private final ObjectMapper mapper = new ObjectMapper();

    public RudderstackSender(final RudderSinkConfig config) {
        this.analytics = RudderAnalytics
                .builder(config.writeKey())
                .setDataPlaneUrl(config.dataPlaneUrl())
                .setGZIP(true)
                .build();
        this.jsonConverter = new JsonConverter();
        jsonConverter.configure(Map.of("schemas.enable", false, "converter.type", "value"));
    }

    /**
     * Converts a SinkRecord to a Map representation.
     *
     * @param sinkRecord the SinkRecord to convert
     * @return a Map representation of the SinkRecord
     * @throws Exception if there is an error converting the record to a map
     */
    private Map<String, Object> convertRecordToMap(SinkRecord sinkRecord) {
        try {
            byte[] jsonBytes = jsonConverter.fromConnectData(sinkRecord.topic(),
                    sinkRecord.valueSchema(),
                    sinkRecord.value());
            return mapper.readValue(jsonBytes, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception e) {
            log.error("Error converting record to map", e);
            return new LinkedHashMap<>();
        }
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
            messageBuilder.properties(convertRecordToMap(sinkRecord));
            this.analytics.enqueue(messageBuilder);
        }
    }
}
