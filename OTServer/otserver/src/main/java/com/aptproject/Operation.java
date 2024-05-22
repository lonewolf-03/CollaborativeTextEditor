package com.aptproject;


public class Operation {
    private String sessionId;
    private String character; // the character that was inserted or deleted
    private Long versionBeforeUpdate; // this is the version sent by the client. It's the version of use here
    private Long versionAfterUpdate; // this is the version that will be broadcasted to all other clients. It's sent by the server only
    private String Action; // "DELETE" or "UPDATE"
    private Integer pos; // where the operation took place in the version received from the client. It will also be updated and sent to the client again.
    private Boolean isAcknowledgement; // is set to true only for the client that sent this operation
    private Long timestamp; // used to identify the operations at the client side
    private Long docId;

    @Override
    protected Operation clone(){
        Operation copy = new Operation();
        copy.setSessionId(this.sessionId);
        copy.setCharacter(this.character);
        copy.setVersionBeforeUpdate(this.versionBeforeUpdate);
        copy.setVersionAfterUpdate(this.versionAfterUpdate);
        copy.setAction(this.Action);
        copy.setPos(this.pos);
        copy.setIsAcknowledgement(this.isAcknowledgement);
        copy.setTimestamp(this.timestamp);

        return copy;
    }

    public Long getDocId() {
        return docId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getAction() {
        return Action;
    }

    public String getCharacter() {
        return character;
    }

    public Boolean getIsAcknowledgement() {
        return isAcknowledgement;
    }

    public Integer getPos() {
        return pos;
    }
    public Long getTimestamp() {
        return timestamp;
    }

    public Long getVersionAfterUpdate() {
        return versionAfterUpdate;
    }

    public Long getVersionBeforeUpdate() {
        return versionBeforeUpdate;
    }

    public void setAction(String action) {
        Action = action;
    }

    public void setCharacter(String character) {
        this.character = character;
    }

    public void setIsAcknowledgement(Boolean isAcknowledgement) {
        this.isAcknowledgement = isAcknowledgement;
    }
    
    public void setPos(Integer pos) {
        this.pos = pos;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public void setVersionAfterUpdate(Long versionAfterUpdate) {
        this.versionAfterUpdate = versionAfterUpdate;
    }

    public void setVersionBeforeUpdate(Long versionBeforeUpdate) {
        this.versionBeforeUpdate = versionBeforeUpdate;
    }

    public void setDocId(Long docId) {
        this.docId = docId;
    }
}
