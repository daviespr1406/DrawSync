package com.edu.eci.DrawSync.auth.Services;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import com.edu.eci.DrawSync.repository.UserRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;

import com.edu.eci.DrawSync.Exceptions.CODE_ERROR;
import com.edu.eci.DrawSync.Exceptions.UserException;
import com.edu.eci.DrawSync.auth.model.Request;
import com.edu.eci.DrawSync.auth.model.UserStatus;
import com.edu.eci.DrawSync.auth.model.DTO.UserDB;
import com.edu.eci.DrawSync.auth.model.DTO.Request.AuthUserRequest;
import com.edu.eci.DrawSync.auth.model.DTO.Request.LoginRequest;
import com.edu.eci.DrawSync.auth.model.DTO.Response.AuthUserResponse;
import com.edu.eci.DrawSync.auth.model.DTO.Response.LoginResponse;
import com.edu.eci.DrawSync.model.User;

import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthFlowType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthenticationResultType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ConfirmForgotPasswordRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ConfirmSignUpRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ForgotPasswordRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.InitiateAuthRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.InitiateAuthResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ResendConfirmationCodeRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.SignUpRequest;

@Service
public class AuthService {

    private final CognitoIdentityProviderClient cognitoClient;

    private final UserRepository userRepository;
    
    private final RestTemplate restTemplate;

    private final Request request;
    @Value("${client.id}")
    private String clientId;
    
    @Value("${client.secret}")
    private String clientSecret;

    @Value("${region}")
    private String region;

    @Value("${user.pool_id}")
    private String userPoolId;
    

