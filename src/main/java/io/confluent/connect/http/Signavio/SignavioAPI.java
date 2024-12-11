package io.confluent.connect.http.Signavio;

import okhttp3.*;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SignavioAPI {

    private final String baseUrl;
    private final String workspaceId;
    private Map<String, String> dictionaryCache = new HashMap<>();
    private String authToken;
    private String jsessionId;
    private OkHttpClient client;

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    // Constructor
    public SignavioAPI(String baseUrl, String workspaceId) {
        this.baseUrl = baseUrl;
        this.workspaceId = workspaceId;
        this.client = new OkHttpClient();
        System.out.println("API object created with " + this.baseUrl);
    }

    // Authenticate method
    public void authenticate(String username, String password) throws IOException {
        System.out.println("POST /p/login");

        String loginUrl = this.baseUrl + "/p/login";
        String json = new JSONObject()
                .put("name", username)
                .put("password", password)
                .put("tokenonly", "true")
                .put("tenant", this.workspaceId)
                .toString();

        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(loginUrl)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();
            this.authToken = responseBody;  // authToken is in the response body
            this.jsessionId = response.header("JSESSIONID");  // Assuming JSESSIONID is in header

            System.out.println("Authenticated, JSESSIONID: " + jsessionId);
        }
    }
    // retrieve root directory id
    public String retrieveRootDiagramsInFolder() throws IOException {
        String url = this.baseUrl + "/p/directory";
        return apiGet(url);
    }
    // Retrieve diagrams in a folder
    public String retrieveDiagramsInFolder(String topLevelDirId) throws IOException {
        String url = this.baseUrl + "/p/directory/" + topLevelDirId;
        return apiGet(url);
    }

    // Get a dictionary entry
    public String getDic(String entryId) throws IOException {
        String entryUrl = this.baseUrl + "/p" + entryId + "/info";
        String dictionaryJson = apiGet(entryUrl);
        return dictionaryJson;
    }

    // Make a GET request to the API
    private String apiGet(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Accept", "application/json")
                .addHeader("x-signavio-id", authToken)
                .addHeader("Connection", "close")
                .addHeader("Cookie", "JSESSIONID=" + jsessionId)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            return response.body().string();
        }
    }

    // Retrieve model revision
    public String retrieveModelRevision(String revisionId) throws IOException {
        System.out.println("GET /p/revision/" + revisionId + "/json");
        String url = this.baseUrl + "/p/revision/" + revisionId + "/json";
        return apiGet(url);
    }

}
