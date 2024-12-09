package io.confluent.connect.http.OffsetManager;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class OffsetManager {
    private final Instant timestamp;
    private final String uri;

    public static final String TIMESTAMP_KEY = "timestamp";
    public static final String URI_KEY = "uri";

    // Constructor
    public OffsetManager(Instant timestamp, String uri) {
        this.timestamp = timestamp;
        this.uri = uri;
    }

    // Getters
    public Instant getTimestamp() {
        return timestamp;
    }

    public String getUri() {
        return uri;
    }

    // Serialize the offset to a Map
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put(TIMESTAMP_KEY, timestamp.toString());
        map.put(URI_KEY, uri);
        return map;
    }

    // Deserialize an offset from a Map
    public static OffsetManager fromMap(Map<String, Object> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }

        String timestampStr = (String) map.get(TIMESTAMP_KEY);
        String uri = (String) map.get(URI_KEY);

        Instant timestamp = (timestampStr != null) ? Instant.parse(timestampStr) : null;
        return new OffsetManager(timestamp, uri);
    }

    @Override
    public String toString() {
        return "OffsetManager{" +
                "timestamp=" + timestamp +
                ", uri='" + uri + '\'' +
                '}';
    }
}