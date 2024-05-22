package apt.project.backend.services;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import apt.project.backend.controllers.RegistrationBody;

public class ConfirmationTokenService {
    private static Integer waitTime = 3;

    private static HashMap<String, ConfirmationToken> tokenMapping = new HashMap<String, ConfirmationToken>();
    private static HashMap<String, RegistrationBody> emailMapping = new HashMap<String, RegistrationBody>();


    public static String generateToken(RegistrationBody body) {
        String tokenString = UUID.randomUUID().toString();
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime expTime = startTime.plusMinutes(waitTime);
        ConfirmationToken newToken = new ConfirmationToken(tokenString, startTime, expTime);
        tokenMapping.put(body.getEmail(), newToken);
        emailMapping.put(body.getEmail(), body);
        return tokenString;
    }

    public static ConfirmationToken getTokenAssociatedWith(String email){
        return tokenMapping.get(email);
    }

    public static RegistrationBody getRegistrationInfoAssociatedWith(String email){
        return emailMapping.get(email);
    }

    public static void remove(String email){
        tokenMapping.remove(email);
        emailMapping.remove(email);
    }

    public static void clean(){
        for(Map.Entry<String, ConfirmationToken> entry: tokenMapping.entrySet()){
            if(LocalDateTime.now().isAfter(entry.getValue().getExpirationTime())){
                tokenMapping.remove(entry.getKey());
                emailMapping.remove(entry.getKey());
            }
        }
    }

}

