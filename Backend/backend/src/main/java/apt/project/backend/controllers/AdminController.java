package apt.project.backend.controllers;


import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import apt.project.backend.models.ControlAccessList;
import apt.project.backend.models.ControlAccessListKey;
import apt.project.backend.models.Document;
import apt.project.backend.services.ControlAccessListRepository;
import apt.project.backend.services.DocumentRepository;
import com.google.gson.Gson;

@RestController
public class AdminController {
    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private ControlAccessListRepository controlAccessListRepository;

    @Autowired
    private JWTProvider jwtProvider;

    private Gson gson = new Gson();

    @GetMapping("/admin")
    @CrossOrigin(origins = "*")
    public String helloAdmin(){
        return "Hello admin";
    }

    @GetMapping("/admin/check_login")
    @CrossOrigin(origins = "*")
    public ResponseEntity<Boolean> checkLogin(@RequestBody String jwt){
        try{
            if(jwtProvider.verifyJWToken(jwt.substring(7))){
                return new ResponseEntity<Boolean>(true, HttpStatus.OK);
            }else{
                return new ResponseEntity<Boolean>(false, HttpStatus.OK);
            }
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/admin/get_permissions")
    @CrossOrigin(origins = "*")
    public ResponseEntity<String> getPermissions(@RequestBody String entity){
        GetPermissionsBody body = gson.fromJson(entity, GetPermissionsBody.class);
        try{
            Long userId = body.getUserId();
            Long docId = body.getDocId();
            ControlAccessListKey key = new ControlAccessListKey(userId, docId);
            Optional<ControlAccessList> cal = controlAccessListRepository.findById(key);
            if(cal.isPresent()){
                return new ResponseEntity<>(cal.get().getPermissions(),HttpStatus.OK);
            }else{
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            } 
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/admin/document/get/{id}")
    @CrossOrigin(origins = "*")
    public ResponseEntity<String> getPermissions(@PathVariable("id") Long id){
        try{
            Optional<Document> doc = documentRepository.findById(id);
            if(doc.isPresent()){
                return new ResponseEntity<>(gson.toJson(doc.get()), HttpStatus.OK);
            }else{
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/admin/document/push/{id}")
    @CrossOrigin(origins = "*")
    public ResponseEntity<String> pushDocument(@RequestBody String entity, @PathVariable("id") Long id){
        UpdateDocBody body = gson.fromJson(entity, UpdateDocBody.class);
        try{
            String newContent = body.getNewContent();
            Optional<Document> doc = documentRepository.findById(id);
            if(doc.isPresent()){
                Document document = doc.get();
                document.setContent(body.getNewContent());
                documentRepository.updateDocumentContent(id, newContent);
                if(!(body.getName() == null)){
                    document.setName(body.getName());
                    documentRepository.updateDocumentName(id, body.getName());
                }
                return new ResponseEntity<String>(gson.toJson(document), HttpStatus.OK);
            }else{
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
