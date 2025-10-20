package com.edu.eci.DrawSync.auth.model.DTO.Request;

/**
 * Request payload for confirming a verification code associated with a user.
 *
 * <p>Used in flows such as email verification, password reset, or multi-factor authentication
 * to validate that the provided code matches the one issued for the specified user.</p>
 *
 * @param username The identifier of the user associated with the confirmation code.
 * @param code The one-time verification/confirmation code provided by the user.
 */
public record CodeConfirmRequest(
    String username,
    String code
) {
}
