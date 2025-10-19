package com.edu.eci.DrawSync.auth.Services;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.passay.CharacterData;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;
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

        var cognitoClient = setProviderClient(Region.US_EAST_2);
        AdminCreateUserRequest request = AdminCreateUserRequest
        .builder()
        .userPoolId(userPoolId)
        .username(user.Username())
        .desiredDeliveryMediumsWithStrings("EMAIL")
        .userAttributes(
            AttributeType.builder().name("email").value(user.email()).build()
        )
        .temporaryPassword(generateTemporaryPassword())
        .build();

        AdminCreateUserResponse response = cognitoClient.adminCreateUser(request);
        
        return new AuthUserResponse(
            response.user().username(), 
            request.userAttributes().stream().collect(Collectors.toMap(AttributeType::name, AttributeType::value)),
            UserStatus.valueOf(response.user().userStatusAsString())
        );

    }

    /**
     * Creates and configures a CognitoIdentityProviderClient instance for the specified AWS region.
     * 
     * @param region the AWS region where the Cognito Identity Provider client will operate
     * @return a configured CognitoIdentityProviderClient instance for the specified region
     * @throws IllegalArgumentException if the region parameter is null
     */
    private CognitoIdentityProviderClient setProviderClient(Region region){
        return CognitoIdentityProviderClient.builder().region(region).build();
    }

    /**
     * Generates a temporary password containing a mix of uppercase letters, lowercase letters,
     * digits, and special characters. The generated password will be 12 characters long and
     * will include at least one character from each category.
     *
     * @return a randomly generated temporary password as a {@code String}
     */
    private String generateTemporaryPassword(){
        PasswordGenerator generator = new PasswordGenerator();
        CharacterRule upper = new CharacterRule(EnglishCharacterData.UpperCase,1);
        CharacterRule lower = new CharacterRule(EnglishCharacterData.LowerCase,1);
        CharacterRule digit = new CharacterRule(EnglishCharacterData.Digit,1);
        CharacterRule special = new CharacterRule(EnglishCharacterData.Special, 1);

        String pswd = generator.generatePassword(12, Arrays.asList(upper, lower, digit, special));
        return pswd;
    }
}
