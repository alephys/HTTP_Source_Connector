package io.confluent.connect.http.connector;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.AbstractConfig;

import java.util.Map;

public class HttpSourceConfig extends AbstractConfig {
    public static final String TOPIC_CONFIG = "topic";
    public static final String HTTP_ENDPOINT_CONFIG = "http.endpoint";
    public static final String TENANT_ID = "tenant.id";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String POLL_INTERVAL_MS = "poll.interval.ms";
    public static final String HEARTBEAT_INTERVAL_MS = "heartbeat.interval.ms";
    public static final String OFFSET_TOPIC = "offset.topic";
    public static final String MODEL_TOPIC = "model.topic";
    public static final String PUBLISHED_ROOT_ID = "published.root.id";
    public static final String RETIRED_ROOT_ID = "retired.root.id";

    public HttpSourceConfig(Map<?, ?> originals) {
        super(config(), originals);
    }

    public static ConfigDef config() {
        return new ConfigDef()
                .define(TOPIC_CONFIG, ConfigDef.Type.STRING, ConfigDef.Importance.HIGH, "Kafka directory topic to send data")
                .define(HTTP_ENDPOINT_CONFIG, ConfigDef.Type.STRING, ConfigDef.Importance.HIGH, "Signavio API to authenticate and JWT Token")
                .define(TENANT_ID, ConfigDef.Type.STRING, ConfigDef.Importance.HIGH, "Signavio Tenant ID")
                .define(USERNAME, ConfigDef.Type.STRING, ConfigDef.Importance.HIGH, "Signavio Username")
                .define(PASSWORD, ConfigDef.Type.PASSWORD, ConfigDef.Importance.HIGH, "Signavio Password")
                .define(POLL_INTERVAL_MS, ConfigDef.Type.INT, 5, ConfigDef.Importance.MEDIUM, "Poll interval in minutes")
                .define(HEARTBEAT_INTERVAL_MS, ConfigDef.Type.INT, 60000, ConfigDef.Importance.MEDIUM, "Heartbeat interval in milliseconds")
                .define(OFFSET_TOPIC, ConfigDef.Type.STRING, ConfigDef.Importance.HIGH, "Kafka topic for storing offsets")
                .define(MODEL_TOPIC, ConfigDef.Type.STRING, ConfigDef.Importance.HIGH, "Kafka topic for model data")
                .define(PUBLISHED_ROOT_ID,ConfigDef.Type.STRING,ConfigDef.Importance.HIGH,"Root DirectoryId for published model")
                .define(RETIRED_ROOT_ID,ConfigDef.Type.STRING,ConfigDef.Importance.HIGH,"Root DirectoryId for retired model");
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
}