package com.edu.eci.DrawSync.auth.Controllers;

import com.edu.eci.DrawSync.config.TokenProvider;
import com.edu.eci.DrawSync.auth.Services.RequestService;
import com.edu.eci.DrawSync.auth.model.DTO.Response.ResponseToken;
import com.edu.eci.DrawSync.auth.model.DTO.Request.VerifyRequest;
import com.edu.eci.DrawSync.auth.model.DTO.Request.SignUpRequest;
import com.edu.eci.DrawSync.auth.model.DTO.Request.LoginRequest;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth/request")
@org.springframework.web.bind.annotation.CrossOrigin(origins = "*")
public class RequestController {

    private final TokenProvider tokenProvider;

    private final RequestService requestService;

    RequestController(RequestService requestService, TokenProvider tokenProvider) {
        this.requestService = requestService;
        this.tokenProvider = tokenProvider;
    }

    @GetMapping("/test")
    public ResponseEntity<?> callback(@RequestParam String code) {
        System.out.println("RequestController: Received callback with code: " + code);
        try {
            ResponseToken tokens = requestService.getTokenFromCognito(code);
            tokenProvider.setAccessToken(tokens.access_token());
            return ResponseEntity.ok(Map.of(
                    "message", "Token received successfully",
                    "response", tokens));
        } catch (Exception e) {
            System.err.println("Error in callback: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(403).body(Map.of("error", "Authentication failed: " + e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        ResponseToken tokens = requestService.login(loginRequest);
        tokenProvider.setAccessToken(tokens.access_token());
        return ResponseEntity.ok(Map.of(
                "message", "Login successful",
                "response", tokens));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody SignUpRequest signUpRequest) {
        requestService.signUp(signUpRequest);
        return ResponseEntity.ok(Map.of(
                "message", "User registered successfully"));
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verify(@RequestBody VerifyRequest verifyRequest) {
        requestService.verifyUser(verifyRequest);
        return ResponseEntity.ok(Map.of(
                "message", "User verified successfully"));
    }

}
