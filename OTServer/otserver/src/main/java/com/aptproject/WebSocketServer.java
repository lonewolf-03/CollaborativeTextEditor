package com.aptproject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;


import com.google.gson.Gson;

@ServerEndpoint(value = "/otserver" )
public class WebSocketServer {


    private Gson seriealizerDeserializer;
    private static Map<String, Long> sessionToUserMapping = new HashMap<>();
    private HttpClient sender;
    private static String uri = "http://localhost:8080";
    private static String adminEmail = "admin@admin.com";
    private static String adminPassword = "ksjdbfiu4e9843hensf++";
    private static Map<Long, Document> documentMapping = new HashMap<>();
    public static Map<Long, List<Session>> documentToNumberOfSessionsMapping = new HashMap<>();

    public WebSocketServer(){
        System.out.println("Server started");
        seriealizerDeserializer = new Gson();
        sender = HttpClient.newHttpClient();
    }

    @OnOpen
    public void onOpen(Session session) throws IOException{
        

        try{
            String[] query = session.getQueryString().split("&");
            Map<String, String> parameters = new HashMap();
            for(String pair: query){
                System.out.println(pair);
                parameters.put(pair.split("=")[0], pair.split("=")[1]);
            }
             
            System.out.println(session.getQueryString());
            if((parameters == null) || (parameters.size() < 3) || (parameters.get("jwt") == null) || (parameters.get("docid") == null) || (parameters.get("uid") == null)){
                session.getBasicRemote().sendText("Missing credentials");
                session.close();
                return;
            }
            String jwt = parameters.get("jwt").replace("%20", " ");
            Long docId = Long.parseLong(parameters.get("docid"));
            Long userId = Long.parseLong(parameters.get("uid"));
            System.out.println(jwt + " " + docId + " " + userId);
            System.out.println();

            HttpRequest req = HttpRequest.newBuilder(new URI(uri+"/admin/check_login"))
               .method("GET", BodyPublishers.ofString(jwt)).header("Authorization", adminEmail+","+adminPassword).build();
            
            HttpResponse<String> res = sender.send(req, BodyHandlers.ofString());

            Boolean isLoggedIn = Boolean.parseBoolean(res.body());

            if(!isLoggedIn){
                session.getBasicRemote().sendText("Not authenticated");
                session.close();
                return;
            }

            Document doc = documentMapping.get(docId);
            DocumentSendBody document = new DocumentSendBody();
            if(doc == null){
                req = HttpRequest.newBuilder(new URI(uri + "/admin/document/get/" + docId)).method("GET", BodyPublishers.noBody())
                .header("Authorization", adminEmail+","+adminPassword).build();
                res = sender.send(req, BodyHandlers.ofString());
                if(res.statusCode() != 200){
                    session.getBasicRemote().sendText("No such document");
                    session.close();
                    return;
                }
                DocumentBody docRes = seriealizerDeserializer.fromJson(res.body(),  DocumentBody.class); 

                doc = new Document();
                doc.setContent(docRes.getContent());
                doc.setDocId(docRes.getId());
                doc.setOwnerId(docRes.getOwner_id());
                doc.setName(docRes.getName());
                documentMapping.put(docId, doc);

                document.setContent(docRes.getContent());
                document.setId(docRes.getId());
                document.setName(docRes.getName());
                document.setVersion(0L); // becuase it is retreived from the database and was not in memory yet
            }else{
                document.setContent(doc.getContent());
                document.setId(doc.getDocId());
                document.setName(doc.getName());
                document.setVersion(userId);
            }

            
            Map<String, Long> body = new HashMap<String,Long>();
            body.put("userId", userId);
            body.put("docId", docId); 

            if(userId != doc.getOwnerId()){
                req = HttpRequest.newBuilder(new URI(uri + "/admin/get_permissions")).method("GET", BodyPublishers.ofString(seriealizerDeserializer
                                 .toJson(body))).header("Authorization", adminEmail+","+adminPassword).build();
                res = sender.send(req, BodyHandlers.ofString());
                if(res.statusCode() != 200){
                    session.getBasicRemote().sendText("Not authorized");
                    session.close();
                    return;
                }
            }

            
            String permission = res.body();
            if(permission.equals("R")){
                document.setReadOnly(true);
            }else{
                document.setReadOnly(false);
            }
            doc.insertPermission(userId, permission);
            sessionToUserMapping.put(session.getId(), userId);
            if(documentToNumberOfSessionsMapping.get(docId) == null){
                documentToNumberOfSessionsMapping.put(docId, new ArrayList<Session>());
            }
            documentToNumberOfSessionsMapping.get(docId).add(session);
            System.out.println("Thread # " + Thread.currentThread().threadId() + ": started session " + session.getId() + " with user " + userId + " for document " + docId);

            session.getBasicRemote().sendText(seriealizerDeserializer.toJson(document));

        }catch(Exception e){
            session.getBasicRemote().sendText("Internal server error");
            session.close();
            e.printStackTrace();
        }

    }

    @OnMessage
    public void onMessage(String message, Session session){
        Operation op = seriealizerDeserializer.fromJson(message, Operation.class);
        Document document = documentMapping.get(op.getDocId());
        Long userId = sessionToUserMapping.get(session.getId());
        System.out.println("Thread # " + Thread.currentThread().threadId() + ": received message from socket with session Id " + session.getId() + " from user " + userId + " for document " + document.getDocId());
        if((document != null) && ( (document.getPermission(userId).equals("RW")) || (document.getOwnerId() == userId) )){
            op.setSessionId(session.getId());
            document.apply(op);
        }else{
            try{
                session.getBasicRemote().sendText("Invalid document or insufficient permissions");
            }catch(Exception e){
                e.printStackTrace();
            }
            return;
        }
        
        if(!op.getAction().equals(Document.INSERT) && !op.getAction().equals(Document.DELETE)){
            try{
                session.getBasicRemote().sendText("Invalid operation");
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    @OnClose
    public void onClose(Session session){
        Document doc;
        Long userId = sessionToUserMapping.get(session.getId());
        try{
        for(Map.Entry<Long, List<Session>> pair : documentToNumberOfSessionsMapping.entrySet()){
            doc = documentMapping.get(pair.getKey());
            if((pair.getValue().size() == 1) && pair.getValue().get(0).getId().equals(session.getId())){ // the last session asscoiated with this document
                System.out.println("last user with this document");
                UpdateDocumentBody body = new UpdateDocumentBody();
                body.setNewContent(doc.getContent());
                body.setName(doc.getName());
                HttpRequest req = HttpRequest.newBuilder(new URI(uri + "/admin/document/push/" + doc.getDocId()))
                          .header("Authorization", adminEmail+","+adminPassword)
                          .method("PUT", BodyPublishers.ofString(seriealizerDeserializer.toJson(body))).build();
                HttpResponse<String> res =  sender.send(req, BodyHandlers.ofString());
                if(res.statusCode() == 200){
                    System.out.println("Document pushed to the database");
                }else{
                    System.out.println(res.statusCode());
                }
                if(res.statusCode() == 200){
                    System.out.println("Closed all sessions associated with " + doc.getDocId());
                }
            }else if(pair.getValue().get(0).getId().equals(session.getId())){
                documentToNumberOfSessionsMapping.get(pair.getKey()).remove(session);
            }

        }
        }catch(Exception e){
            e.printStackTrace();
        }
        
        System.out.println("Thread # " + Thread.currentThread().threadId() + ": closing session " + session.getId() + " with user " + userId); 
        sessionToUserMapping.remove(session.getId());
    }

    @OnError
    public void onError(Throwable t){
        t.printStackTrace();
    }


}
