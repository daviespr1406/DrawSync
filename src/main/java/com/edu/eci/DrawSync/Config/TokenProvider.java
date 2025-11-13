package com.edu.eci.DrawSync.Config;


import org.springframework.stereotype.Component;

/**
 * Spring-managed component that stores an application-wide access token in memory.
 * Useful for sharing a mutable token across collaborating beans within a single application instance.
 * Note: This implementation is not thread-safe and does not persist beyond the JVM lifecycle.
 */

/**
 * Sets or replaces the current access token.
 *
 * @param token the access token to store; may be null to clear the token
 */

/**
 * Retrieves the currently stored access token.
 *
 * @return the access token, or null if none has been set
 */
@Component
public class TokenProvider {

    private String accessToken;

    public void setAccessToken(String token) {
        this.accessToken = token;
    }

    public String getAccessToken() {
        return accessToken;
    }
    
}
