package apt.project.backend.controllers;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import apt.project.backend.models.User;
import apt.project.backend.services.ConfirmationBody;
import apt.project.backend.services.ConfirmationToken;
import apt.project.backend.services.ConfirmationTokenService;
import apt.project.backend.services.EmailService;
import apt.project.backend.services.UserRepository;
import apt.project.backend.services.UserService;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController
public class AuthController {

    private Base64.Decoder decoder = Base64.getUrlDecoder();

    @Autowired
    private JWTProvider jwtProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    
    private static Integer minPasswdLength = 8;

    private static Integer maxPasswdLength = 25;



    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private Gson gson = new Gson();
    
    
    @GetMapping("/")
    public String hello() {
        return "hello";
    }
    

    @PostMapping("/login")
    @CrossOrigin(origins = "*")
    public ResponseEntity<String> login(@RequestBody String body) {
        try{
            LoginBody entity = gson.fromJson(body, LoginBody.class);
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                  entity.getEmail(), entity.getPassword()  
            ));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtProvider.generateWebToken(authentication);
            Map<String, Object> resBody  = new HashMap<>();
            resBody.put("token", "Bearer " + token);
            resBody.put("email", entity.getEmail());
            User usr = userRepository.findByEmail(entity.getEmail()).get();
            resBody.put("uid", userRepository.getIdFromEmail(usr.getEmail()).get());
            resBody.put("firstName", usr.getFirstName());
            resBody.put("lastName", usr.getLastName()); 
            Map<String, Object> finalResBody  = new HashMap<>();
            finalResBody.put("message", "logged in successfully");
            finalResBody.put("body", resBody);

            ResponseEntity<String> res = new ResponseEntity<>(gson.toJson(finalResBody), HttpStatus.OK); 
            return res;
        }catch(Exception e){
            Map<String, Object> resBody  = new HashMap<>();
            resBody.put("message", "Invalid email or password");
            return ResponseEntity.badRequest().body(gson.toJson(resBody));
        }
    }


    @PostMapping("/register")
    @CrossOrigin(origins = "*")
    public ResponseEntity<String> register(@RequestBody String entity) {
        RegistrationBody body = gson.fromJson(entity, RegistrationBody.class);
        if(userService.userExists(body.getEmail())){
            Map<String, Object> resBody  = new HashMap<>();
            resBody.put("message", "Email already taken");
            return ResponseEntity.badRequest().body(gson.toJson(resBody));
        }

        try{
            InternetAddress internetAddress = new InternetAddress(body.getEmail());
            internetAddress.validate();
        }catch(AddressException e){
            Map<String, Object> resBody  = new HashMap<>();
            resBody.put("message", "Invalid email");
            return ResponseEntity.badRequest().body(gson.toJson(resBody));
        }

        
        if((body.getPassword().length() > maxPasswdLength) || (body.getPassword().length() < minPasswdLength)){
            Map<String, Object> resBody  = new HashMap<>();
            resBody.put("message", "Password length must be between " + minPasswdLength + " and " + maxPasswdLength);
            return ResponseEntity.badRequest().body(gson.toJson(resBody));
        }

        if(!body.getPassword().equals(body.getConfirmedPassword())){
            Map<String, Object> resBody  = new HashMap<>();
            resBody.put("message", "Passwords not matching");
            return ResponseEntity.badRequest().body(gson.toJson(resBody));
        }

        String token = ConfirmationTokenService.generateToken(body);
        
        try{
            emailService.send(body.getEmail(), token, "Confirmation token");
        }catch(Exception e){
            Map<String, Object> resBody  = new HashMap<>();
            resBody.put("message", "Registration failed. Try again later");
            return ResponseEntity.internalServerError().body(gson.toJson(resBody));
        }

        

        Map<String, Object> resBody  = new HashMap<>();
        resBody.put("message", "Confirmation token has been sent to " + body.getEmail());
        return ResponseEntity.ok().body(gson.toJson(resBody));
    }

    @PostMapping("/register/confirm")
    @CrossOrigin(origins = "*")
    public ResponseEntity<String> confirm(@RequestBody String entity) {
        ConfirmationBody body = gson.fromJson(entity, ConfirmationBody.class);
        System.out.println(body.getEmail());
        System.out.println(body.getToken());
        ConfirmationToken token = ConfirmationTokenService.getTokenAssociatedWith(body.getEmail());
        if(!(token.getToken().equals(body.getToken()))){
            Map<String, Object> resBody  = new HashMap<>();
            resBody.put("message", "Invalid token");
            return ResponseEntity.badRequest().body(gson.toJson(resBody));
        }

        if(token.getExpirationTime().isBefore(LocalDateTime.now())){
            ConfirmationTokenService.remove(body.getEmail());
            Map<String, Object> resBody  = new HashMap<>();
            resBody.put("message", "Expired token");
            return ResponseEntity.badRequest().body(gson.toJson(resBody));
        }
        
        RegistrationBody registrationInfo = ConfirmationTokenService.getRegistrationInfoAssociatedWith(body.getEmail());

        String encryptedPassword = passwordEncoder.encode(registrationInfo.getPassword());

        User newUser = new User(registrationInfo.getFirstName(), registrationInfo.getLastName()
                              , registrationInfo.getEmail(), encryptedPassword);

        userService.register(newUser);

        Map<String, Object> resBody  = new HashMap<>();
        resBody.put("message", "User registered successfully");
        return ResponseEntity.ok().body(gson.toJson(resBody));
    }

    @DeleteMapping("/user/delete/{email}")
    @CrossOrigin(origins = "*")
    public ResponseEntity<String> deleteUser(@PathVariable("email") String email, @RequestHeader("Authorization") String authorization){
        try{
            String chuncks[] = authorization.substring(7).split("\\.");
            String payload = new String(decoder.decode(chuncks[1]));
            JsonParser parser = JsonParserFactory.getJsonParser();
            String userEmail = (String) parser.parseMap(payload).get("sub");
            Long userId = userRepository.getIdFromEmail(userEmail).get();
            Optional<User> user = userRepository.findByEmail(email);
            if(user.isPresent() &&  (userId == user.get().getId())){
                userRepository.deleteById(userId);
                user.get().setId(-1);
                Map<String, Object> resBody  = new HashMap<>();
                resBody.put("body", user.get());
                resBody.put("message", "User deleted successfully");
                ResponseEntity<String> res = new ResponseEntity<String>(gson.toJson(gson.toJson(resBody)), HttpStatus.OK); 
                return res;
            }else{
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        }catch(Exception e){
            Map<String, Object> resBody  = new HashMap<>();
            resBody.put("message", "Confirmation failed. Try again later");
            ResponseEntity<String> res = new ResponseEntity<>(gson.toJson(resBody) , HttpStatus.INTERNAL_SERVER_ERROR);
            return res;
        }
    }
 
    
}
