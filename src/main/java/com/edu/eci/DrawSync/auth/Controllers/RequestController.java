package com.edu.eci.DrawSync.auth.Controllers;

import com.edu.eci.DrawSync.auth.Services.RequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/auth/request")
public class RequestController {

    private final RequestService requestService;

    RequestController(RequestService requestService) {
        this.requestService = requestService;
    }
    
    @GetMapping("/")
    public ResponseEntity<?> getTokenFromCognito(){
        if (requestService.getTokenFromCognito() == null) return ResponseEntity.internalServerError().body("Error retrieving token");
        return ResponseEntity.ok(requestService.getTokenFromCognito());
    }
}
