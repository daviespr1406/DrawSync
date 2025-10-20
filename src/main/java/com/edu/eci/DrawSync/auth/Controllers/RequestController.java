package com.edu.eci.DrawSync.auth.Controllers;

import com.edu.eci.DrawSync.auth.Services.RequestService;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("api/auth/request")
public class RequestController {

    private final RequestService requestService;

    RequestController(RequestService requestService) {
        this.requestService = requestService;
    }
    
    @GetMapping("/")
    public ResponseEntity<?> getTokenFromCognito(){
        var token = requestService.getTokenFromCognito();
        if (token == null) return ResponseEntity.internalServerError().body("Error retrieving token");
        return ResponseEntity.ok(Map.of(
            "message","token received correctly",
            "response", token
        ));
    }

    @GetMapping("/callback")
    public ResponseEntity<?> getMethodName(@RequestParam String code) {
        return requestService.handleCallback(code);
    }
    
    
}
