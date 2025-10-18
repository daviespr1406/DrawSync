package com.edu.eci.DrawSync.auth.Services;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.edu.eci.DrawSync.auth.Request;

@Service
public class RequestService {

    private final Request request;
    
    public RequestService(Request request) {
        this.request = request;
    }

    public ResponseEntity<String> getTokenFromCognito(){

        RestTemplate restTemplate = request.getRestTemplate();
        String clientId = request.getClientId();
        String clientSecret = request.getClientSecret();
        String baseUrl = request.getBaseUrl();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String,String> body = new LinkedMultiValueMap<>();
        body.add("client_id",clientId);
        body.add("client_secret", clientSecret);
        body.add("scope", "default-m2m-resource-server-zrqwan/read");
        body.add("grant_type", "client_credentials");
       
        

        HttpEntity< MultiValueMap<String,String>> request = new HttpEntity<MultiValueMap<String,String>>(body, headers);

        System.out.println("El request es "+ request);
        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl, request, String.class);

        return response;
    }
}
