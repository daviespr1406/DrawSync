package com.edu.eci.DrawSync.auth.Controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.edu.eci.DrawSync.auth.Services.AuthService;
import com.edu.eci.DrawSync.auth.model.DTO.Request.AuthUserRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/auth/users")
public class AuthController {

    private final AuthService authService;

    AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> postMethodName(@RequestBody AuthUserRequest user) {
        return ResponseEntity.ok(authService.createUserCognito(user));
    }
    
}
