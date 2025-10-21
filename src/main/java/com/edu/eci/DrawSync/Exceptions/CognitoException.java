package com.edu.eci.DrawSync.Exceptions;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import software.amazon.awssdk.services.cognitoidentityprovider.model.InvalidParameterException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.InvalidPasswordException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.LimitExceededException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.NotAuthorizedException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UserNotFoundException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UsernameExistsException;

/**
 * Global exception handler for Cognito-related exceptions in the DrawSync application.
 * This class uses Spring's @ControllerAdvice annotation to centrally handle exceptions
 * thrown by AWS Cognito operations across all controllers.
 * 
 * <p>The handler provides standardized error responses with consistent structure including
 * timestamp, error code, message, HTTP status, and request ID for tracking purposes.</p>
 * 
 * @see org.springframework.web.bind.annotation.ControllerAdvice
 * @see org.springframework.web.bind.annotation.ExceptionHandler
 */
@ControllerAdvice
public class CognitoException{
    
    /**
     * Builds a standardized error response for failed requests.
     *
     * The response body includes:
     * - timestamp: ISO-8601 instant when the error was generated
     * - error: application-specific error code
     * - message: human-readable error description
     * - status: numeric HTTP status code
     * - requestId: correlation identifier for tracing
     *
     * @param status    HTTP status to return.
     * @param code      Application-specific error code.
     * @param message   Human-readable error message.
     * @param requestId Unique identifier of the request for tracing/correlation.
     * @return a ResponseEntity with the provided HTTP status and a serializable body describing the error.
     */
    private ResponseEntity<?> buildError(HttpStatus status, CODE_ERROR code, String message,String requestId){
        String timestamp = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        .withZone(ZoneId.systemDefault())
        .format(Instant.now());
        return ResponseEntity
                            .status(status)
                            .body(
                                Map.of(
                                    "timestamp", timestamp,
                                    "error", code,
                                    "message",message,
                                    "status",status.value(),
                                    "requestId",requestId
                                )
                            );
    }

    /**
     * Handles a Cognito username conflict.
     *
     * Invoked when a UsernameExistsException is thrown, producing a standardized
     * error response that indicates the username is already in use. The HTTP status
     * and request ID are derived from the exception.
     *
     * @param e the UsernameExistsException raised by the identity provider
     * @return a ResponseEntity containing the error details and appropriate HTTP status
     */
    @ExceptionHandler(UsernameExistsException.class)
    public ResponseEntity<?> userNameExists(UsernameExistsException e){
        return buildError(
            HttpStatus.valueOf(e.statusCode()), 
            CODE_ERROR.USER_ALREADY_EXISTS, 
            "User account already exists",
            e.requestId()
            );
    }

    /**
     * Handles InvalidPasswordException thrown during password validation.
     * This exception occurs when a user attempts to set a password that does not meet
     * the required security policy standards.
     *
     * @param e the InvalidPasswordException containing details about the password policy violation
     * @return a ResponseEntity with HTTP 400 (Bad Request) status, containing an error response
     *         that includes the error code BAD_PASSWORD, a descriptive message about the password
     *         requirements (specifically the need for symbol characters), and the request ID for tracking
     */
    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<?> invalidPassword(InvalidPasswordException e){
        return buildError(
            HttpStatus.valueOf(e.statusCode()), 
            CODE_ERROR.BAD_PASSWORD, 
            "Password did not conform with password policy: Password must have symbol characters",
            e.requestId()
            );
    }

    /**
     * Exception handler for invalid email format errors reported by Cognito.
     *
     * Handles InvalidParameterException when an email address does not meet the required format
     * and returns a standardized error response including:
     * - An HTTP status derived from the exception's status code
     * - The application-specific error code BAD_EMAIL
     * - A descriptive message indicating an invalid email address format
     * - The originating request ID for traceability
     *
     * @param e the InvalidParameterException thrown by Cognito due to an invalid email format
     * @return a ResponseEntity containing the formatted error payload and appropriate HTTP status
     */
    @ExceptionHandler(InvalidParameterException.class)
    public ResponseEntity<?> invalidMail(InvalidParameterException e){
        return buildError(
            HttpStatus.valueOf(e.statusCode()), 
            CODE_ERROR.BAD_EMAIL, 
            "Invalid email address format",
            e.requestId()
            );
    }

    /**
     * Handles cases where the requested user does not exist by converting the
     * UserNotFoundException into a standardized error response. The response
     * includes the HTTP status provided by the exception, a USER_NOT_FOUND error
     * code, a descriptive message, and the associated request ID.
     *
     * @param e the UserNotFoundException that triggered this handler
     * @return a ResponseEntity containing the structured error payload and the appropriate HTTP status
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> userNotFound(UserNotFoundException e){
        return buildError(HttpStatus.valueOf(e.statusCode()), 
        CODE_ERROR.USER_NOT_FOUND, 
        "User does not exist", 
        e.requestId());
    }

    /**
     * Handles {@link NotAuthorizedException} when a user is already confirmed.
     * <p>
     * Returns an error response with HTTP status code from the exception,
     * a specific error code indicating the user is already confirmed,
     * and a descriptive message.
     *
     * @param e the {@link NotAuthorizedException} thrown when the user is already confirmed
     * @return a {@link ResponseEntity} containing the error details
     */
    @ExceptionHandler(NotAuthorizedException.class)
    public ResponseEntity<?> userAlreadyConfirmed(NotAuthorizedException e){
        return buildError(HttpStatus.valueOf(e.statusCode()), 
        CODE_ERROR.USER_ALREADY_CONFIMED, 
        "User is already confirmed", 
        e.requestId());
    }

    /**
     * Handles rate/attempt throttling by converting a LimitExceededException into an HTTP 429 (Too Many Requests) response.
     *
     * Produces a standardized error body identified as CODE_ERROR.LIMIT_EXCEEDED, includes a user-facing message advising
     * to retry after some time, and propagates the originating request ID for traceability.
     *
     * @param e the thrown LimitExceededException from the downstream service
     * @return a ResponseEntity with HTTP 429 (Too Many Requests) and a descriptive error payload
     */
    @ExceptionHandler(LimitExceededException.class)
    public ResponseEntity<?> limitExceeded(LimitExceededException e){
        return buildError(HttpStatus.TOO_MANY_REQUESTS, 
        CODE_ERROR.LIMIT_EXCEEDED, 
        "Attempt limit exceeded, please try after some time.", 
        e.requestId());
    }

}
