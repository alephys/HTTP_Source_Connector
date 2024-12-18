package io.confluent.ps.connect.signavio.connector;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.AbstractConfig;

import java.util.Map;

public class ProcessdataConfig extends AbstractConfig {
    public static final String TOPIC_CONFIG = "kafka.directory.topic";
    public static final String HTTP_ENDPOINT_CONFIG = "signavio.authenticate.api.url";
    public static final String TENANT_ID = "signavio.tenant.id";
    public static final String USERNAME = "signavio.username";
    public static final String PASSWORD = "signavio.password";
    public static final String POLL_INTERVAL_MS = "signavio.poll.interval.ms";
    public static final String HEARTBEAT_INTERVAL_MS = "signavio.heartbeat.interval.ms";
    public static final String OFFSET_TOPIC = "offset.storage.topic";
    public static final String MODEL_TOPIC = "kafka.model.topic";
    public static final String PUBLISHED_ROOT_ID = "signavio.published.root.id";
    public static final String RETIRED_ROOT_ID = "siganvio.retired.root.id";
    public static final String DLQ_TOPIC_CONFIG = "dead.letter.queue.topic";
    public static final String DLQ_TOPIC_DEFAULT = "dlq";
    public static final String DLQ_REPORT_ERRORS_CONFIG = "dead.letter.queue.report.errors";
    public static final boolean DLQ_REPORT_ERRORS_DEFAULT = true;


    public ProcessdataConfig(Map<?, ?> originals) {
        super(config(), originals);
    }

    public static ConfigDef config() {
        return new ConfigDef()
                .define(TOPIC_CONFIG, ConfigDef.Type.STRING, ConfigDef.Importance.HIGH, "Kafka directory topic to send data")
                .define(HTTP_ENDPOINT_CONFIG, ConfigDef.Type.STRING, ConfigDef.Importance.HIGH, "Signavio API to authenticate and JWT Token")
                .define(TENANT_ID, ConfigDef.Type.STRING, ConfigDef.Importance.HIGH, "Signavio Tenant ID")
                .define(USERNAME, ConfigDef.Type.STRING, ConfigDef.Importance.HIGH, "Signavio Username")
                .define(PASSWORD, ConfigDef.Type.PASSWORD, ConfigDef.Importance.HIGH, "Signavio Password")
                .define(POLL_INTERVAL_MS, ConfigDef.Type.INT, 1440, ConfigDef.Importance.MEDIUM, "Poll interval in minutes")
                .define(HEARTBEAT_INTERVAL_MS, ConfigDef.Type.INT, 60000, ConfigDef.Importance.MEDIUM, "Heartbeat interval in milliseconds")
                .define(OFFSET_TOPIC, ConfigDef.Type.STRING, ConfigDef.Importance.HIGH, "Kafka topic for storing offsets")
                .define(MODEL_TOPIC, ConfigDef.Type.STRING, ConfigDef.Importance.HIGH, "Kafka topic for model data")
                .define(PUBLISHED_ROOT_ID,ConfigDef.Type.STRING,ConfigDef.Importance.HIGH,"Root DirectoryId for published model")
                .define(RETIRED_ROOT_ID,ConfigDef.Type.STRING,ConfigDef.Importance.HIGH,"Root DirectoryId for retired model")
                .define(DLQ_TOPIC_CONFIG, ConfigDef.Type.STRING, DLQ_TOPIC_DEFAULT, ConfigDef.Importance.HIGH, "Topic to write failed records.")
                .define(DLQ_REPORT_ERRORS_CONFIG, ConfigDef.Type.BOOLEAN, DLQ_REPORT_ERRORS_DEFAULT, ConfigDef.Importance.MEDIUM, "Enable reporting of errors to the DLQ.");
    }

    public String getTopic() {
        return this.getString(TOPIC_CONFIG);
    }

    public String getHttpEndpoint() {
        return this.getString(HTTP_ENDPOINT_CONFIG);
    }

    public int getPollIntervalMs() {
        return this.getInt(POLL_INTERVAL_MS);
    }

    public int getHeartBeatIntervalMs() {
        return this.getInt(HEARTBEAT_INTERVAL_MS);
    }

    public String getUserName() {
        return this.getString(USERNAME);
    }

    public String getPassword() {
        return this.getString(PASSWORD);
    }

    public String getTenantId() {
        return this.getString(TENANT_ID);
    }

    public String getModelTopic() {return this.getString(MODEL_TOPIC);}

    public String getPublishedRootId() {return this.getString(PUBLISHED_ROOT_ID);}

    public String getRetiredRootId() {return this.getString(RETIRED_ROOT_ID);}

    public String getDlqTopic() {return this.getString(DLQ_TOPIC_CONFIG);}

    public String getDlqReportErrors() {return this.getString(DLQ_REPORT_ERRORS_CONFIG);}

}