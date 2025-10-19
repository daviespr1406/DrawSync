package com.edu.eci.DrawSync.auth.model.DTO.Response;

import java.util.List;
import java.util.Map;

import com.edu.eci.DrawSync.auth.model.UserStatus;



public record AuthUserResponse(
    String username,
    Map<String,Object> attributes,
    UserStatus status
) {
}
