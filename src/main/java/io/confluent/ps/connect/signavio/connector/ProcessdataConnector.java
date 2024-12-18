package io.confluent.ps.connect.signavio.connector;

import io.confluent.ps.connect.signavio.Logger.ConnectorLogger;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.source.SourceConnector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProcessdataConnector extends SourceConnector {
    private Map<String, String> configProps;
    private static final ConnectorLogger log = ConnectorLogger.getLogger(ProcessdataConnector.class);

    @Override
    public String version() {
        return "1.0.0";
    }

    @Override
    public void start(Map<String, String> props) {
        this.configProps = props;
        log.info("Connector has been started");
    }

    @Override
    public Class<? extends Task> taskClass() {
        log.info("Task has been started");
        return ProcessdataTask.class;
    }

    @Override
    public List<Map<String, String>> taskConfigs(int maxTasks) {
        List<Map<String, String>> configs = new ArrayList<>();
        for (int i = 0; i < maxTasks; i++) {
            configs.add(configProps);
        }
        return configs;
    }

    @Override
    public void stop() {
        log.info("Connector has been stopped");
    }

    @Override
    public ConfigDef config() {
        return ProcessdataConfig.config();
    }
}