    AuthService(UserRepository userRepository, CognitoIdentityProviderClient cognitoClient, RestTemplate restTemplate, Request request) {
        this.userRepository = userRepository;
        this.cognitoClient = cognitoClient;
        this.restTemplate = restTemplate;
        this.request = request;
    }

    
    /**
     * Creates a new user in AWS Cognito and stores their information in the local database.
     * 
     * This method performs the following operations:
     * 1. Validates that the username is not already registered in the local database
     * 2. Validates that the email is not already registered in the local database
     * 3. Calculates the secret hash required for Cognito authentication
     * 4. Registers the user in AWS Cognito with the provided credentials and email
     * 5. Saves the user's basic information (username and email) in the local database
     * 
     * @param user the authentication request containing the user's username, password, and email
     * @return AuthUserResponse containing the username, user attributes (email), and confirmation status (UNCONFIRMED)
     * @throws UserException if the username already exists (USERNAME_ALREADY_EXISTS) or 
     *                      if the email already exists (EMAIL_ALREADY_EXISTS)
     */
    public AuthUserResponse createUserCognito (AuthUserRequest user) throws UserException{
        
        if (userRepository.findByUsername(user.Username()).isPresent()) 
            throw new UserException(UserException.USERNAME_ALREADY_EXISTS,CODE_ERROR.USERNAME_ALREADY_EXISTS);
        if (userRepository.findByEmail(user.email()).isPresent()) 
            throw new UserException(UserException.EMAIL_ALREADY_EXISTS,CODE_ERROR.EMAIL_ALREADY_EXISTS);

        
        String secretHash = calculateSecretHash(clientId, clientSecret, user.Username());
        
        SignUpRequest request = SignUpRequest.builder()
        .clientId(clientId)
        .secretHash(secretHash)
        .username(user.Username())
        .password(user.password())
        .userAttributes(AttributeType.builder().name("email").value(user.email()).build())
        .build();

        cognitoClient.signUp(request);

        var userToSave = new User();
        userToSave.setUsername(user.Username());
        userToSave.setEmail(user.email());
        userToSave.setCreatedAt(
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            .withZone(ZoneId.systemDefault())
            .format(Instant.now())
        );
        userRepository.save(userToSave);

        return new AuthUserResponse(
            user.Username(), 
            request.userAttributes().stream().collect(Collectors.toMap(AttributeType::name, AttributeType::value)),
            UserStatus.UNCONFIRMED
        );
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

    /**
     * Confirms a user's registration using a confirmation code sent to their email or phone.
     * 
     * This method performs two main operations:
     * 1. Confirms the user's sign-up using the provided confirmation code
     * 2. Retrieves the user's details from the Cognito user pool
     * 
     * @param username the username of the user to be confirmed
     * @param code the confirmation code sent to the user during registration
     * @return an {@link AuthUserResponse} object containing the user's username, 
     *         attributes as a map, and confirmation status set to CONFIRMED
     * @throws software.amazon.awssdk.services.cognitoidentityprovider.model.CodeMismatchException 
     *         if the confirmation code is invalid
     * @throws software.amazon.awssdk.services.cognitoidentityprovider.model.ExpiredCodeException 
     *         if the confirmation code has expired
     * @throws software.amazon.awssdk.services.cognitoidentityprovider.model.UserNotFoundException 
     *         if the specified user does not exist
     */
    public AuthUserResponse confirmUserWithCode(String username,String code){
        var secretHash = calculateSecretHash(clientId, clientSecret, username);
    
        User existingUser = userRepository.findByUsername(username)
        .orElseThrow(
            () -> new UserException("User not found",CODE_ERROR.USER_NOT_FOUND)
            );

        ConfirmSignUpRequest confirmRequest = ConfirmSignUpRequest.builder()
        .clientId(clientId)
        .username(username)
        .confirmationCode(code)
        .secretHash(secretHash)
        .build();

        cognitoClient.confirmSignUp(confirmRequest);

       
        return new AuthUserResponse(
            existingUser.getUsername(), 
            Map.of("email",existingUser.getEmail()),
            UserStatus.CONFIRMED) ;
    }

    /**
     * Retrieves user information from AWS Cognito user pool.
     * 
     * This method queries the AWS Cognito service to fetch details about a specific user
     * identified by their username. It communicates with the Cognito user pool using the
     * AWS SDK and returns the user's information in a structured format.
     *
     * @param username the unique username of the user to retrieve from Cognito
     * @return an {@link AuthUserResponse} object containing the user's username,
     *         attributes as a key-value map, and current user status
     * @throws software.amazon.awssdk.services.cognitoidentityprovider.model.UserNotFoundException
     *         if the specified user does not exist in the user pool
     * @throws software.amazon.awssdk.services.cognitoidentityprovider.model.CognitoIdentityProviderException
     *         if there is an error communicating with AWS Cognito service
     */


    public AuthUserResponse getUserFromCognito() {
        StopWatch sw = new StopWatch();
        sw.start();
        @SuppressWarnings(value = "unchecked")
        Map<String,Object> body = restTemplate.getForObject(
            request.getBaseUrl() + "/oauth2/userInfo",
            Map.class);

        
        AuthUserResponse response = new AuthUserResponse(
            null,
            body.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> String.valueOf(e.getValue()))),
            UserStatus.CONFIRMED
        );
        sw.stop();
        System.out.println(sw.prettyPrint(TimeUnit.MILLISECONDS));
        return response;
    }

    public UserDB getUserDB(String username){
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UserException(UserException.USERNAME_NO_EXISTS,CODE_ERROR.USER_NOT_FOUND));
            return new UserDB(
            user.getUsername(),
            user.getEmail(), 
            user.getCreatedAt(),
            user.getPicture());
        }


   
    /**
     * Resends the sign-up confirmation code to the specified user via the configured identity provider (for example, Amazon Cognito).
     * <p>
     * The delivery channel (email or SMS) and destination are determined by the user pool configuration and the user's attributes.
     * This operation requires the service to be configured with a valid region and client ID.
     *
     * @param username the unique username of the user to whom the confirmation code should be re-sent; must match the value used at sign-up
     * @throws RuntimeException if the underlying identity provider rejects the request (e.g., user not found, user already confirmed, throttling, invalid parameters, or code delivery failure)
     */
    public void resendCode(String username){

        ResendConfirmationCodeRequest resend = ResendConfirmationCodeRequest.builder()
            .clientId(clientId)
            .username(username)
            .secretHash(calculateSecretHash(clientId, clientSecret, username))
            .build();
        
        cognitoClient.resendConfirmationCode(resend);
    } 

  
    public void recoverPassword(String username){
        var request = ForgotPasswordRequest
        .builder()
        .clientId(clientId)
        .secretHash(calculateSecretHash(clientId, clientSecret, username))
        .username(username)
        .build();

       cognitoClient.forgotPassword(request);
    }

    public ResponseEntity<?> confirmPassword(String username, String code, String newPassword){
        var request = ConfirmForgotPasswordRequest.builder()
        .clientId(clientId)
        .secretHash(calculateSecretHash(clientId, clientSecret, username))
        .username(username)
        .confirmationCode(code)
        .password(newPassword)
        .build();

        var response = cognitoClient.confirmForgotPassword(request);

        return ResponseEntity.ok(response.sdkHttpResponse());
    }

    public LoginResponse login(LoginRequest loginrequest){
        var request = InitiateAuthRequest.builder()
        .clientId(clientId)
        .authFlow(AuthFlowType.USER_PASSWORD_AUTH)
        .authParameters(
            Map.of(
                "USERNAME",loginrequest.username(),
                "PASSWORD",loginrequest.password(),
                "SECRET_HASH",calculateSecretHash(clientId, clientSecret, loginrequest.username())
                ))
        .build();

        InitiateAuthResponse response = cognitoClient.initiateAuth(request);

        AuthenticationResultType result = response.authenticationResult();
        
        return new LoginResponse(
            loginrequest.username(),
            result.idToken(),
            result.accessToken(),
            result.refreshToken()
        );
    }
}
