package com.edu.eci.DrawSync.auth.Services;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.edu.eci.DrawSync.auth.model.UserStatus;
import com.edu.eci.DrawSync.auth.model.DTO.Request.AuthUserRequest;
import com.edu.eci.DrawSync.auth.model.DTO.Response.AuthUserResponse;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminCreateUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminCreateUserResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;

@Service
public class AuthService {
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
        Region region = Region.US_EAST_2;
        CognitoIdentityProviderClient cognitoClient = CognitoIdentityProviderClient
        .builder()
        .region(region)
        .build();

        AdminCreateUserRequest request = AdminCreateUserRequest
        .builder()
        .userPoolId(userPoolId)
        .username(user.Username())
        .messageAction("SUPPRESS")
        .userAttributes(
            AttributeType.builder().name("email").value(user.email()).build()
        )
        .temporaryPassword(user.password())
        .build();

        AdminCreateUserResponse response = cognitoClient.adminCreateUser(request);
        
        return new AuthUserResponse(
            response.user().username(), 
            request.userAttributes().stream().collect(Collectors.toMap(AttributeType::name, AttributeType::value)),
            UserStatus.valueOf(response.user().userStatusAsString())
        );

    }
}
