package com.edu.eci.DrawSync.auth.model.DTO.Request;

import com.fasterxml.jackson.annotation.JsonAlias;

/**
 * Data Transfer Object (DTO) for user authentication requests.
 * This record encapsulates the necessary information required to authenticate a user.
 *
 * @param Username the unique username of the user
 * @param email the email address of the user
 */
public record AuthUserRequest(
    @JsonAlias("username")
    String Username,
    String email,
    String password
) {}
