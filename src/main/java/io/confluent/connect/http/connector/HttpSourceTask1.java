package io.confluent.connect.http.connector;

import io.confluent.connect.http.Schemas.Schemas;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import io.confluent.connect.http.OffsetManager.OffsetManager;
import io.confluent.connect.http.resources.TimeCheck;
import io.confluent.connect.http.Logger.ConnectorLogger;
import io.confluent.connect.http.Signavio.SignavioAPI;

import org.json.JSONObject;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpSourceTask1 extends SourceTask {
    private static final ConnectorLogger log = ConnectorLogger.getLogger(HttpSourceTask1.class);
    private String topic;
    private String httpEndpoint;
    private int POLL_INTERVAL_MS;
    private int HEARTBEAT_INTERVAL_MS;
    private String USERNAME;
    private String PASSWORD;
    private String nextUri;
    private String TENANT_ID;


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
        this.USERNAME = config.getUserName();
        this.PASSWORD = config.getPassword();
        this.TENANT_ID = config.getTenantId();
        SignavioAPI signavioApi = new SignavioAPI(httpEndpoint, TENANT_ID);
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

            SignavioAPI signavioApi = new SignavioAPI(httpEndpoint, TENANT_ID);
            signavioApi.authenticate(USERNAME, PASSWORD);

            String topLevelDirectoryId = "someTopLevelDirectoryId";
            String directoryResponse = signavioApi.retrieveDiagramsInFolder(topLevelDirectoryId);

            JSONObject responseJson = new JSONObject(directoryResponse);
            if (responseJson.has("error")) {
                log.error("Error while fetching directory: " + responseJson.getString("error"));
                // DLQ to configure
                return records;
            }

            for (Object item : responseJson.getJSONArray("items")) {
                JSONObject itemJson = (JSONObject) item;
                String rel = itemJson.getString("rel");

                if ("info".equals(rel)) {
                    String directoryId = itemJson.getString("id");
                    records.add(createSourceRecordForDirectory(directoryId));

                } else if ("mod".equals(rel)) {
                    String modelId = itemJson.getString("id");
                    String modelContentResponse = signavioApi.retrieveModelRevision(modelId);

                    JSONObject modelResponseJson = new JSONObject(modelContentResponse);
                    if (modelResponseJson.has("error")) {
                        log.error("Error while fetching model content: " + modelResponseJson.getString("error"));
                        // DLQ to configure
                    } else {
                        records.add(createSourceRecordForModel(modelResponseJson));
                    }
                }
            }

            String nextUri = responseJson.optString("nextUri", null);
            if (nextUri != null) {
                log.info("Next page available, URI: " + nextUri);
                records.add(createSourceRecordForWatermark(nextUri));
            } else {
                Instant currentTimestamp = Instant.now();
                records.add(createSourceRecordForHighWatermark(currentTimestamp));
            }
        } catch (Exception e) {
            return new ArrayList<>();
        }
        return records;
    }

    private SourceRecord createSourceRecordForDirectory(String directoryId) {
        return new SourceRecord(sourcePartition(), createOffsetMap(directoryId), topic, Schemas.DIRECTORY_DATA_SCHEMA, directoryId);
    }

    private SourceRecord createSourceRecordForModel(JSONObject modelContentJson) {
        String modelId = modelContentJson.getString("id");
        return new SourceRecord(sourcePartition(), createOffsetMap(modelId), topic, Schemas.MODEL_DATA_SCHEMA, modelContentJson.toString());
    }

    private Map<String, Object> createOffsetMap(String id) {
        Map<String, Object> offsetMap = new HashMap<>();
        offsetMap.put("id", id);
        return offsetMap;
    }

    private SourceRecord createSourceRecordForWatermark(String nextUri) {
        return new SourceRecord(sourcePartition(), createOffsetMap(nextUri), topic, Schema.STRING_SCHEMA, nextUri);
    }

    private SourceRecord createSourceRecordForHighWatermark(Instant timestamp) {
        return new SourceRecord(sourcePartition(), createOffsetMap(timestamp.toString()), topic, Schema.STRING_SCHEMA, timestamp.toString());
    }

    @Override
    public void stop() {
    }
}