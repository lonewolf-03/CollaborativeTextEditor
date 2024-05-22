package apt.project.backend.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import apt.project.backend.models.ControlAccessList;
import apt.project.backend.models.ControlAccessListKey;
import apt.project.backend.models.Document;
import apt.project.backend.models.User;
import apt.project.backend.services.ControlAccessListRepository;
import apt.project.backend.services.DocumentRepository;
import apt.project.backend.services.UserRepository;

import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.query.SemanticException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;



@RestController
public class DocumentController {
    private Base64.Decoder decoder = Base64.getUrlDecoder();
    
    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ControlAccessListRepository controlAccessListRepository;

    private Gson gson = new Gson();

    @GetMapping("/document/{id}")
    @CrossOrigin(origins = "*")
    public ResponseEntity<String> getDocument(@PathVariable("id") Long id, @RequestHeader("Authorization") String authorization) {
        try{
            String chuncks[] = authorization.substring(7).split("\\.");
            String payload = new String(decoder.decode(chuncks[1]));
            JsonParser parser = JsonParserFactory.getJsonParser();
            String email = (String) parser.parseMap(payload).get("sub");
            Optional<Document> doc = documentRepository.findById(id);
            if(doc.isPresent()){
                Optional<Long> userId = userRepository.getIdFromEmail(email);
                ControlAccessListKey key = new ControlAccessListKey(userId.get(), id);
                Optional<ControlAccessList> permissions = controlAccessListRepository.findById(key);
                if(userId.isPresent() && 
                  ((doc.get().getOwner_id() == userId.get()))){
                    Map<String, Object> resBody = new HashMap<>();
                    resBody.put("body", doc.get());
                    return new ResponseEntity<String>(gson.toJson(resBody), HttpStatus.OK);
                }else if(permissions.isPresent() && (permissions.get().getPermissions().equals("RW") || permissions.get().getPermissions().equals("R"))){
                    doc.get().setOwner_id(-1);
                    Map<String, Object> resBody = new HashMap<>();
                    resBody.put("body", doc.get());
                    return new ResponseEntity<String>(gson.toJson(resBody), HttpStatus.OK);
                }else{
                    return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
                }    
            }else{
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/document/update/{id}")
    @CrossOrigin(origins = "*")
    public ResponseEntity<String> updateDocument(@PathVariable("id") Long id, @RequestBody String entity, @RequestHeader("Authorization") String authorization) {
        UpdateDocBody body = gson.fromJson(entity, UpdateDocBody.class);
        try{
            if(body.getNewContent().equals(null)){
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            
            String chuncks[] = authorization.substring(7).split("\\.");
            String payload = new String(decoder.decode(chuncks[1]));
            JsonParser parser = JsonParserFactory.getJsonParser();
            String email = (String) parser.parseMap(payload).get("sub");
            Optional<Document> doc = documentRepository.findById(id);
            if(doc.isPresent()){
                System.out.println(doc.get().getId());
                Document document = doc.get();
                Optional<Long> userId = userRepository.getIdFromEmail(email);
                ControlAccessListKey key = new ControlAccessListKey(userId.get(), id);
                Optional<ControlAccessList> permissions = controlAccessListRepository.findById(key);
                if((document.getOwner_id() == userId.get()) || (permissions.isPresent() && permissions.get().getPermissions().equals("RW"))){
                    document.setContent(body.getNewContent());
                    documentRepository.updateDocumentContent(document.getId(), body.getNewContent());
                    if(!(body.getName() == null)){
                        document.setName(body.getName());
                    }
                    documentRepository.updateDocumentContent(document.getId(), body.getNewContent());
                    Map<String, Object> resBody = new HashMap<>();
                    resBody.put("body", document);
                    return new ResponseEntity<String>(gson.toJson(resBody), HttpStatus.OK);
                }else{
                    return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
                }
            }else{
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }catch(Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/document/owned")
    @CrossOrigin(origins = "*")
    public ResponseEntity<String> getOwnedDocuments(@RequestHeader("Authorization") String authorization) {
        try{
            String chuncks[] = authorization.substring(7).split("\\.");
            String payload = new String(decoder.decode(chuncks[1]));
            JsonParser parser = JsonParserFactory.getJsonParser();
            String email = (String) parser.parseMap(payload).get("sub");
            Map<String, Object> resBody = new HashMap<>();
            resBody.put("body", documentRepository.getAllDocumentsOfEmail(email).get());
            return new ResponseEntity<String>(gson.toJson(resBody), HttpStatus.OK);    
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PostMapping("/document/add")
    @CrossOrigin(origins = "*")
    public ResponseEntity<String> addDocument(@RequestBody String entity, @RequestHeader("Authorization") String authorization) {
        AddDocumentBody body = gson.fromJson(entity, AddDocumentBody.class);
        try{
            String chuncks[] = authorization.substring(7).split("\\.");
            String payload = new String(decoder.decode(chuncks[1]));
            JsonParser parser = JsonParserFactory.getJsonParser();
            String email = (String) parser.parseMap(payload).get("sub");
            Long Id = userRepository.getIdFromEmail(email).get();
            Document newDocument = new Document(Id , body.getName(), body.getContent());
            Map<String, Object> resBody = new HashMap<>();
            resBody.put("body", documentRepository.save(newDocument));
            return new ResponseEntity<String>(gson.toJson(resBody), HttpStatus.OK);
        }catch(SemanticException e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/document/delete/{did}")
    @CrossOrigin(origins = "*")
    public ResponseEntity<String> deleteDocument(@PathVariable("did") Long docId, @RequestHeader("Authorization") String authorization) {
        try{
            System.out.println(docId);
            Optional<Document> document = documentRepository.findById(docId);
            String chuncks[] = authorization.substring(7).split("\\.");
            String payload = new String(decoder.decode(chuncks[1]));
            JsonParser parser = JsonParserFactory.getJsonParser();
            String email = (String) parser.parseMap(payload).get("sub");
            Optional<Long> Id = userRepository.getIdFromEmail(email);
            if(document.isPresent() && Id.isPresent()){
                if(Id.get() != document.get().getOwner_id()){
                    return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
                }
                Document doc = document.get();
                documentRepository.deleteByIdAndCascade(docId);
                doc.setId(-1);
                Map<String, Object> resBody = new HashMap<>();
                resBody.put("body", doc);
                return new ResponseEntity<String>(gson.toJson(resBody), HttpStatus.OK);
            }else{
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/document/shared/{did}")
    @CrossOrigin(origins = "*")
    public ResponseEntity<String> getDocumentPermissions(@PathVariable("did") Long did, @RequestHeader("Authorization") String authorization) {
        try{
            String chuncks[] = authorization.substring(7).split("\\.");
            String payload = new String(decoder.decode(chuncks[1]));
            JsonParser parser = JsonParserFactory.getJsonParser();
            String email = (String) parser.parseMap(payload).get("sub");
            Long Id = userRepository.getIdFromEmail(email).get();
            Optional<Long> Did = documentRepository.getOwnerId(did); 
            if((Did.isPresent()) && (Id == Did.get())){
                Map<String, Object> resBody = new HashMap<>();
                resBody.put("body", controlAccessListRepository.getPermissionsOfDocument(did));
                return new ResponseEntity<String>(gson.toJson(resBody), HttpStatus.OK);
            }else{
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/document/shared/user")
    @CrossOrigin(origins = "*")
    public ResponseEntity<String> getSharedPermissions(@RequestHeader("Authorization") String authorization) {
        try{
            String chuncks[] = authorization.substring(7).split("\\.");
            String payload = new String(decoder.decode(chuncks[1]));
            JsonParser parser = JsonParserFactory.getJsonParser();
            String email = (String) parser.parseMap(payload).get("sub");
            Optional<User> user = userRepository.findByEmail(email);
            if(user.isPresent()){
                Map<String, Object> resBody = new HashMap<>();
                resBody.put("body", controlAccessListRepository.getSharedPermissionsFromUID(user.get().getId()));
                return new ResponseEntity<String>(gson.toJson(resBody), HttpStatus.OK);
            }else{
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PostMapping("/document/shared/add")
    @CrossOrigin(origins = "*")
    public ResponseEntity<String> addUserToControlAccessList(@RequestBody String entity, @RequestHeader("Authorization") String authorization) {
        SharedUserAddReqBody body = gson.fromJson(entity, SharedUserAddReqBody.class);
        try{
            Optional<User> user = userRepository.findByEmail(body.getEmail());
            String chuncks[] = authorization.substring(7).split("\\.");
            String payload = new String(decoder.decode(chuncks[1]));
            JsonParser parser = JsonParserFactory.getJsonParser();
            String email = (String) parser.parseMap(payload).get("sub");
            Long Id = userRepository.getIdFromEmail(email).get();
            System.out.println(Id);
            Optional<Long> docOwnerId = documentRepository.getOwnerId(body.getDocId());
            if(docOwnerId.isPresent() && (Id == docOwnerId.get())){
                System.out.println(docOwnerId.get());
                if(user.isPresent()){
                    ControlAccessList cal = new ControlAccessList();
                    cal.setId(new ControlAccessListKey(user.get().getId(), body.getDocId()));  
                    cal.setDocName(body.getDocName());
                    String permString = body.getPermissions();
                    if(permString.equals("RW")){ 
                        cal.setPermissions("RW");
                    }else if(permString.equals("R")){
                        cal.setPermissions("R");
                    }else{
                        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                    }
                    try{
                        controlAccessListRepository.save(cal);
                    }catch(Exception e){
                        e.printStackTrace();
                        return new ResponseEntity<>("Invalid permissions or document id", HttpStatus.BAD_REQUEST);
                    }
                    Map<String, Object> resBody = new HashMap<>();
                    resBody.put("message", "User added successfully");
                    return ResponseEntity.ok().body(gson.toJson(resBody));
                }else{
                    Map<String, Object> resBody = new HashMap<>();
                    resBody.put("message", "No such user");
                    return ResponseEntity.badRequest().body(gson.toJson(resBody));
                }
            }else{
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        }catch(Exception e){
            e.printStackTrace();
            Map<String, Object> resBody = new HashMap<>();
            resBody.put("message", "Operation failed. Try again later");
            return ResponseEntity.internalServerError().body(gson.toJson(resBody));
        }
    }
    
    @DeleteMapping("/document/shared/remove")
    @CrossOrigin(origins = "*")
    public ResponseEntity<String> deleteUserFromControlAccessList(@RequestBody SharedUserRemoveReqBody body, @RequestHeader("Authorization") String authorization) {
        try{
            Optional<User> user = userRepository.findByEmail(body.getEmail());
            String chuncks[] = authorization.substring(7).split("\\.");
            String payload = new String(decoder.decode(chuncks[1]));
            JsonParser parser = JsonParserFactory.getJsonParser();
            String email = (String) parser.parseMap(payload).get("sub");
            Long Id = userRepository.getIdFromEmail(email).get();
            Optional<Long> docOwnerId = documentRepository.getOwnerId(body.getDocId());
            if(docOwnerId.isPresent() && (Id == docOwnerId.get())){
                if(user.isPresent()){
                    ControlAccessListKey key = new ControlAccessListKey(user.get().getId(), body.getDocId());
                    try{
                        controlAccessListRepository.deleteById(key);
                        
                        Map<String, Object> resBody = new HashMap<>();
                        resBody.put("message", "User removed successfully from the list of shared users");
                        return ResponseEntity.ok().body(gson.toJson(resBody));
                    }catch(Exception e){
                        Map<String, Object> resBody = new HashMap<>();
                        resBody.put("message", "Document nor found");
                        return ResponseEntity.badRequest().body(gson.toJson(resBody));
                    }
                }else{
                    Map<String, Object> resBody = new HashMap<>();
                    resBody.put("message", "No such user");
                    return ResponseEntity.badRequest().body(gson.toJson(resBody));
                }
            }else{
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            } 
        }catch(Exception e){
            Map<String, Object> resBody = new HashMap<>();
            resBody.put("message", "Operation failed. Try again later.");
            return ResponseEntity.internalServerError().body(gson.toJson(resBody));
        }
    }

    @PutMapping("/document/shared/update")
    @CrossOrigin(origins = "*")
    public ResponseEntity<String> updateUserPermission(@RequestBody String entity, @RequestHeader("Authorization") String authorization){
        UpdatePermissionReqBody body = gson.fromJson(entity, UpdatePermissionReqBody.class);
        try{
            String chuncks[] = authorization.substring(7).split("\\.");
            String payload = new String(decoder.decode(chuncks[1]));
            JsonParser parser = JsonParserFactory.getJsonParser();
            String email = (String) parser.parseMap(payload).get("sub");
            Optional<Document> doc = documentRepository.findById(body.getDocId()); 
            Long ownerId = userRepository.getIdFromEmail(email).get();
            System.out.println("Checking");
            if(doc.isPresent() && (ownerId == doc.get().getOwner_id())){
                System.out.println("Owner verified");
                Optional<Long> userId = userRepository.getIdFromEmail(body.getEmail());
                if(userId.isPresent()){
                    System.out.println("User found");
                    String permString = body.getNewPermission();
                    if(permString.equals("RW")){ 
                        controlAccessListRepository.updateUserPermission(body.getDocId(), userId.get(), "RW");
                    }else if(permString.equals("R")){
                        controlAccessListRepository.updateUserPermission(body.getDocId(), userId.get(), "R");
                    }else{
                        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                    }
                }else{
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
                Map<String, Object> resBody = new HashMap<>();
                resBody.put("message", "Permission update successfully");
                return ResponseEntity.ok().body(gson.toJson(resBody));
            }else{
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        }catch(Exception e){
            Map<String, Object> resBody = new HashMap<>();
            resBody.put("message", "Operation failed. Try again later.");
            return ResponseEntity.internalServerError().body(gson.toJson(resBody));
        }
    }

}
