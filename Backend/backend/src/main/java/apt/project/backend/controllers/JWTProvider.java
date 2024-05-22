package apt.project.backend.controllers;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Jwts.SIG;
import io.jsonwebtoken.security.Keys;

@Component
public class JWTProvider {
 
    //@Value("${app.JWT_EXPIRE_TIME}")
    private static long JWT_EXPIRE_TIME = 7000000;

    //@Value("${app.JWT_SECRET}")
    private static String JWT_SECRET = "R0zRzVk7iU0S78uCbNTa6p241kyBKL3RdYZcalU1w9A=";

    public String generateWebToken(Authentication authentication){
        String email = authentication.getName(); // this method will use UserDetails object , so it will return the email in this situation
        Date currTime = new Date();
        Date expirTime = new Date(currTime.getTime() + JWT_EXPIRE_TIME); 
        
        String token = Jwts.builder().subject(email)
                       .issuedAt(currTime).expiration(expirTime)
                       .signWith(Keys.hmacShaKeyFor(JWT_SECRET.getBytes()), SIG.HS256).compact();
        return token;
    }

    public String getEmailFromJWT(String token){
        SecretKey key = Keys.hmacShaKeyFor(JWT_SECRET.getBytes());
        Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
        return claims.getSubject();
    }

    public Boolean verifyJWToken(String token){
        try{
            SecretKey key = Keys.hmacShaKeyFor(JWT_SECRET.getBytes());
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            System.out.println("Leaving verifyToken");
            return true;
        }catch (Exception e){
            return false;
        }
    }
}
