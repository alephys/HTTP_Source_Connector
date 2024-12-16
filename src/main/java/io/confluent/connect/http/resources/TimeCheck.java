package io.confluent.connect.http.resources;

import io.confluent.connect.http.Logger.ConnectorLogger;

import java.time.Duration;
import java.time.Instant;

public class TimeCheck {
    private static final ConnectorLogger log = ConnectorLogger.getLogger(TimeCheck.class);

    public static void checkOffsetWithPollInterval(Instant lastOffsetTime, int pollIntervalMs, int sleepIntervalMs) throws InterruptedException {

        if (lastOffsetTime == null) {
            log.warn("Last offset timestamp is null. Unable to proceed.");
            return;
        }

        Duration pollInterval = Duration.ofMillis(pollIntervalMs);
        Instant pollEndTime = lastOffsetTime.plus(pollInterval);

        while (true) {
            Instant currentTime = Instant.now();
            if (currentTime.isAfter(pollEndTime)) {
                log.info("Poll interval has elapsed. Proceeding with the task...");
                break;
            } else {
                Duration remaining = Duration.between(currentTime, pollEndTime);
                log.info("Poll interval not yet completed. Time remaining: " + remaining.toMillis() + " ms. Sleeping for " + sleepIntervalMs + " ms.");
                Thread.sleep(sleepIntervalMs);
            }
        }
    }
}
