package com.edu.eci.DrawSync.auth.Controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.edu.eci.DrawSync.auth.Services.AuthService;
import com.edu.eci.DrawSync.auth.model.DTO.Request.AuthUserRequest;

import java.util.Map;

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

    /**
     * Handles user registration requests.
     * 
     * This endpoint creates a new user account in AWS Cognito using the provided
     * authentication credentials and user information.
     *
     * @param user the authentication request containing user registration details
     *             (username, password, and other required attributes)
     * @return ResponseEntity containing a success message and the created user details
     *         wrapped in a Map with keys "message" and "user"
     * @throws  if user creation fails in Cognito
     */
    @PostMapping("/signup")
    public ResponseEntity<?> postMethodName(@RequestBody AuthUserRequest user) {
        return ResponseEntity.ok(Map.of(
            "message","user created successfully",
            "user", authService.createUserCognito(user)
        ));
    }
    
}
