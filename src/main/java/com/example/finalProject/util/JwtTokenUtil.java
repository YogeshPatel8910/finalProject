package com.example.finalProject.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenUtil {

    public String generateToken(String username,String role){
        Map<String, Object> claim = new HashMap<>();
        claim.put("role",role); // Add roles as a custom claim

        return Jwts.builder()
                .subject(username)
                .claims(claim)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis()+(30*60*1000)))
                .signWith(getSigningKey())
                .compact();

    }
//    public Claims getAllClamis(String token){
//        return  Jwts.parser().verifyWith(getSigningKey())
//                .build().parseSignedClaims(token)
//                .getPayload();
//    }

    SecretKey getSigningKey(){
//        byte[] keyBytes = Base64.getDecoder().decode(secret);
        String secret = "8sbStaifF33osCGE3KwAM953bSDegeWt";
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);

        return Keys.hmacShaKeyFor(keyBytes);
    }
    public String extractUsername(String token){
        return Jwts.parser().verifyWith(getSigningKey())
                .build().parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
    public boolean validateToken(String token){
        try{
            Jwts.parser().verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        }
        catch (Exception e){
            return false;
        }
    }

    public String extractRoles(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build().parseSignedClaims(token)
                .getPayload();
        return claims.get("role", String.class);
    }
}
