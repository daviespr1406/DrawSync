package com.edu.eci.DrawSync.auth.Services;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import com.edu.eci.DrawSync.auth.model.Request;
import com.edu.eci.DrawSync.auth.model.DTO.Response.ResponseToken;

@Service
public class RequestService {

    private final Request request;
    
    public RequestService(Request request) {
        this.request = request;
    }

    public ResponseToken getTokenFromCognito(){

        RestTemplate restTemplate = request.getRestTemplate();
        String clientId = request.getClientId();
        String clientSecret = request.getClientSecret();
        String baseUrl = request.getBaseUrl();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("redirect_uri", "http://localhost:8080/api/auth/request/test");
        body.add("request_type","code" );
       
        
        
        HttpEntity< MultiValueMap<String,String>> request = new HttpEntity<MultiValueMap<String,String>>(body, headers);

        ResponseToken response = restTemplate.postForEntity(baseUrl + "/oauth2/token", request, ResponseToken.class).getBody();
        
        return response;
    }

    public ResponseEntity<?> handleCallback(@RequestParam("code") String code) {

        RestTemplate restTemplate = request.getRestTemplate();
        String clientId = request.getClientId();
        String clientSecret = request.getClientSecret();
        String baseUrl = request.getBaseUrl();
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("redirect_uri", "http://localhost:8080/callback");
        body.add("code", code);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        String tokenEndpoint = baseUrl + "/oauth2/token";
        ResponseEntity<String> response = restTemplate.postForEntity(tokenEndpoint, request, String.class);

        return response;
    }
}
