package com.aptproject;

public class DocumentSendBody {
    private Long id;
    private String name;
    private String content;
    private Long version;
    private Boolean readOnly;

    public Boolean getReadOnly() {
        return readOnly;
    }

    public void setReadOnly(Boolean readOnly) {
        this.readOnly = readOnly;
    }
    
    public Long getVersion() {
        return version;
    }

    public String getContent() {
        return content;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
