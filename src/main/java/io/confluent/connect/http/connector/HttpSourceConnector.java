package io.confluent.connect.http.connector;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.source.SourceConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class HttpSourceConnector extends SourceConnector {

    private static final Logger log = LoggerFactory.getLogger(HttpSourceConnector.class);

    private Map<String, String> config;

    @Override
    public void start(Map<String, String> config) {
        this.config = config;
        log.info("HttpSourceConnector started with config: {}", config);
    }

    @Override
    public void stop() {
        log.info("HttpSourceConnector stopped.");
    }

    @Override
    public ConfigDef config() {
        return HttpSourceConfig.config();
    }

    @Override
    public Class<? extends Task> taskClass() {
        return HttpSourceTask.class;
    }

    @Override
    public List<Map<String, String>> taskConfigs(int maxTasks) {
        return IntStream.range(0, maxTasks)
                .mapToObj(i -> config)
                .collect(Collectors.toList());
    }

    @Override
    public String version() {
        return "1.0.0"; // Connector version
    }
}