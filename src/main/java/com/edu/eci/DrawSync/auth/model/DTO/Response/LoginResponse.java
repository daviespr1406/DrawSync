package com.edu.eci.DrawSync.auth.model.DTO.Response;

public record LoginResponse(
    String username,
    String id_token,
    String access_token,
    String refresh_token
) {

}
