package com.aptproject;

public class DocumentBody {
    private Long id;
    private String name;
    private String content;
    private Long owner_id;

    public String getContent() {
        return content;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Long getOwner_id() {
        return owner_id;
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
    public void setOwner_id(Long owner_id) {
        this.owner_id = owner_id;
    }
}
