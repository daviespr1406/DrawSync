package com.edu.eci.DrawSync.auth.Controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.edu.eci.DrawSync.auth.Services.AuthService;
import com.edu.eci.DrawSync.auth.model.DTO.Request.AuthUserRequest;
import com.edu.eci.DrawSync.auth.model.DTO.Request.CodeConfirmRequest;
import com.edu.eci.DrawSync.auth.model.DTO.Request.LoginRequest;
import com.edu.eci.DrawSync.auth.model.DTO.Request.ReocoverPassword;
import com.edu.eci.DrawSync.auth.model.DTO.Response.AuthUserResponse;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;





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
    public ResponseEntity<?> signUp(
        @RequestBody AuthUserRequest user) {
        return ResponseEntity.ok(Map.of(
            "message","user created successfully",
            "user", authService.createUserCognito(user)
        ));
    }

    /**
     * Handles POST requests to confirm a user's account using a confirmation code.
     * <p>
     * Expects a {@link CodeConfirmRequest} in the request body containing the username and confirmation code.
     * Returns a response entity with a success message and the confirmed user details.
     *
     * @param request the confirmation request containing username and code
     * @return ResponseEntity containing a message and the confirmed user information
     */
    @PostMapping("/confirm")
    public ResponseEntity<?> confirmWithCode(@RequestBody CodeConfirmRequest request) {
        return ResponseEntity.ok(
            Map.of("message","User confirmed successfully",
            "user",authService.confirmUserWithCode(request.username(), request.code()))
            );
    }

    /**
     * Retrieves user information from Cognito based on the provided username.
     *
     * @param username the username of the user to retrieve
     * @return a ResponseEntity containing a success message and the user data
     */
    @GetMapping("/me")
    public ResponseEntity<AuthUserResponse> getCurrentUser() {
        return ResponseEntity.ok(authService.getUserFromCognito());
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> getMethodName(@PathVariable String username) {
        return ResponseEntity.ok(authService.getUserDB(username));
    }
    
    
    @GetMapping("/resendCode/{username}")
    public String resendCodeToUser(@PathVariable String username) {
        authService.resendCode(username);
        return "Code resent successfully";
    }

    @GetMapping("/password")
    public ResponseEntity<?> forgotPassword(@RequestParam String username) {
        authService.recoverPassword(username);
        return ResponseEntity.ok(Map.of(
            "message", "Password recovery initiated successfully"
        ));
    }
    
    @PostMapping("/confirm_password")
    public ResponseEntity<?> forgotPassword(@RequestBody ReocoverPassword request) {
        authService.confirmPassword(request.username(),request.code(),request.newPassword());
        return ResponseEntity.ok(Map.of(
            "message", "Password recovery initiated successfully"
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));     
    }
    
    
}
