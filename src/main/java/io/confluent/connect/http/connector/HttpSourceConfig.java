package io.confluent.connect.http.connector;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigDef.Type;
import org.apache.kafka.common.config.ConfigDef.Importance;

import java.util.Map;

public class HttpSourceConfig extends AbstractConfig {

    public static final String KAFKA_STATUS_FOLDER_TOPIC = "kafka.status.folder.topic";
    public static final String KAFKA_STATUS_MODEL_TOPIC = "kafka.status.model.topic";
    public static final String KAFKA_STATUS_HEARBEAT_TOPIC = "kafka.status.heartbeat.topic";
    public static final String SAP_SIGNAVIO_LOGIN_API = "sap.signavio.login.api";
    public static final String SAP_SIGNAVIO_LOGIN_TENANTID = "sap.signavio.login.tenantId";
    public static final String SAP_SIGNAVIO_LOGIN_USERNAME = "sap.signavio.login.username";
    public static final String SAP_SIGNAVIO_LOGIN_PASSWORD = "sap.signavio.login.password";
    public static final String SAP_SIGNAVIO_FOLDER_API = "sap.signavio.folder.api";
    public static final String SAP_SIGNAVIO_MODEL_CONTENT_API = "sap.signavio.model.content.api";
    public static final String POLL_INTERNVAL_MS = "poll.interval.ms";
    public static final String HEARTBEAT_INTERVAL_MS = "heartbeat.interval.ms";



    public HttpSourceConfig(Map<String, String> props) {
        super(config(), props);
    }

    public static ConfigDef config() {
        return new ConfigDef()
                .define(KAFKA_STATUS_FOLDER_TOPIC, Type.STRING, Importance.HIGH, KAFKA_STATUS_FOLDER_TOPIC)
                .define(KAFKA_STATUS_MODEL_TOPIC, Type.STRING, Importance.HIGH, KAFKA_STATUS_MODEL_TOPIC)
                .define(KAFKA_STATUS_HEARBEAT_TOPIC, Type.STRING, Importance.HIGH, KAFKA_STATUS_HEARBEAT_TOPIC)
                .define(SAP_SIGNAVIO_LOGIN_API, Type.STRING, Importance.HIGH, SAP_SIGNAVIO_LOGIN_API)
                .define(SAP_SIGNAVIO_LOGIN_TENANTID, Type.STRING, Importance.HIGH, SAP_SIGNAVIO_LOGIN_TENANTID)
                .define(SAP_SIGNAVIO_LOGIN_USERNAME, Type.STRING, Importance.HIGH, SAP_SIGNAVIO_LOGIN_USERNAME)
                .define(SAP_SIGNAVIO_LOGIN_PASSWORD, Type.STRING, Importance.HIGH, SAP_SIGNAVIO_LOGIN_USERNAME)
                .define(SAP_SIGNAVIO_FOLDER_API, Type.STRING, Importance.HIGH, SAP_SIGNAVIO_FOLDER_API)
                .define(SAP_SIGNAVIO_MODEL_CONTENT_API, Type.STRING, Importance.HIGH, SAP_SIGNAVIO_MODEL_CONTENT_API)
                .define(POLL_INTERNVAL_MS, Type.LONG, Importance.HIGH, POLL_INTERNVAL_MS)
                .define(HEARTBEAT_INTERVAL_MS, Type.LONG, Importance.HIGH, HEARTBEAT_INTERVAL_MS);
    }

    public String getKafkaStatusFolderTopic() {
        return getString(KAFKA_STATUS_FOLDER_TOPIC);
    }
    public String getKafkaStatusModelTopic() {
        return getString(KAFKA_STATUS_MODEL_TOPIC);
    }
    public String getKafkaStatusHeartbeatTopic() {
        return getString(KAFKA_STATUS_HEARBEAT_TOPIC);
    }
    public String getSapSignavioLoginApi() {
        return getString(SAP_SIGNAVIO_LOGIN_API);
    }
    public String getSapSignavioLoginTenantId() {
        return getString(SAP_SIGNAVIO_LOGIN_TENANTID);
    }
    public String getSapSignavioLoginUsername() {
        return getString(SAP_SIGNAVIO_LOGIN_USERNAME);
    }
    public String getSapSignavioLoginPassword() {
        return getString(SAP_SIGNAVIO_LOGIN_PASSWORD);
    }
    public String getSapSignavioFolderApi() {
        return getString(SAP_SIGNAVIO_FOLDER_API);
    }
    public String getSapSignavioModelContentApi() {
        return getString(SAP_SIGNAVIO_MODEL_CONTENT_API);
    }
    public String getPollIntervalMs() {
        return getString(POLL_INTERNVAL_MS);
    }
    public String getHeartbeatIntervalMs() {
        return getString(HEARTBEAT_INTERVAL_MS);
    }

}
