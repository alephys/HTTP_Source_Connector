package io.confluent.connect.http.resources;

import java.time.Duration;
import java.time.Instant;

import java.time.Instant;
import java.time.Duration;

public class TimeChecker {

    private static final long ONE_DAY_IN_SECONDS = 24 * 60 * 60;

    public static boolean isOlderThan24Hours(long lastOffsetTimestamp) {
        long currentTime = Instant.now().getEpochSecond();
        return (currentTime - lastOffsetTimestamp) >= ONE_DAY_IN_SECONDS;
    }


    public static void sleep(long durationMillis) {
        try {
            Thread.sleep(durationMillis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Sleep interrupted: " + e.getMessage());
        }
    }
}

