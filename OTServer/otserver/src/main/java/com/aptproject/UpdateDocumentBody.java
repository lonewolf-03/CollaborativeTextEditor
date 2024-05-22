package com.aptproject;

public class UpdateDocumentBody {
    private String name;
    private String newContent;

    public String getName() {
        return name;
    }

    public String getNewContent() {
        return newContent;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public void setNewContent(String newContent) {
        this.newContent = newContent;
    }
}
