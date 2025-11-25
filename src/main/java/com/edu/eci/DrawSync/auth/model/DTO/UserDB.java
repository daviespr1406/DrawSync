package com.edu.eci.DrawSync.auth.model.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(value = Include.NON_NULL)
public record UserDB(
    String username,
    String email,
    String createdAt,
    String picture
) {

}
