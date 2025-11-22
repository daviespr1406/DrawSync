package com.edu.eci.DrawSync.auth.model.DTO.Response;

/**
 * Immutable DTO representing an authorization token response.
 *
 * <p>Holds the access token string, its time-to-live in seconds, and the token type
 * (for example, "Bearer").</p>
 *
 * @param access_token the issued access token
 * @param expires_in the token lifetime, in seconds
 * @param token_type the token type indicator (e.g., "Bearer")
 */
public record ResponseToken(
    String access_token,
    Integer expires_in,
    String token_type
) {
}