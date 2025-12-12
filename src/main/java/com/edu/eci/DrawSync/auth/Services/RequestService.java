
package com.edu.eci.DrawSync.auth.Services;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.edu.eci.DrawSync.auth.model.Request;
import com.edu.eci.DrawSync.auth.model.DTO.Response.ResponseToken;
import com.edu.eci.DrawSync.auth.model.DTO.Request.LoginRequest;
import com.edu.eci.DrawSync.auth.model.DTO.Request.SignUpRequest;
import com.edu.eci.DrawSync.auth.model.DTO.Request.VerifyRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthFlowType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.InitiateAuthRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.InitiateAuthResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class RequestService {

    private final RestTemplate restTemplate;
    private final CognitoIdentityProviderClient cognitoClient;

    private final Request request;

    public RequestService(Request request, RestTemplate restTemplate, CognitoIdentityProviderClient cognitoClient) {
        this.request = request;
        this.restTemplate = restTemplate;
        this.cognitoClient = cognitoClient;
    }

    public ResponseToken getTokenFromCognito(String code) {

        String clientId = request.getClientId();
        String clientSecret = request.getClientSecret();
        String baseUrl = request.getBaseUrl();

        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);

        // ✅ FIX: NO modificar el redirect_uri - usar exactamente como está configurado
        // El redirect_uri DEBE coincidir EXACTAMENTE con el usado en /oauth2/authorize
        String redirectUri = request.getRedirectUri();

        System.err.println("RequestService: Using Redirect URI for Token Exchange: [" + redirectUri + "]");
        System.err.println("RequestService: Client ID: [" + clientId + "]");
        System.err.println("RequestService: Code: [" + code + "]");

        body.add("redirect_uri", redirectUri);
        body.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(body,
                headers);

        try {
            ResponseToken response = restTemplate.postForEntity(baseUrl + "/oauth2/token", request, ResponseToken.class)
                    .getBody();
            return new ResponseToken(
                    response.access_token(),
                    response.expires_in(),
                    response.token_type(),
                    response.refresh_token(),
                    response.id_token(),
                    extractUsernameFromIdToken(response.id_token()));
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            System.err.println("RequestService: Token Exchange Failed. Status: " + e.getStatusCode());
            System.err.println("RequestService: Response Body: " + e.getResponseBodyAsString());
            System.err.println("RequestService: Redirect URI used: [" + redirectUri + "]");
            throw e;
        }
    }

    public ResponseToken login(LoginRequest loginRequest) {
        String clientId = request.getClientId();
        String clientSecret = request.getClientSecret();

        System.out.println("Login attempt for email: " + loginRequest.getEmail());
        System.out.println("ClientID: " + clientId);
        System.out.println("Has Secret: " + (clientSecret != null && !clientSecret.isEmpty()));

        // Use email as the username for secret hash calculation and auth parameters
        String secretHash = calculateSecretHash(clientId, clientSecret, loginRequest.getEmail());

        Map<String, String> authParams = Map.of(
                "USERNAME", loginRequest.getEmail(),
                "PASSWORD", loginRequest.getPassword(),
                "SECRET_HASH", secretHash);

        InitiateAuthRequest authRequest = InitiateAuthRequest.builder()
                .clientId(clientId)
                .authFlow(AuthFlowType.USER_PASSWORD_AUTH)
                .authParameters(authParams)
                .build();

        try {
            InitiateAuthResponse authResponse = cognitoClient.initiateAuth(authRequest);
            String username = loginRequest.getEmail();
            if (authResponse.authenticationResult().idToken() != null) {
                try {
                    username = extractUsernameFromIdToken(authResponse.authenticationResult().idToken());
                } catch (Exception e) {
                    System.out.println("Could not extract username from ID token during login, using email.");
                }
            }

            return new ResponseToken(
                    authResponse.authenticationResult().accessToken(),
                    authResponse.authenticationResult().expiresIn(),
                    authResponse.authenticationResult().tokenType(),
                    authResponse.authenticationResult().refreshToken(),
                    authResponse.authenticationResult().idToken(),
                    username);
        } catch (software.amazon.awssdk.services.cognitoidentityprovider.model.CognitoIdentityProviderException e) {
            System.err.println("Cognito Login Error: " + e.awsErrorDetails().errorMessage());
            System.err.println("Error Code: " + e.awsErrorDetails().errorCode());
            System.err.println("Request ID: " + e.requestId());
            throw e;
        } catch (Exception e) {
            System.err.println("Unexpected error during login: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public void signUp(SignUpRequest signUpRequest) {
        String clientId = request.getClientId();
        String clientSecret = request.getClientSecret();

        String secretHash = calculateSecretHash(clientId, clientSecret, signUpRequest.getUsername());

        AttributeType emailAttribute = AttributeType.builder()
                .name("email")
                .value(signUpRequest.getEmail())
                .build();

        // Use username as name fallback to satisfy potential requirement
        AttributeType nameAttribute = AttributeType.builder()
                .name("name")
                .value(signUpRequest.getUsername())
                .build();

        software.amazon.awssdk.services.cognitoidentityprovider.model.SignUpRequest awsSignUpRequest = software.amazon.awssdk.services.cognitoidentityprovider.model.SignUpRequest
                .builder()
                .clientId(clientId)
                .username(signUpRequest.getUsername())
                .password(signUpRequest.getPassword())
                .secretHash(secretHash)
                .userAttributes(emailAttribute, nameAttribute)
                .build();

        try {
            cognitoClient.signUp(awsSignUpRequest);
        } catch (software.amazon.awssdk.services.cognitoidentityprovider.model.CognitoIdentityProviderException e) {
            System.err.println("Cognito SignUp Error: " + e.awsErrorDetails().errorMessage());
            throw e;
        }
    }

    public void verifyUser(VerifyRequest verifyRequest) {
        String clientId = request.getClientId();
        String clientSecret = request.getClientSecret();

        String secretHash = calculateSecretHash(clientId, clientSecret, verifyRequest.getUsername());

        software.amazon.awssdk.services.cognitoidentityprovider.model.ConfirmSignUpRequest confirmSignUpRequest = software.amazon.awssdk.services.cognitoidentityprovider.model.ConfirmSignUpRequest
                .builder()
                .clientId(clientId)
                .username(verifyRequest.getUsername())
                .confirmationCode(verifyRequest.getConfirmationCode())
                .secretHash(secretHash)
                .build();

        try {
            cognitoClient.confirmSignUp(confirmSignUpRequest);
        } catch (software.amazon.awssdk.services.cognitoidentityprovider.model.CognitoIdentityProviderException e) {
            System.err.println("Cognito Verification Error: " + e.awsErrorDetails().errorMessage());
            throw e;
        }
    }

    private String calculateSecretHash(String userPoolClientId, String userPoolClientSecret, String userName) {
        final String HMAC_SHA256_ALGORITHM = "HmacSHA256";
        javax.crypto.Mac mac;
        try {
            javax.crypto.spec.SecretKeySpec signingKey = new javax.crypto.spec.SecretKeySpec(
                    userPoolClientSecret.getBytes(java.nio.charset.StandardCharsets.UTF_8),
                    HMAC_SHA256_ALGORITHM);
            mac = javax.crypto.Mac.getInstance(HMAC_SHA256_ALGORITHM);
            mac.init(signingKey);
            mac.update(userName.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            byte[] rawHmac = mac.doFinal(userPoolClientId.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return java.util.Base64.getEncoder().encodeToString(rawHmac);
        } catch (Exception e) {
            throw new RuntimeException("Error calculating secret hash", e);
        }
    }

    private String extractUsernameFromIdToken(String idToken) {
        try {
            String[] parts = idToken.split("\\.");
            if (parts.length < 2)
                return "Unknown";

            String payload = new String(java.util.Base64.getUrlDecoder().decode(parts[1]));
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            Map<String, Object> claims = mapper.readValue(payload, Map.class);

            if (claims.containsKey("name")) {
                return (String) claims.get("name");
            } else if (claims.containsKey("email")) {
                return (String) claims.get("email");
            } else if (claims.containsKey("cognito:username")) {
                return (String) claims.get("cognito:username");
            }
            return "Unknown";
        } catch (Exception e) {
            System.err.println("Error parsing ID token: " + e.getMessage());
            return "Unknown";
        }
    }
}