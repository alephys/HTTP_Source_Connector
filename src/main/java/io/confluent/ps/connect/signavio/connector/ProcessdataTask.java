package io.confluent.ps.connect.signavio.connector;

import io.confluent.ps.connect.signavio.OffsetManager.HttpSourceOffsetManager;
import io.confluent.ps.connect.signavio.model.Directory;
import io.confluent.ps.connect.signavio.model.Model;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import io.confluent.ps.connect.signavio.resources.TimeCheck;
import io.confluent.ps.connect.signavio.Logger.ConnectorLogger;
import io.confluent.ps.connect.signavio.Signavio.SignavioAPI;
import static io.confluent.ps.connect.signavio.schemas.Schemas.*;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.time.Instant;
import java.util.*;

public class ProcessdataTask extends SourceTask {
    private static final ConnectorLogger log = ConnectorLogger.getLogger(ProcessdataTask.class);
    private String dir_topic;
    private String model_topic;
    private String httpEndpoint;
    private int POLL_INTERVAL_MS;
    private int HEARTBEAT_INTERVAL_MS;
    private String USERNAME;
    private String PASSWORD;
    private String nextUri;
    private String TENANT_ID;
    private String PUBLISHED_ROOT_ID;
    private String RETIRED_ROOT_ID;
    private SignavioAPI signavioApi;
    private HttpSourceOffsetManager offsetManager;
    private Instant lastTimestamp;
    private ProcessdataConfig config;



    @Override
    public String version() {
        return "1.0.0";
    }

    @Override
    public void start(Map<String, String> props) {
        log.info("Starting HttpSourceTask with provided configuration...");
        config = new ProcessdataConfig(props);
        this.dir_topic = config.getTopic();
        this.model_topic = config.getModelTopic();
        this.httpEndpoint = config.getHttpEndpoint();
        this.POLL_INTERVAL_MS = config.getPollIntervalMs();
        this.HEARTBEAT_INTERVAL_MS = config.getHeartBeatIntervalMs();
        this.USERNAME = config.getUserName();
        this.PASSWORD = config.getPassword();
        this.TENANT_ID = config.getTenantId();
        this.signavioApi = new SignavioAPI(httpEndpoint, TENANT_ID, config);
        this.offsetManager = new HttpSourceOffsetManager(context,httpEndpoint);
        this.PUBLISHED_ROOT_ID = config.getPublishedRootId();
        this.RETIRED_ROOT_ID = config.getRetiredRootId();
        Map<String, Object> highWatermark = offsetManager.getHighWatermark();
        lastTimestamp = Instant.ofEpochMilli((Long) highWatermark.get("timestamp"));
        nextUri = (String) highWatermark.get("nextUri");
        log.info("HttpSourceTask started successfully with endpoint: {}");
    }

    @Override
    public List<SourceRecord> poll() throws InterruptedException {
        List<SourceRecord> records = new ArrayList<>();
        try {
            if (lastTimestamp != null) {
                log.info("Last offset timestamp: " + lastTimestamp);
                TimeCheck.checkOffsetWithPollInterval(lastTimestamp, POLL_INTERVAL_MS * 60 * 1000, HEARTBEAT_INTERVAL_MS);
            } else {
                log.warn("No timestamp found in the last offset. Unable to proceed.");
            }
            log.debug("Authenticating with Signavio API...");
            signavioApi.authenticate(USERNAME, PASSWORD);

            log.info("Fetching published diagrams...");
            records = fetching(PUBLISHED_ROOT_ID,records);

            log.info("Fetching retired diagrams...");
            records = fetching(RETIRED_ROOT_ID,records);

            log.info("Polling completed with "+records.size()+" records." );
        } catch (Exception e) {
            log.error("Error during polling. Details: "+ e.getMessage());
            handleDLQ(e,"Error during polling.",records);
        }
        return records;
    }

