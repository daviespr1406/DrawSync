package com.edu.eci.DrawSync.auth.Controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.edu.eci.DrawSync.auth.Services.AuthService;
import com.edu.eci.DrawSync.auth.model.DTO.Request.AuthUserRequest;
import com.edu.eci.DrawSync.auth.model.DTO.Request.CodeConfirmRequest;
import com.edu.eci.DrawSync.auth.model.DTO.Request.LoginRequest;
import com.edu.eci.DrawSync.auth.model.DTO.Request.ReocoverPassword;
import com.edu.eci.DrawSync.auth.model.DTO.Response.AuthUserResponse;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;





@RestController
@RequestMapping("/api/auth/users")
public class AuthController {

    private final AuthService authService;

    AuthController(AuthService authService) {
        this.authService = authService;
    }
    /**
     * Handles user registration requests.
     * 
     * This endpoint creates a new user account in AWS Cognito using the provided
     * authentication credentials and user information.
     *
     * @param user the authentication request containing user registration details
     *             (username, password, and other required attributes)
     * @return ResponseEntity containing a success message and the created user details
     *         wrapped in a Map with keys "message" and "user"
     * @throws  if user creation fails in Cognito
     */
    @PostMapping("/signup")
    public ResponseEntity<?> signUp(
        @RequestBody AuthUserRequest user) {
        return ResponseEntity.ok(Map.of(
            "message","user created successfully",
            "user", authService.createUserCognito(user)
        ));
    }

    /**
     * Handles POST requests to confirm a user's account using a confirmation code.
     * <p>
     * Expects a {@link CodeConfirmRequest} in the request body containing the username and confirmation code.
     * Returns a response entity with a success message and the confirmed user details.
     *
     * @param request the confirmation request containing username and code
     * @return ResponseEntity containing a message and the confirmed user information
     */
    @PostMapping("/confirm")
    public ResponseEntity<?> confirmWithCode(@RequestBody CodeConfirmRequest request) {
        return ResponseEntity.ok(
            Map.of("message","User confirmed successfully",
            "user",authService.confirmUserWithCode(request.username(), request.code()))
            );
    }

    /**
     * Retrieves user information from Cognito based on the provided username.
     *
     * @param username the username of the user to retrieve
     * @return a ResponseEntity containing a success message and the user data
     */
    @GetMapping("/me")
    public ResponseEntity<AuthUserResponse> getCurrentUser() {
        return ResponseEntity.ok(authService.getUserFromCognito());
    }

    /**
     * This Java function uses a GET request to retrieve user information from a database based on the
     * provided username.
     * @param {String} username - The `@GetMapping("/{username}")` annotation in the code snippet
     * indicates that this method will handle HTTP GET requests to a specific endpoint with a path
     * variable `{username}`. The `@PathVariable String username` parameter in the method signature
     * captures the value of the `username` path variable from the request URL
     * @returns The method is returning a ResponseEntity object with the result of calling the
     * `getUserDB` method from the `authService` with the `username` parameter.
     */
    @GetMapping("/{username}")
    public ResponseEntity<?> getMethodName(@PathVariable String username) {
        return ResponseEntity.ok(authService.getUserDB(username));
    }
    
    
    /**
     * The `resendCodeToUser` function in Java resends a verification code to a user specified by their
     * username.
     * @param {String} username - The `username` parameter in the `resendCodeToUser` method is a path
     * variable that is extracted from the URL path. It is used to identify the user to whom the code needs
     * to be resent.
     * @returns The method `resendCodeToUser` is returning a String message "Code resent successfully".
     */
    @GetMapping("/resendCode/{username}")
    public String resendCodeToUser(@PathVariable String username) {
        authService.resendCode(username);
        return "Code resent successfully";
    }

    /**
     * The `forgotPassword` function in the Java code initiates the password recovery process for a
     * specified username.
     * @param {String} username - The `username` parameter in the `forgotPassword` method is used to
     * identify the user for whom the password recovery process needs to be initiated. This parameter
     * is passed as a query parameter in the GET request to the `/password` endpoint.
     * @returns A ResponseEntity with a message indicating that the password recovery has been
     * initiated successfully.
     */
    @GetMapping("/password")
    public ResponseEntity<?> forgotPassword(@RequestParam String username) {
        authService.recoverPassword(username);
        return ResponseEntity.ok(Map.of(
            "message", "Password recovery initiated successfully"
        ));
    }
    
    /**
     * The `forgotPassword` function in the Java code snippet handles confirming a new password for
     * password recovery.
     * @param {ReocoverPassword} request - The `forgotPassword` method in the code snippet is a POST
     * mapping that handles a request to confirm a password recovery. The method takes in a
     * `RecoverPassword` object as the request body, which likely contains the following parameters:
     * @returns The method is returning a ResponseEntity with a message indicating that the password
     * recovery has been initiated successfully.
     */
    @PostMapping("/confirm_password")
    public ResponseEntity<?> forgotPassword(@RequestBody ReocoverPassword request) {
        authService.confirmPassword(request.username(),request.code(),request.newPassword());
        return ResponseEntity.ok(Map.of(
            "message", "Password recovery initiated successfully"
        ));
    }
    
    /**
     * This Java function handles a POST request to the "/login" endpoint and returns the result of the
     * login operation using the authService.
     * @param {LoginRequest} request - The `login` method in the code snippet is a POST mapping that
     * accepts a request body of type `LoginRequest`. The method then calls the `login` method of the
     * `authService` and returns the result as a ResponseEntity.
     * @returns The code snippet is returning a ResponseEntity object with the result of the login
     * operation performed by the authService.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));     
    }
    
    
}
