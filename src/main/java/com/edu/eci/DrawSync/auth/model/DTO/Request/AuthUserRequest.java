package com.edu.eci.DrawSync.auth.model.DTO.Request;

/**
 * Data Transfer Object (DTO) for user authentication requests.
 * This record encapsulates the necessary information required to authenticate a user.
 *
 * @param GroupName the name of the group the user belongs to
 * @param Username the unique username of the user
 * @param email the email address of the user
 * @param password the password for authentication (should be handled securely)
 */
public record AuthUserRequest(
    String Username,
    String email,
    String password
) {}