    private List<SourceRecord> fetching(String id,List<SourceRecord> records) throws IOException {
        log.debug("Fetching diagrams for root ID: "+id);
        String topLevelDirectoryJson = signavioApi.retrieveRootDiagramsInFolder(id);
        JSONArray responseJson = new JSONArray(topLevelDirectoryJson);
        ArrayList<String> topLevelDirectoryId = new ArrayList<>();
        for (int i=0; i < responseJson.length(); i++) {
            JSONObject res = responseJson.getJSONObject(i);
            if("dir".equals(res.getString("rel"))) {
                String temp = res.getString("href").replace("/directory/","");
                topLevelDirectoryId.add(temp);
            }
        }
        log.info("Found "+topLevelDirectoryId.size()+"directories for root ID: "+ id);

        for (int i=0; i< topLevelDirectoryId.size(); i++) {
            String directoryResponse = signavioApi.retrieveDiagramsInFolder(topLevelDirectoryId.get(i));
            JSONArray folderResponseJson = new JSONArray(directoryResponse);
            records = (retrivediagramMetaData(folderResponseJson,records));
        }
        return records;
    }

    private List<SourceRecord> retrivediagramMetaData(JSONArray dirResponse, List<SourceRecord> records) throws IOException {
        for(int i=0; i < dirResponse.length(); i++) {
            JSONObject dirJson = dirResponse.getJSONObject(i);

            if("info".equals(dirJson.getString("rel"))) {
                String dirId = dirJson.getString("href").replace("/directory/","");
                log.debug("Processing directory metadata with id: "+ dirId );
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
                nextUri = httpEndpoint+"/p/directory/"+dirId;
                records.add(createSourceRecordForDirectory(res,"directory_id",dirId,nextUri));
            }
            if("dir".equals(dirJson.getString("rel"))){
                log.debug("Found subdirectory. Recursively processing...");
                retrivediagramMetaData(dirResponse, records);
            }else if("mod".equals(dirJson.getString("rel"))){
                log.debug("Processing model metadata...");
                JSONObject modJson = dirJson.getJSONObject("rep");
                boolean published = modJson.getJSONObject("status").getBoolean("publish");
                boolean retired = modJson.getJSONObject("status").getBoolean("retired");
                if(published || retired) {
                    String modelId = modJson.getString("href").replace("/model/", "");
                    String modelRevision = "";
                    if (modJson.getString("granted_revision").isEmpty()) {
                        modelRevision = modJson.getString("revision").replace("/revision/", "");
                    } else {
                        modelRevision = modJson.getString("granted_revision").replace("/revision/", "");
                    }
                    String tempPubDate = modJson.getString("granted_revision_date").split("\\+")[0];
                    String pubDate = tempPubDate.split(" ")[0];
                    String tempLastEdit = modJson.getString("updated").split("\\+")[0];
                    String lastEdit = tempLastEdit.split(" ")[0];
                    Map<String, String> modelRev = retriveModelRevision(modelRevision);
                    log.debug("Processing model metadata with id: "+ modelId );
                    Model m = new Model(modelId,
                            pubDate,
                            modJson.getString("granted_revision"),
                            modJson.getString("granted_revision_number"),
                            modJson.getString("name"),
                            modJson.getString("comment"),
                            modJson.getString("modeller"),
                            "Published",
                            lastEdit,
                            modJson.getString("parentName"),
                            modJson.getJSONObject("rep").getString("parent"),
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
                            modelRev.get("division"),
                            modelRev.get("division_code"));
                    nextUri = httpEndpoint+"/p/directory/"+modelId+"/json";
                    records.add(createSourceRecordForModel(m, "model_id", modelId,nextUri));
                }
            }
        }
        return records;
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

        String divisionCodeId = modelRevisionJson.getJSONObject("properties").getString("Pending");
        String  divCodeDic = signavioApi.getDic(divisionCodeId);
        JSONObject divCodeDicResJson = new JSONObject(divCodeDic);
        modelRevisionMap.put("division_code", divCodeDicResJson.getString("title"));

        return modelRevisionMap;

    }

