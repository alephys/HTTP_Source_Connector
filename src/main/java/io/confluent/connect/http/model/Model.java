package io.confluent.connect.http.model;

public class Model {
    public String model_id;
    public String published_date;
    public String published_revision;
    public boolean published_revision_number;
    public String model_name;
    public String comment;
    public String modeller;
    public String published_status;
    public String last_edit;
    public boolean parent;
    public String folder_name;
    public StencilSet stencil_set;
    public String process_owner;
    public String process_owner_email;
    public String assurance_lead;
    public String assurance_lead_email;
    public String e2e_process_owner;
    public String e2e_process_owner_email;
    public String process_type;
    public boolean pcf_id;
    public String critical_operations;
    public String last_attestation_date;
    public String critical_operations_categories;
    public String documentation;
    public String division;

    public static class StencilSet {
        public String name_space;
        public String url;

        public String getName_space() {
            return name_space;
        }

        public void setName_space(String name_space) {
            this.name_space = name_space;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public StencilSet(String name_space, String url) {
            this.name_space = name_space;
            this.url = url;
        }

        @Override
        public String toString() {
            return "{" +
                    "\"name_space\": \"" + name_space + "\"," +
                    "\"url\": \"" + url + "\"" +
                    "}";
        }
    }

    public Model(String model_id, String name, String published_date, String published_revision, boolean published_revision_number, String model_name, String comment, String modeller, String published_status, String last_edit, boolean parent, String folder_name, StencilSet stencil_set, String process_owner, String process_owner_email, String assurance_lead, String assurance_lead_email, String e2e_process_owner, String e2e_process_owner_email, String process_type, boolean pcf_id, String critical_operations, String last_attestation_date, String critical_operations_categories, String documentation, String division) {
        this.model_id = model_id;
        this.published_date = published_date;
        this.published_revision = published_revision;
        this.published_revision_number = published_revision_number;
        this.model_name = model_name;
        this.comment = comment;
        this.modeller = modeller;
        this.published_status = published_status;
        this.last_edit = last_edit;
        this.parent = parent;
        this.folder_name = folder_name;
        this.stencil_set = stencil_set;
        this.published_date = published_date;
        this.process_owner = process_owner;
        this.process_owner_email = process_owner_email;
        this.assurance_lead = assurance_lead;
        this.assurance_lead_email = assurance_lead_email;
        this.e2e_process_owner = e2e_process_owner;
        this.e2e_process_owner_email = e2e_process_owner_email;
        this.process_type = process_type;
        this.pcf_id = pcf_id;
        this.critical_operations = critical_operations;
        this.last_attestation_date = last_attestation_date;
        this.critical_operations_categories = critical_operations_categories;
        this.documentation = documentation;
        this.division = division;
    }

    public String getModel_id() {
        return model_id;
    }

    public void setModel_id(String model_id) {
        this.model_id = model_id;
    }

    public String getPublished_date() {
        return published_date;
    }

    public void setPublished_date(String published_date) {
        this.published_date = published_date;
    }

    public String getPublished_revision() {
        return published_revision;
    }

    public void setPublished_revision(String published_revision) {
        this.published_revision = published_revision;
    }

    public boolean isPublished_revision_number() {
        return published_revision_number;
    }

    public void setPublished_revision_number(boolean published_revision_number) {
        this.published_revision_number = published_revision_number;
    }

    public String getModel_name() {
        return model_name;
    }

