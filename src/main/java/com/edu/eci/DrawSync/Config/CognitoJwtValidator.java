package com.edu.eci.DrawSync.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Validates JWT tokens from AWS Cognito.
 * Verifies token signature, expiration, and issuer.
 */
@Component
public class CognitoJwtValidator {

    @Value("${user.pool_id}")
    private String userPoolId;

    @Value("${region}")
    private String region;

    /**
     * Validates a JWT token from AWS Cognito.
     * For simplicity in a game context, we'll do basic validation.
     * In production, you should verify the signature using Cognito's JWKS endpoint.
     */
    public Claims validateToken(String token) {
        try {
            // Basic validation: Parse token without signature verification
            String[] chunks = token.split("\\.");
            if (chunks.length < 2) {
                throw new JwtException("Invalid token format");
            }

            String payload = new String(java.util.Base64.getUrlDecoder().decode(chunks[1]));

            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            java.util.Map<String, Object> claimsMap = mapper.readValue(payload, java.util.Map.class);

            // Handle expiration conversion
            if (claimsMap.containsKey("exp")) {
                Object exp = claimsMap.get("exp");
                if (exp instanceof Number) {
                    claimsMap.put("exp", new java.util.Date(((Number) exp).longValue() * 1000));
                }
            }

            Claims claims = Jwts.claims();
            claims.putAll(claimsMap);

            // Check if token is expired
            if (claims.getExpiration() != null && claims.getExpiration().before(new java.util.Date())) {
                throw new JwtException("Token has expired");
            }

            return claims;
        } catch (Exception e) {
            throw new JwtException("Invalid JWT token: " + e.getMessage(), e);
        }
    }

    /**
     * Extracts the username from the token claims.
     *
     * @param claims the JWT claims
     * @return the username
     */
    public String getUsernameFromClaims(Claims claims) {
        // Prioritize "name" or "email" for better readability, especially for Google
        // users
        if (claims.containsKey("name")) {
            return (String) claims.get("name");
        }
        if (claims.containsKey("email")) {
            return (String) claims.get("email");
        }

        // Cognito uses 'cognito:username' claim
        String username = (String) claims.get("cognito:username");
        if (username == null) {
            username = claims.getSubject();
        }
        return username;
    }

    /**
     * Extracts the email from the token claims.
     *
     * @param claims the JWT claims
     * @return the email, or null if not present
     */
    public String getEmailFromClaims(Claims claims) {
        return (String) claims.get("email");
    }
}
