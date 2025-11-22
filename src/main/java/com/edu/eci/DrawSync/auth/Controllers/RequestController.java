package com.edu.eci.DrawSync.auth.Controllers;

import com.edu.eci.DrawSync.Config.TokenProvider;
import com.edu.eci.DrawSync.auth.Services.RequestService;
import com.edu.eci.DrawSync.auth.model.DTO.Response.ResponseToken;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("api/auth/request")
public class RequestController {

    private final TokenProvider tokenProvider;

    private final RequestService requestService;

    RequestController(RequestService requestService, TokenProvider tokenProvider) {
        this.requestService = requestService;
        this.tokenProvider = tokenProvider;
    }
    

   @GetMapping("/test")
   public ResponseEntity<?> callback(@RequestParam String code) {
       ResponseToken tokens = requestService.getTokenFromCognito(code);
       tokenProvider.setAccessToken(tokens.access_token());
        return ResponseEntity.ok(Map.of(
            "message", "Token received successfully",
            "response", tokens
        ));
   }
    
}
