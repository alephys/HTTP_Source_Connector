package io.confluent.connect.http.connector;

import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class HttpSourceTask extends SourceTask {

    private static final Logger log = LoggerFactory.getLogger(HttpSourceTask.class);
    private String folderTopic;
    private String modelTopic;
    private String heartBeatTopic;
    private String signavioLoginAPI;
    private String signavioTenandId;
    private String signavioLoginUsername;
    private String signavioLoginPassword;
    private String signavioFolderAPI;
    private String signavioModelContentAPI;
    private String pollIntervalMs;
    private String heartBeatIntervalMs;



    @Override
    public String version() {
        return "1.0.0";
    }

    @Override
    public void start(Map<String, String> props) {
        HttpSourceConfig config = new HttpSourceConfig(props);
        this.folderTopic = config.getKafkaStatusFolderTopic();
        this.modelTopic = config.getKafkaStatusModelTopic();
        this.heartBeatTopic = config.getKafkaStatusHeartbeatTopic();
        this.signavioLoginAPI = config.getSapSignavioLoginApi();
        this.signavioTenandId = config.getSapSignavioLoginTenantId();
        this.signavioLoginUsername = config.getSapSignavioLoginUsername();
        this.signavioLoginPassword = config.getSapSignavioLoginPassword();
        this.signavioFolderAPI = config.getSapSignavioFolderApi();
        this.signavioModelContentAPI = config.getSapSignavioModelContentApi();
        this.pollIntervalMs = config.getPollIntervalMs();
        this.heartBeatIntervalMs = config.getHeartbeatIntervalMs();

        log.info("Starting the HTTP Source Connector");
    }

    @Override
    public List<SourceRecord> poll() throws InterruptedException {
        List<SourceRecord> records = new ArrayList<>();
        return records;
    }

    @Override
    public void stop() {
        log.info("Shutting down the HTTP Source Connector");
    }
}
