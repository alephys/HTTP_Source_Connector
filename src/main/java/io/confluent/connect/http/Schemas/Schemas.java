package io.confluent.connect.http.Schemas;

import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;

public class Schemas {

    public static final Schema KEY_SCHEMA = SchemaBuilder.struct()
            .name("issue_key")
            .field("id", Schema.STRING_SCHEMA)
            .build();


    public static final Schema DIRECTORY_DATA_SCHEMA = SchemaBuilder.struct()
            .name("directory_data")
            .field("rel", Schema.STRING_SCHEMA)
            .field("href", Schema.STRING_SCHEMA)
            .field("parent", Schema.STRING_SCHEMA)
            .field("allowedMimeTypeRegex", Schema.STRING_SCHEMA)
            .field("parentName", Schema.STRING_SCHEMA)
            .field("deleted", Schema.BOOLEAN_SCHEMA)
            .field("visible", Schema.BOOLEAN_SCHEMA)
            .field("created", Schema.STRING_SCHEMA)
            .field("name", Schema.STRING_SCHEMA)
            .field("description", Schema.STRING_SCHEMA)
            .field("name_en", Schema.STRING_SCHEMA)
            .build();

    public static final Schema MODEL_DATA_SCHEMA = SchemaBuilder.struct()
            .name("model_data")
            .field("model_id", Schema.STRING_SCHEMA)
            .field("name", Schema.STRING_SCHEMA)
            .field("published_date", Schema.STRING_SCHEMA)
            .field("published_revision", Schema.STRING_SCHEMA)
            .field("published_revision_number", Schema.BOOLEAN_SCHEMA)
            .field("model_name", Schema.STRING_SCHEMA)
            .field("comment", Schema.STRING_SCHEMA)
            .field("modeller", Schema.STRING_SCHEMA)
            .field("published_status", Schema.STRING_SCHEMA)
            .field("last_edit", Schema.STRING_SCHEMA)
            .field("parent", Schema.BOOLEAN_SCHEMA)
            .field("folder_name", Schema.STRING_SCHEMA)
            .field("stencil_set", SchemaBuilder.struct()
                    .name("stencil_set")
                    .field("name_space", Schema.STRING_SCHEMA)
                    .field("url", Schema.STRING_SCHEMA)
                    .build())
            .field("process_owner", Schema.STRING_SCHEMA)
            .field("process_owner_email", Schema.STRING_SCHEMA)
            .field("assurance_lead", Schema.STRING_SCHEMA)
            .field("assurance_lead_email", Schema.STRING_SCHEMA)
            .field("e2e_process_owner", Schema.STRING_SCHEMA)
            .field("e2e_process_owner_email", Schema.STRING_SCHEMA)
            .field("process_type", Schema.STRING_SCHEMA)
            .field("pcf_id", Schema.BOOLEAN_SCHEMA)
            .field("critical_operations", Schema.STRING_SCHEMA)
            .field("last_attestation_date", Schema.STRING_SCHEMA)
            .field("critical_operations_categories", Schema.STRING_SCHEMA)
            .field("documentation", Schema.STRING_SCHEMA)
            .field("division", Schema.STRING_SCHEMA)
            .field("division_code", Schema.STRING_SCHEMA)
            .build();
}

