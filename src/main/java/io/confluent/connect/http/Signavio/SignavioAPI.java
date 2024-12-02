package io.confluent.connect.http.Signavio;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.json.JSONObject;

public class SignavioAPI {

    private final String baseUrl;
    private final String workspaceId;
    private final HttpClient httpClient;
    private Map<String, JSONObject> dictionaryCache;
    private Map<String, String> cookies;
    private Map<String, String> headers;

    public SignavioAPI(String baseUrl, String workspaceId) {
        this.baseUrl = baseUrl;
        this.workspaceId = workspaceId;
        this.httpClient = HttpClient.newBuilder().build();
        this.dictionaryCache = new HashMap<>();
        this.cookies = new HashMap<>();
        this.headers = new HashMap<>();
        System.out.println("API object created with " + this.baseUrl);
    }

    public void authenticate(String username, String password) throws IOException, InterruptedException {
        System.out.println("POST /p/login");
        String loginUrl = baseUrl + "/p/login";
        String requestBody = String.format(
                "name=%s&password=%s&tokenonly=true&tenant=%s",
                username, password, workspaceId
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(loginUrl))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        String authToken = response.body();
        String jsessionId = response.headers().map().get("set-cookie").stream()
                .filter(cookie -> cookie.startsWith("JSESSIONID"))
                .findFirst()
                .map(cookie -> cookie.split(";")[0].split("=")[1])
                .orElseThrow(() -> new IllegalStateException("JSESSIONID not found"));

        cookies.put("JSESSIONID", jsessionId);
        headers.put("Accept", "application/json");
        headers.put("x-signavio-id", authToken);
        headers.put("Connection", "close");
    }

    public JSONObject retrieveDiagramsInFolder(String topLevelDirId) throws IOException, InterruptedException {
        System.out.println("Retrieving directory with ID " + topLevelDirId);
        String url = baseUrl + "/p/directory/" + topLevelDirId;
        String response = apiGet(url);
        return new JSONObject(response);
    }

    public JSONObject getDictionaryEntry(String entryId) throws IOException, InterruptedException {
        String entryUrl = baseUrl + "/p" + entryId + "/info";

        if (dictionaryCache.containsKey(entryUrl)) {
            System.out.println("Item " + entryId + " cached. Retrieving from cache...");
            return dictionaryCache.get(entryUrl);
        } else {
            System.out.println("Item " + entryId + " not in dictionary cache. Retrieving...");
            String response = apiGet(entryUrl);
            JSONObject jsonResponse = new JSONObject(response);
            dictionaryCache.put(entryUrl, jsonResponse);
            return jsonResponse;
        }
    }

    private String apiGet(String url) throws IOException, InterruptedException {
        boolean apiSuccess = false;
        String response = null;

        while (!apiSuccess) {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .headers(buildHeaders())
                        .GET()
                        .build();

                HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                response = httpResponse.body();
                apiSuccess = true;
            } catch (Exception e) {
                System.out.println("api_get fail, retrying: " + e.getMessage());
                TimeUnit.SECONDS.sleep(2);
            }
        }
        return response;
    }

    public JSONObject retrieveModelRevision(String revisionId) throws IOException, InterruptedException {
        System.out.println("GET /p/revision/" + revisionId + "/json");
        String url = baseUrl + "/p/revision/" + revisionId + "/json";
        String response = apiGet(url);
        return new JSONObject(response);
    }

    public JSONObject retrieveDiagramInfo(String modelId) throws IOException, InterruptedException {
        System.out.println("GET /p/model/" + modelId + "/info");
        String url = baseUrl + "/p/model/" + modelId + "/info";
        String response = apiGet(url);
        return new JSONObject(response);
    }

    public JSONObject getModelInfo(String modelId) throws IOException, InterruptedException {
        System.out.println("Getting workflow status for model: " + modelId);
        String url = baseUrl + "/p/model/" + modelId + "/info";
        String response = apiGet(url);
        return new JSONObject(response);
    }

    public JSONObject retrieveModelRevisions(String modelId) throws IOException, InterruptedException {
        System.out.println("Getting revisions for model: " + modelId);
        String url = baseUrl + "/p/model/" + modelId + "/revisions";
        String response = apiGet(url);
        return new JSONObject(response);
    }

    private String[] buildHeaders() {
        return headers.entrySet().stream()
                .flatMap(entry -> Map.of(entry.getKey(), entry.getValue()).entrySet().stream())
                .flatMap(map -> map.entrySet().stream())
                .map(Map.Entry::toString)
                .toArray(String[]::new);
    }
}

