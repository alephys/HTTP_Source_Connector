package io.confluent.connect.http.connector;

import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import io.confluent.connect.http.OffsetManager.OffsetManager;
import io.confluent.connect.http.resources.TimeCheck;
import io.confluent.connect.http.Logger.ConnectorLogger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpSourceTask extends SourceTask {
    private String topic;
    private String httpEndpoint;
    private int POLL_INTERVAL_MS;
    private int HEARTBEAT_INTERVAL_MS;
    private String nextUri;
    private static final ConnectorLogger log = ConnectorLogger.getLogger(HttpSourceTask.class);

    @Override
    public String version() {
        return "1.0.0";
    }

    @Override
    public void start(Map<String, String> props) {
        HttpSourceConfig config = new HttpSourceConfig(props);
        this.topic = config.getTopic();
        this.httpEndpoint = config.getHttpEndpoint();
        this.POLL_INTERVAL_MS = config.getPollIntervalMs();
        this.HEARTBEAT_INTERVAL_MS = config.getHeartBeatIntervalMs();

        Map<String, Object> offsetMap = context.offsetStorageReader().offset(sourcePartition());
        OffsetManager offsetManager = OffsetManager.fromMap(offsetMap);
        if (offsetManager != null) {
            nextUri = offsetManager.getUri();
        } else {
        }
    }

    private Map<String, String> sourcePartition() {
        Map<String, String> partition = new HashMap<>();
        partition.put("http.endpoint", httpEndpoint);
        return partition;
    }

    @Override
    public List<SourceRecord> poll() throws InterruptedException {
        List<SourceRecord> records = new ArrayList<>();
        try {

            Map<String, Object> lastOffsetMap = context.offsetStorageReader().offset(sourcePartition());
            if (lastOffsetMap == null) {
                log.info("No previous offset found. Starting from the beginning.");
            }

            OffsetManager lastOffset = OffsetManager.fromMap(lastOffsetMap);
            if (lastOffset == null) {
                log.warn("Failed to parse offset. Starting from the beginning.");
            }

            Instant lastTimestamp = lastOffset.getTimestamp();
            if (lastTimestamp != null) {
                log.info("Last offset timestamp: " + lastTimestamp);
                TimeCheck.checkOffsetWithPollInterval(lastTimestamp, POLL_INTERVAL_MS * 60 * 1000, HEARTBEAT_INTERVAL_MS);
            } else {
                log.warn("No timestamp found in the last offset. Unable to proceed.");
            }
            HttpURLConnection connection = (HttpURLConnection) new URL(httpEndpoint).openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            if (connection.getResponseCode() == 200) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String messageKey = "message-" + line.hashCode();
                        Instant timestamp = Instant.now();
                        OffsetManager offsetManager = new OffsetManager(timestamp, httpEndpoint);

                        SourceRecord record = new SourceRecord(
                                sourcePartition(),
                                offsetManager.toMap(),
                                topic,
                                Schema.STRING_SCHEMA,
                                messageKey,
                                Schema.STRING_SCHEMA,
                                line
                        );
                        records.add(record);
                    }
                }
            }
        } catch (Exception e) {
            return new ArrayList<>();
        }
        return records;
    }

    @Override
    public void stop() {
    }
}