    public static List<String> dictionaryListRetrieval(JSONArray jsonArray) {
        List<String> result = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            result.add(jsonArray.getString(i));
        }
        return result;
    }

    private SourceRecord createSourceRecordForDirectory(Directory dir,String key,String value,String nextUri) {
        return new SourceRecord(sourcePartition(key,value), offsetManager.createOffset(System.currentTimeMillis(),nextUri), dir_topic,KEY_SCHEMA,directoryRecordKey(dir), DIRECTORY_DATA_SCHEMA, directoryRecordValue(dir));
    }

    private SourceRecord createSourceRecordForModel(Model model, String key, String value, String nextUri) {
        return new SourceRecord(sourcePartition(key,value), offsetManager.createOffset(System.currentTimeMillis(),nextUri), model_topic,KEY_SCHEMA, ModelRecordKey(model), MODEL_DATA_SCHEMA, model.toString());
    }

    private Map<String, String> sourcePartition(String key,String value) {
        Map<String, String> partition = new HashMap<>();
        partition.put(key, value);
        return partition;
    }
    private Struct directoryRecordKey(Directory dir){
        // Key Schema
        Struct key = new Struct(KEY_SCHEMA)
                .put("id", dir.getDirectory_id());
        return key;
    }

    private Struct ModelRecordKey(Model model){
        // Key Schema
        Struct key = new Struct(KEY_SCHEMA)
                .put("id", model.getModel_id());
        return key;
    }

    public Struct directoryRecordValue(Directory dir){
        Struct directoryValue = new Struct(DIRECTORY_DATA_SCHEMA)
                .put("rel",dir.getRel())
                .put("href",dir.getDirectory_id())
                .put("parent",dir.getParent())
                .put("allowedMimeTypeRegex",dir.getAllowedMimeTypeRegex())
                .put("parentName",dir.getParentName())
                .put("deleted",dir.getDeleted())
                .put("visible",dir.getVisible())
                .put("created",dir.getCreated())
                .put("name",dir.getName())
                .put("description",dir.getDescription())
                .put("name_en",dir.getNameEn());
        return directoryValue;
    }

    public Struct ModelRecordValue(Model model){
        Struct modelValue = new Struct(MODEL_DATA_SCHEMA)
                .put("model_id", model.getModel_id())
                .put("name", model.getModel_name())
                .put("published_date", model.getPublished_date())
                .put("published_revision", model.getPublished_revision())
                .put("published_revision_number", model.getPublished_revision_number())
                .put("model_name", "Process Model")
                .put("comment", "This is a sample comment.")
                .put("modeller", "John Doe")
                .put("last_edit", "2023-12-18")
                .put("parent", false)
                .put("folder_name", "Root Folder")
                .put("stencil_set", new Struct(MODEL_DATA_SCHEMA.field("stencil_set").schema())
                        .put("name_space", "namespace.example.com")
                        .put("url", model.getStencil_set().getUrl()))
                .put("process_owner", model.getProcess_owner())
                .put("process_owner_email", model.getProcess_owner_email())
                .put("assurance_lead", model.getAssurance_lead())
                .put("assurance_lead_email", model.getAssurance_lead_email())
                .put("e2e_process_owner", model.getE2e_process_owner())
                .put("e2e_process_owner_email", model.getE2e_process_owner_email())
                .put("process_type", model.getProcess_type())
                .put("pcf_id", model.getPcf_id())
                .put("critical_operations", model.getCritical_operations())
                .put("last_attestation_date", model.getLast_attestation_date())
                .put("critical_operationals_categaories", model.getCritical_operations_categories())
                .put("documentation", model.getDocumentation())
                .put("division", model.getDivision())
                .put("division_code", model.getDivision_code());
        return modelValue;
    }



    private List<SourceRecord> handleDLQ(Exception exception, String failedData,List<SourceRecord> records) {
        Boolean dlq = config.getDlqReportErrors();
        if (dlq) {
            String dlqTopic = config.getDlqTopic();
            try {
                Map<String, String> partition = Collections.singletonMap("source", "http-source");
                Map<String, String> offset = Collections.singletonMap("failed-timestamp", String.valueOf(System.currentTimeMillis()));

                SourceRecord dlqRecord = new SourceRecord(
                        partition,
                        offset,
                        dlqTopic,
                        Schema.STRING_SCHEMA,
                        failedData,
                        Schema.STRING_SCHEMA,
                        exception.getMessage()
                );
                records.add(dlqRecord);
                log.warn("Record sent to DLQ: "+ dlqRecord);
            } catch (Exception dlqException) {
                log.error("Failed to send record to DLQ: "+ dlqException);
            }
        }
        return records;
    }


    @Override
    public void stop() {
    }
}