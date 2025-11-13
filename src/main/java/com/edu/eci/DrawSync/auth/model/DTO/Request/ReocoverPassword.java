package com.edu.eci.DrawSync.auth.model.DTO.Request;

public record ReocoverPassword(
    String username,
    String code,
    String newPassword
) {}
