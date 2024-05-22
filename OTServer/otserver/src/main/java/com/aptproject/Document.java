package com.aptproject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.websocket.Session;

import com.google.gson.Gson;



public class Document {
    public static String INSERT = "INSERT";
    public static String DELETE = "DELETE";

    private StringBuilder content = new StringBuilder();
    private String name;
    private Long docId;
    private Long ownerId;
    private List<Operation> executedOperations = new ArrayList<Operation>();

    private Map<Long, String> userToPermissionMapping = new HashMap<>();

    private Gson serializerDeserializer = new Gson();

    private Long currVersion = 0L;

    public String getContent() {
        return content.toString();
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public Long getDocId() {
        return docId;
    }

    public String getName() {
        return name;
    }

    public void setDocId(Long docId) {
        this.docId = docId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public void setContent(String cont) {
        this.content.replace(0, this.content.length(), cont);
    }

    
    public synchronized void apply(Operation op){
        Operation transformedOp = transorm(op);


        if(INSERT.equals(transformedOp.getAction())){
            if(transformedOp.getPos() >= content.length()){
                content.append(transformedOp.getCharacter());
            }else{
                content.insert(transformedOp.getPos(), transformedOp.getCharacter());
            }
        }else{
            content.deleteCharAt(transformedOp.getPos());
        }

        currVersion++;
        replicateChange(transformedOp);
    }

    public Operation transorm(Operation op){
        System.out.println(op == null);
        Operation transformedOp = op.clone();
        for(Operation pastOp : executedOperations){
            if(pastOp != null && (pastOp.getVersionBeforeUpdate() >= op.getVersionBeforeUpdate())){ // if version of past operation is older than version of current operation, then the current operation is in its place. Just return it
                if(pastOp != null && !(pastOp.getSessionId().equals(op.getSessionId()))){  // the case that past operation version is newer than current operation version for the same client is not possible
                    if(pastOp.getPos() < op.getPos()){ // in this case , the current operation must take into account the past operation
                        if(INSERT.equals(pastOp.getAction())){
                            transformedOp.setPos(transformedOp.getPos() + 1);
                        }else if (DELETE.equals(transformedOp.getAction())){ 
                            transformedOp.setPos(transformedOp.getPos() - 1);
                        }
                    }
                }
            }
        }

        return transformedOp;
    }

    public void replicateChange(Operation op){
        op.setVersionAfterUpdate(currVersion);
        executedOperations.add(op);

        for(Session session : WebSocketServer.documentToNumberOfSessionsMapping.get(this.docId)){
            if(session != null){
                if(session.getId().equals(op.getSessionId())){ // acknowledge the operation for that user
                    Operation copy = op.clone();
                    copy.setIsAcknowledgement(true);
                    try{
                        session.getBasicRemote().sendText(serializerDeserializer.toJson(copy));
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }else{
                    try{
                        session.getBasicRemote().sendText(serializerDeserializer.toJson(op));
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void insertPermission(Long uid, String permission){
        userToPermissionMapping.put(uid, permission);
    }

    public String getPermission(Long uid){
        return userToPermissionMapping.get(uid);
    }

    // public Map<String, Object> getContentWithVersion(){
    //     Map<String, Object> contentBody = new HashMap<>();
	// 	contentBody.put("content", content.toString());
	// 	contentBody.put("version", currVersion);
    //     contentBody.put("id", docId);

	// 	return contentBody;        
    // }
}