    public void setModel_name(String model_name) {
        this.model_name = model_name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getModeller() {
        return modeller;
    }

    public void setModeller(String modeller) {
        this.modeller = modeller;
    }

    public String getPublished_status() {
        return published_status;
    }

    public void setPublished_status(String published_status) {
        this.published_status = published_status;
    }

    public String getLast_edit() {
        return last_edit;
    }

    public void setLast_edit(String last_edit) {
        this.last_edit = last_edit;
    }

    public boolean isParent() {
        return parent;
    }

    public void setParent(boolean parent) {
        this.parent = parent;
    }

    public String getFolder_name() {
        return folder_name;
    }

    public void setFolder_name(String folder_name) {
        this.folder_name = folder_name;
    }

    public StencilSet getStencil_set() {
        return stencil_set;
    }

    public void setStencil_set(StencilSet stencil_set) {
        this.stencil_set = stencil_set;
    }

    public String getProcess_owner() {
        return process_owner;
    }

    public void setProcess_owner(String process_owner) {
        this.process_owner = process_owner;
    }

    public String getProcess_owner_email() {
        return process_owner_email;
    }

    public void setProcess_owner_email(String process_owner_email) {
        this.process_owner_email = process_owner_email;
    }

    public String getAssurance_lead() {
        return assurance_lead;
    }

    public void setAssurance_lead(String assurance_lead) {
        this.assurance_lead = assurance_lead;
    }

    public String getAssurance_lead_email() {
        return assurance_lead_email;
    }

    public void setAssurance_lead_email(String assurance_lead_email) {
        this.assurance_lead_email = assurance_lead_email;
    }

    public String getE2e_process_owner() {
        return e2e_process_owner;
    }

    public void setE2e_process_owner(String e2e_process_owner) {
        this.e2e_process_owner = e2e_process_owner;
    }

    public String getE2e_process_owner_email() {
        return e2e_process_owner_email;
    }

    public void setE2e_process_owner_email(String e2e_process_owner_email) {
        this.e2e_process_owner_email = e2e_process_owner_email;
    }

    public String getProcess_type() {
        return process_type;
    }

    public void setProcess_type(String process_type) {
        this.process_type = process_type;
    }

    public boolean isPcf_id() {
        return pcf_id;
    }

    public void setPcf_id(boolean pcf_id) {
        this.pcf_id = pcf_id;
    }

    public String getCritical_operations() {
        return critical_operations;
    }

    public void setCritical_operations(String critical_operations) {
        this.critical_operations = critical_operations;
    }

    public String getLast_attestation_date() {
        return last_attestation_date;
    }

    public void setLast_attestation_date(String last_attestation_date) {
        this.last_attestation_date = last_attestation_date;
    }

    public String getCritical_operations_categories() {
        return critical_operations_categories;
    }

    public void setCritical_operations_categories(String critical_operations_categories) {
        this.critical_operations_categories = critical_operations_categories;
    }

    public String getDocumentation() {
        return documentation;
    }

    public void setDocumentation(String documentation) {
        this.documentation = documentation;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    @Override
    public String toString() {
        return "{" +
                "\"model_id\": \"" + model_id + "\"," +
                "\"published_date\": \"" + published_date + "\"," +
                "\"published_revision\": \"" + published_revision + "\"," +
                "\"published_revision_number\": " + published_revision_number + "," +
                "\"model_name\": \"" + model_name + "\"," +
                "\"comment\": \"" + comment + "\"," +
                "\"modeller\": \"" + modeller + "\"," +
                "\"published_status\": \"" + published_status + "\"," +
                "\"last_edit\": \"" + last_edit + "\"," +
                "\"parent\": " + parent + "," +
                "\"folder_name\": \"" + folder_name + "\"," +
                "\"stencil_set\": " + (stencil_set != null ? stencil_set.toString() : null) + "," +
                "\"process_owner\": \"" + process_owner + "\"," +
                "\"process_owner_email\": \"" + process_owner_email + "\"," +
                "\"assurance_lead\": \"" + assurance_lead + "\"," +
                "\"assurance_lead_email\": \"" + assurance_lead_email + "\"," +
                "\"e2e_process_owner\": \"" + e2e_process_owner + "\"," +
                "\"e2e_process_owner_email\": \"" + e2e_process_owner_email + "\"," +
                "\"process_type\": \"" + process_type + "\"," +
                "\"pcf_id\": " + pcf_id + "," +
                "\"critical_operations\": \"" + critical_operations + "\"," +
                "\"last_attestation_date\": \"" + last_attestation_date + "\"," +
                "\"critical_operations_categories\": \"" + critical_operations_categories + "\"," +
                "\"documentation\": \"" + documentation + "\"," +
                "\"division\": \"" + division + "\"," +
                "}";
    }
}

