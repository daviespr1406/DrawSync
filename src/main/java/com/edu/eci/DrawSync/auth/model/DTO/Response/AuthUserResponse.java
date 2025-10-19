package com.edu.eci.DrawSync.auth.model.DTO.Response;

import java.util.Map;

import com.edu.eci.DrawSync.auth.model.UserStatus;

/**
 * Data Transfer Object (DTO) representing the authentication response for a user.
 * This record encapsulates the essential information returned after a successful user authentication.
 *
 * @param username The unique username identifier of the authenticated user
 * @param attributes A map containing additional user attributes such as email, roles, permissions, or other metadata
 * @param status The current status of the user account (e.g., ACTIVE, INACTIVE, SUSPENDED)
 */
public record AuthUserResponse(
    String username,
    Map<String,Object> attributes,
    UserStatus status
) {
}
