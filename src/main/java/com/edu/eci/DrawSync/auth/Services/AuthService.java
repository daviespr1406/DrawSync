package com.edu.eci.DrawSync.auth.Services;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.stream.Collectors;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.edu.eci.DrawSync.auth.model.UserStatus;
import com.edu.eci.DrawSync.auth.model.DTO.Request.AuthUserRequest;
import com.edu.eci.DrawSync.auth.model.DTO.Response.AuthUserResponse;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminGetUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminGetUserResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ConfirmSignUpRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.SignUpRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.SignUpResponse;

@Service
public class AuthService {
    @Value("${client.id}")
    private String clientId;
    
    @Value("${client.secret}")
    private String clientSecret;

    @Value("${region}")
    private String region;

    @Value("${user.pool_id}")
    private String userPoolId;

    /**
     * Creates a new user in AWS Cognito user pool.
     * 
     * This method initializes a Cognito Identity Provider client for the US_EAST_2 region
     * and creates a new user with the provided credentials. The user is created with a
     * temporary password and email verification is suppressed.
     * 
     * @param user the authentication user request containing username, email, and password
     * @return AuthUserResponse containing the created user's username, attributes map
     *         (including email), and current user status
     * @throws CognitoIdentityProviderException if the user creation fails due to Cognito service errors
     * @throws SdkClientException if there are issues with the AWS SDK client
     */
    public AuthUserResponse createUserCognito (AuthUserRequest user){

        var cognitoClient = setProviderClient(region);
        String secretHash = calculateSecretHash(clientId, clientSecret, user.Username());
        
        SignUpRequest request = SignUpRequest.builder()
        .clientId(clientId)
        .secretHash(secretHash)
        .username(user.Username())
        .password(user.password())
        .userAttributes(AttributeType.builder().name("email").value(user.email()).build())
        .build();

        SignUpResponse response = cognitoClient.signUp(request);
        
        return new AuthUserResponse(
            response.userSub(), 
            request.userAttributes().stream().collect(Collectors.toMap(AttributeType::name, AttributeType::value)),
            UserStatus.UNCONFIRMED
        );

    }

    /**
     * Creates and configures a CognitoIdentityProviderClient instance for the specified AWS region.
     * 
     * @param region the AWS region where the Cognito Identity Provider client will operate
     * @return a configured CognitoIdentityProviderClient instance for the specified region
     * @throws IllegalArgumentException if the region parameter is null
     */
    private CognitoIdentityProviderClient setProviderClient(String region){
        return CognitoIdentityProviderClient.builder().region(Region.of(region)).build();
    }

    /**
     * Calculates the SECRET_HASH required for Cognito operations when the app client has a secret.
     * 
     * The SECRET_HASH is computed as Base64(HMAC_SHA256(clientSecret, username + clientId))
     * 
     * @param clientId the Cognito app client ID
     * @param clientSecret the Cognito app client secret
     * @param username the username for which to calculate the hash
     * @return the calculated SECRET_HASH as a Base64-encoded string
     */
    private String calculateSecretHash(String clientId, String clientSecret, String username) {
        try {
            final String HMAC_SHA256 = "HmacSHA256";
            Mac mac = Mac.getInstance(HMAC_SHA256);
            SecretKeySpec secretKeySpec = new SecretKeySpec(clientSecret.getBytes(StandardCharsets.UTF_8), HMAC_SHA256);
            mac.init(secretKeySpec);
            byte[] rawHmac = mac.doFinal((username + clientId).getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(rawHmac);
        } catch (Exception e) {
            throw new RuntimeException("Error calculating SECRET_HASH", e);
        }
    }

    public AuthUserResponse confirmUserWithCode(String username,String code){
        var provider = setProviderClient(region);
        var secretHash = calculateSecretHash(clientId, clientSecret, username);
        ConfirmSignUpRequest confirmRequest = ConfirmSignUpRequest.builder()
        .clientId(clientId)
        .username(username)
        .confirmationCode(code)
        .secretHash(secretHash)
        .build();

        provider.confirmSignUp(confirmRequest);

        AdminGetUserRequest userRequest = AdminGetUserRequest.builder()
        .userPoolId(userPoolId)
        .username(username)
        .build();

        

        AdminGetUserResponse userResponse = provider.adminGetUser(userRequest);

        return new AuthUserResponse(
            userResponse.username(), 
            userResponse.userAttributes().stream().collect(Collectors.toMap(AttributeType::name, AttributeType::value)),
            UserStatus.CONFIRMED) ;
    }

   

}
