package io.confluent.connect.http.connector;

import io.confluent.connect.http.Schemas.Schemas;
import io.confluent.connect.http.model.Directory;
import io.confluent.connect.http.model.Model;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import io.confluent.connect.http.OffsetManager.OffsetManager;
import io.confluent.connect.http.resources.TimeCheck;
import io.confluent.connect.http.Logger.ConnectorLogger;
import io.confluent.connect.http.Signavio.SignavioAPI;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpSourceTask1 extends SourceTask {
    private static final ConnectorLogger log = ConnectorLogger.getLogger(HttpSourceTask1.class);
    private String dir_topic;
    private String model_topic;
    private String httpEndpoint;
    private int POLL_INTERVAL_MS;
    private int HEARTBEAT_INTERVAL_MS;
    private String USERNAME;
    private String PASSWORD;
    private String nextUri;
    private String TENANT_ID;
    private SignavioAPI signavioApi;
    private OffsetManager offsetManager;



    @Override
    public String version() {
        return "1.0.0";
    }

    @Override
    public void start(Map<String, String> props) {
        HttpSourceConfig config = new HttpSourceConfig(props);
        this.dir_topic = config.getTopic();
        this.model_topic = config.getModelTopic();
        this.httpEndpoint = config.getHttpEndpoint();
        this.POLL_INTERVAL_MS = config.getPollIntervalMs();
        this.HEARTBEAT_INTERVAL_MS = config.getHeartBeatIntervalMs();
        this.USERNAME = config.getUserName();
        this.PASSWORD = config.getPassword();
        this.TENANT_ID = config.getTenantId();
        this.signavioApi = new SignavioAPI(httpEndpoint, TENANT_ID);
        this.offsetManager = new OffsetManager();
        Map<String, Object> offsetMap = context.offsetStorageReader().offset(sourcePartition());
        offsetManager = OffsetManager.fromMap(offsetMap);
        if (offsetManager != null) {
            nextUri = offsetManager.getUri();
        } else {
        }
    }

    private Map<String, String> sourcePartition(String key,String value) {
        Map<String, String> partition = new HashMap<>();
        partition.put(key, value);
        return partition;
    }

    @Override
    public List<SourceRecord> poll() throws InterruptedException {
        List<SourceRecord> records = new ArrayList<>();
        try {

            Map<String, Object> lastOffsetMap = context.offsetStorageReader().offset(sourcePartition("http-endpoint",httpEndpoint));
            if (lastOffsetMap == null) {
                log.info("No previous offset found. Starting from the beginning.");
            }

            OffsetManager lastOffset = OffsetManager.fromMap(lastOffsetMap);
            if (lastOffset == null) {
                log.warn("Failed to parse offset. Starting from the beginning.");
            }

            Instant lastTimestamp = lastOffset.getTimestamp();
            if (lastTimestamp != null) {
                log.info("Last offset timestamp: " + lastTimestamp);
                TimeCheck.checkOffsetWithPollInterval(lastTimestamp, POLL_INTERVAL_MS * 60 * 1000, HEARTBEAT_INTERVAL_MS);
            } else {
                log.warn("No timestamp found in the last offset. Unable to proceed.");
            }
            signavioApi.authenticate(USERNAME, PASSWORD);

            String topLevelDirectoryJson = signavioApi.retrieveRootDiagramsInFolder();
            JSONArray responseJson = new JSONArray(topLevelDirectoryJson);
            ArrayList<String> topLevelDirectoryId = new ArrayList<>();
            for (int i=0; i < responseJson.length(); i++) {
                JSONObject res = responseJson.getJSONObject(i);
                if("dir".equals(res.getString("rel"))) {
                    String temp = res.getString("href").replace("/directory/","");
                    topLevelDirectoryId.add(temp);
                }
            }

            for (int i=0; i< topLevelDirectoryId.size(); i++) {
                String directoryResponse = signavioApi.retrieveDiagramsInFolder(topLevelDirectoryId.get(i));
                JSONArray folderResponseJson = new JSONArray(directoryResponse);
                retrivediagramMetaData(folderResponseJson,records);
            }


        } catch (Exception e) {
            log.error("");
        }
        return records;
    }

    private void retrivediagramMetaData(JSONArray dirResponse, List<SourceRecord> records) throws IOException {

        for(int i=0; i < dirResponse.length(); i++) {
            JSONObject dirJson = dirResponse.getJSONObject(i);

            if("info".equals(dirJson.getString("rel"))) {
                Directory res = new Directory(dirJson.getString("rel"),
                        dirJson.getString("href").replace("/directory/",""),
                        dirJson.getJSONObject("rep").getString("parent"),
                        dirJson.getJSONObject("rep").getString("allowedMimeTypeRegex"),
                        dirJson.getJSONObject("rep").getString("parentName"),
                        dirJson.getJSONObject("rep").getBoolean("deleted"),
                        dirJson.getJSONObject("rep").getBoolean("visible"),
                        dirJson.getJSONObject("rep").getString("created"),
                        dirJson.getJSONObject("rep").getString("name"),
                        dirJson.getJSONObject("rep").getString("description"),
                        dirJson.getJSONObject("rep").getString("name_en"));
                records.add(createSourceRecordForDirectory(res,"directory_id",dirJson.getString("href").replace("/directory/",""));
            }
            if("dir".equals(dirJson.getString("rel"))){
                String dirId = dirJson.getString("rep").replace("/directory/","");
                retrivediagramMetaData(dirResponse, records);
            }else if("mod".equals(dirJson.getString("rel"))){
                JSONObject modJson = dirJson.getJSONObject("rep");
                boolean published = modJson.getJSONObject("status").getBoolean("publish");
                String modelId = modJson.getString("href").replace("/model/","");
                String modelRevision = "";
                if(modJson.getString("granted_revision").isEmpty()){
                    modelRevision = modJson.getString("revision").replace("/revision/","");
                }else{
                    modelRevision = modJson.getString("granted_revision").replace("/revision/","");
                }
                String tempPubDate = modJson.getString("granted_revision_date").split("\\+")[0];
                String pubDate = tempPubDate.split(" ")[0];
                String tempLastEdit = modJson.getString("updated").split("\\+")[0];
                String lastEdit = tempLastEdit.split(" ")[0];
                Map<String,String> modelRev = retriveModelRevision(modelRevision);
                Model m = new Model(modJson.getString("href").replace("/model/",""),
                        modJson.getString(pubDate),
                        modJson.getString("granted_revision"),
                        modJson.getString("granted_revision_number"),
                        modJson.getString("name"),
                        modJson.getString("comment"),
                        modJson.getString("modeller"),
                        "Published",
                        lastEdit,
                        modJson.getString("parentName"),
                        dirJson.getJSONObject("rep").getString("name"),
                        new Model.StencilSet(
                                modJson.getJSONObject("stencil_set").getString("name_space"),
                                modJson.getJSONObject("stencil_set").getString("url")),
                        modelRev.get("process_owner"),
                        modelRev.get("process_owner_email"),
                        modelRev.get("assurance_lead"),
                        modelRev.get("assurance_lead_email"),
                        modelRev.get("e2e_process_owner"),
                        modelRev.get("e2e_process_owner_email"),
                        modelRev.get("process_type"),
                        modelRev.get("pcf_id"),
                        modelRev.get("critical_operations"),
                        modelRev.get("last_attestation_date"),
                        modelRev.get("critical_operation_categories"),
                        modelRev.get("documentation"),
                        modelRev.get("division"));

                records.add(createSourceRecordForModel(m,"model_id",modJson.getString("href").replace("/model/",""));

            }
        }
    }

    private Map<String,String> retriveModelRevision(String modelRevision) throws IOException {
        Map<String,String> modelRevisionMap = new HashMap<>();
        String modelRev = signavioApi.retrieveModelRevision(modelRevision);
        JSONObject modelRevisionJson = new JSONObject(modelRev);
        modelRevisionMap.put("process_id", modelRevisionJson.getJSONObject("properties").getString("meta-processid"));

        String processOwnerId = modelRevisionJson.getJSONObject("properties").getString("meta-processowner");
        String proDic = signavioApi.getDic(processOwnerId);
        JSONObject proDicResJson = new JSONObject(proDic);
        modelRevisionMap.put("process_owner", proDicResJson.getString("title"));
        modelRevisionMap.put("process_owner_email", proDicResJson.getString("meta-email"));

        String assuranceLeadId = modelRevisionJson.getJSONObject("properties").getString("meta-riskandcontrolapprover");
        String  assDic = signavioApi.getDic(assuranceLeadId);
        JSONObject assDicResJson = new JSONObject(assDic);
        modelRevisionMap.put("assurance_lead", assDicResJson.getString("title"));
        modelRevisionMap.put("assurance_lead_email", assDicResJson.getString("meta-email"));

        String e2eProcessownerId = modelRevisionJson.getJSONObject("properties").getString("meta-e2eprocessowner");
        String  e2eDic = signavioApi.getDic(e2eProcessownerId);
        JSONObject e2eDicResJson = new JSONObject(e2eDic);
        modelRevisionMap.put("e2e_process_owner", e2eDicResJson.getString("title"));
        modelRevisionMap.put("e2e_process_owner_email", e2eDicResJson.getString("meta-email"));

        String processCategoryId = modelRevisionJson.getJSONObject("properties").getString("meta-processcategory");
        String  proCatDic = signavioApi.getDic(processCategoryId);
        JSONObject proCatDicResJson = new JSONObject(proCatDic);
        modelRevisionMap.put("process_type", proCatDicResJson.getString("title"));

        String pcfId = modelRevisionJson.getJSONObject("properties").getString("meta-apqcframework");
        String  pcfDic = signavioApi.getDic(pcfId);
        JSONObject pCfDicResJson = new JSONObject(pcfDic);
        modelRevisionMap.put("pcf_id", pCfDicResJson.getString("title"));

        String criticalOperations = modelRevisionJson.getJSONObject("properties").getString("meta-criticaloperations");
        modelRevisionMap.put("critical_operations", criticalOperations);

        String last_attestation_date = modelRevisionJson.getJSONObject("properties").getString("meta-lastattestationdate");
        modelRevisionMap.put("last_attestation_date", last_attestation_date);

        JSONArray catList = modelRevisionJson.getJSONObject("properties").getJSONArray("meta-criticaloperationscateg");
        List<String> catListRes =dictionaryListRetrieval(catList);
        modelRevisionMap.put("critical_operation_categories",String.join(",",catListRes));

        String documentation = modelRevisionJson.getJSONObject("properties").getString("documentation");
        modelRevisionMap.put("documentation", documentation);

        String divisionId = modelRevisionJson.getJSONObject("properties").getString("meta-anzdivision2");
        String  divDic = signavioApi.getDic(divisionId);
        JSONObject divDicResJson = new JSONObject(divDic);
        modelRevisionMap.put("division", divDicResJson.getString("title"));

        return modelRevisionMap;

    }

    public static List<String> dictionaryListRetrieval(JSONArray jsonArray) {
        List<String> result = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            result.add(jsonArray.getString(i));
        }
        return result;
    }

    private SourceRecord createSourceRecordForDirectory(Directory dir,String key,String value) {
        return new SourceRecord(sourcePartition(key,value), offsetManager.toMap(), dir_topic, Schemas.DIRECTORY_DATA_SCHEMA, dir.toString());
    }

    private SourceRecord createSourceRecordForModel(Model model,String key,String value) {
        return new SourceRecord(sourcePartition(key,value), offsetManager.toMap(), model_topic, Schemas.MODEL_DATA_SCHEMA, model.toString());
    }

    @Override
    public void stop() {
    }
}