package io.confluent.connect.http.model;

public class Directory {
    private String rel;
    private String directory_id;
    private String parent;
    private String allowedMimeTypeRegex;
    private String parentName;
    private Boolean deleted;
    private Boolean visible;
    private String created;
    private String name;
    private String description;
    private String nameEn;

    public Directory(String rel, String directory_id, String parent, String allowedMimeTypeRegex, String parentName, Boolean deleted, Boolean visible, String created, String name, String description, String nameEn) {
        this.rel = rel;
        this.directory_id = directory_id;
        this.parent = parent;
        this.allowedMimeTypeRegex = allowedMimeTypeRegex;
        this.parentName = parentName;
        this.deleted = deleted;
        this.visible = visible;
        this.created = created;
        this.name = name;
        this.description = description;
        this.nameEn = nameEn;
    }

    public String getRel() {
        return rel;
    }

    public void setRel(String rel) {
        this.rel = rel;
    }

    public String getDirectory_id() {
        return directory_id;
    }

    public void setDirectory_id(String directory_id) {
        this.directory_id = directory_id;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getAllowedMimeTypeRegex() {
        return allowedMimeTypeRegex;
    }

    public void setAllowedMimeTypeRegex(String allowedMimeTypeRegex) {
        this.allowedMimeTypeRegex = allowedMimeTypeRegex;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    @Override
    public String toString() {
        return "{" +
                "\"rel\": \"" + rel + "\"," +
                "\"href\": \"" + directory_id + "\"," +
                "\"parent\": \"" + parent + "\"," +
                "\"allowedMimeTypeRegex\": \"" + allowedMimeTypeRegex + "\"," +
                "\"parentName\": \"" + parentName + "\"," +
                "\"deleted\": " + deleted + "," +
                "\"visible\": " + visible + "," +
                "\"created\": \"" + created + "\"," +
                "\"name\": \"" + name + "\"," +
                "\"description\": \"" + description + "\"," +
                "\"nameEn\": \"" + nameEn + "\"" +
                "}";
    }


}
