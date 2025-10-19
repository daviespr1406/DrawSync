package com.edu.eci.DrawSync.auth.model.DTO.Request;

public record CodeConfirmRequest(
    String username,
    String code
) {
}
