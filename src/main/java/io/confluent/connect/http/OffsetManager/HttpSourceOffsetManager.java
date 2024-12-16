package io.confluent.connect.http.OffsetManager;

import org.apache.kafka.connect.source.SourceTaskContext;
import org.apache.kafka.connect.storage.OffsetStorageReader;

import java.util.HashMap;
import java.util.Map;

public class HttpSourceOffsetManager {

    private final OffsetStorageReader offsetReader;
    private final String uri;

    public HttpSourceOffsetManager(SourceTaskContext context, String uri) {
        this.offsetReader = context.offsetStorageReader();
        this.uri = uri;
    }

    public Map<String, Object> getHighWatermark() {
        Map<String, String> partition = new HashMap<>();
        partition.put("uri", uri);

        // Fetch the committed offset for the partition
        Map<String, Object> offset = offsetReader.offset(partition);

        if (offset == null) {
            offset = new HashMap<>();
            offset.put("timestamp", 0L); // Default timestamp
            offset.put("nextUri", "");   // Default next URI
        }

        return offset;
    }


    public Map<String, Object> createOffset(long timestamp, String nextUri) {
        Map<String, Object> newOffset = new HashMap<>();
        newOffset.put("uri", uri);
        newOffset.put("timestamp", timestamp);
        newOffset.put("nextUri", nextUri);

        return newOffset;
    }
}

