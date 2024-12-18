package io.confluent.ps.connect.signavio.Logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectorLogger {
    private final Logger logger;


    private ConnectorLogger(Class<?> clazz) {
        this.logger = LoggerFactory.getLogger(clazz);
    }


    public static ConnectorLogger getLogger(Class<?> clazz) {
        return new ConnectorLogger(clazz);
    }

    public void info(String message) {
        logger.info(message);
    }

    public void error(String message) {
        logger.error(message);
    }

    public void debug(String message) {
        logger.debug(message);
    }

    public void warn(String message) {
        logger.warn(message);
    }

    public void trace(String message) {
        logger.trace(message);
    }
}