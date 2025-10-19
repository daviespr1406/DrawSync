package com.edu.eci.DrawSync.auth.model.DTO.Request;

public record AuthUserRequest(
    String GroupName,
    String Username,
    String email,
    String password,
    String UserPoolId
) {}
