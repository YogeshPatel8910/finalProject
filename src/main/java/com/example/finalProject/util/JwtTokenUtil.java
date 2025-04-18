package com.example.finalProject.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for JWT operations including token generation, validation, and claim extraction.
 * Used for authentication and authorization in the application.
 */
@Component
public class JwtTokenUtil {

    @Value("${secret.key}")
    private String secretKey;

    /**
     * Generates a JWT token for a user with specified role
     * @param username The username to be included in the token subject
     * @param role The role to be included as a claim
     * @return Generated JWT token string
     */
    public String generateToken(String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role); // Store user role as a claim

        return Jwts.builder()
                .subject(username)
                .claims(claims)
                .issuedAt(new Date(System.currentTimeMillis()))
                // Token valid for 12 hours
                .expiration(new Date(System.currentTimeMillis() + (12 * 60 * 60 * 1000)))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Creates the signing key used to generate and validate tokens
     * @return SecretKey for JWT operations
     */
    SecretKey getSigningKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Extracts the username from a token
     * @param token JWT token string
     * @return Username stored in the token
     */
    public String extractUsername(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    /**
     * Validates if a token is properly signed and not expired
     * @param token JWT token string
     * @return true if valid, false otherwise
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Extracts the role information from a token
     * @param token JWT token string
     * @return Role string stored in the token
     */
    public String extractRoles(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.get("role", String.class);
    }
}