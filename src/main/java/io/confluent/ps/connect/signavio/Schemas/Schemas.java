package io.confluent.ps.connect.signavio.Schemas;

import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;

public class Schemas {

    private static String REL = "rel";
    private static String HREF = "href";
    private static String PARENT = "parent";
    private static String ALLOWED_MIME_TYPE_REGEX = "allowedMimeTypeRegex";
    private static String PARENT_NAME = "parentName";
    private static String DELETED = "deleted";
    private static String VISIBLE = "visible";
    private static String CREATED = "created";
    private static String NAME = "name";
    private static String DESCRIPTION = "description";
    private static String NAME_EN = "name_en";


    private static String MODEL_ID = "model_id";
    private static String PUBLISHED_DATE = "published_date";
    private static String PUBLISHED_REVISION = "published_revision";
    private static String PUBLISHED_REVISION_NUMBER = "published_revision_number";
    private static String MODEL_NAME = "model_name";
    private static String COMMENT = "comment";
    private static String MODELLER = "modeller";
    private static String LAST_EDIT = "last_edit";
    private static String FOLDER_NAME = "folder_name";
    private static String STENCIL_SET = "stencil_set";
    private static String NAME_SPACE = "name_space";
    private static String URL = "url";
    private static String PROCESS_OWNER = "process_owner";
    private static String PROCESS_OWNER_EMAIL = "process_owner_email";
    private static String ASSURANCE_LEAD = "assurance_lead";
    private static String ASSURANCE_LEAD_EMAIL = "assurance_lead_email";
    private static String E2E_PROCESS_OWNER = "e2e_process_owner";
    private static String E2E_PROCESS_OWNER_EMAIL = "e2e_process_owner_email";
    private static String PCF_ID = "pcf_id";
    private static String CRITICAL_OPERATIONS = "critical_operations";
    private static String LAST_ATTESTATION_DATE = "last_attestation_date";
    private static String CRITICAL_OPERATIONALS_CATEGAORIES = "critical_operations_categories";
    private static String DOCUMENTATION = "documentation";
    private static String DIVISION = "division";
    private static String DIVISION_CODE = "division_code";
    private static String PROCESS_TYPE = "process_type";




    public static final Schema KEY_SCHEMA = SchemaBuilder.struct()
            .name("issue_key")
            .field("id", Schema.STRING_SCHEMA)
            .build();


    public static final Schema DIRECTORY_DATA_SCHEMA = SchemaBuilder.struct()
            .name("directory_data")
            .field(REL, Schema.STRING_SCHEMA)
            .field(HREF, Schema.STRING_SCHEMA)
            .field(PARENT, Schema.STRING_SCHEMA)
            .field(ALLOWED_MIME_TYPE_REGEX, Schema.STRING_SCHEMA)
            .field(PARENT_NAME, Schema.STRING_SCHEMA)
            .field(DELETED, Schema.BOOLEAN_SCHEMA)
            .field(VISIBLE, Schema.BOOLEAN_SCHEMA)
            .field(CREATED, Schema.STRING_SCHEMA)
            .field(NAME, Schema.STRING_SCHEMA)
            .field(DESCRIPTION, Schema.STRING_SCHEMA)
            .field(NAME_EN, Schema.STRING_SCHEMA)
            .build();

    public static final Schema MODEL_DATA_SCHEMA = SchemaBuilder.struct()
            .name("model_data")
            .field(MODEL_ID, Schema.STRING_SCHEMA)
            .field(NAME, Schema.STRING_SCHEMA)
            .field(PUBLISHED_DATE, Schema.STRING_SCHEMA)
            .field(PUBLISHED_REVISION, Schema.STRING_SCHEMA)
            .field(PUBLISHED_REVISION_NUMBER, Schema.BOOLEAN_SCHEMA)
            .field(MODEL_NAME, Schema.STRING_SCHEMA)
            .field(COMMENT, Schema.STRING_SCHEMA)
            .field(MODELLER, Schema.STRING_SCHEMA)
            .field(LAST_EDIT, Schema.STRING_SCHEMA)
            .field(PARENT, Schema.BOOLEAN_SCHEMA)
            .field(FOLDER_NAME, Schema.STRING_SCHEMA)
            .field(STENCIL_SET, SchemaBuilder.struct()
                    .name("stencil_set")
                    .field(NAME_SPACE, Schema.STRING_SCHEMA)
                    .field(URL, Schema.STRING_SCHEMA)
                    .build())
            .field(PROCESS_OWNER, Schema.STRING_SCHEMA)
            .field(PROCESS_OWNER_EMAIL, Schema.STRING_SCHEMA)
            .field(ASSURANCE_LEAD, Schema.STRING_SCHEMA)
            .field(ASSURANCE_LEAD_EMAIL, Schema.STRING_SCHEMA)
            .field(E2E_PROCESS_OWNER, Schema.STRING_SCHEMA)
            .field(E2E_PROCESS_OWNER_EMAIL, Schema.STRING_SCHEMA)
            .field(PROCESS_TYPE, Schema.STRING_SCHEMA)
            .field(PCF_ID, Schema.BOOLEAN_SCHEMA)
            .field(CRITICAL_OPERATIONS, Schema.STRING_SCHEMA)
            .field(LAST_ATTESTATION_DATE, Schema.STRING_SCHEMA)
            .field(CRITICAL_OPERATIONALS_CATEGAORIES, Schema.STRING_SCHEMA)
            .field(DOCUMENTATION, Schema.STRING_SCHEMA)
            .field(DIVISION, Schema.STRING_SCHEMA)
            .field(DIVISION_CODE, Schema.STRING_SCHEMA)
            .build();
}

