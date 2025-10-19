package com.edu.eci.DrawSync.Exceptions;

import java.time.Instant;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import software.amazon.awssdk.services.cognitoidentityprovider.model.UsernameExistsException;

@ControllerAdvice
public class CognitoException{
    
    private ResponseEntity<?> buildError(HttpStatus status, CODE_ERROR code, String message,String requestId){
        return ResponseEntity
                            .status(status)
                            .body(
                                Map.of(
                                    "timestamp", Instant.now().toString(),
                                    "error", code,
                                    "message",message,
                                    "status",status.value(),
                                    "requestId",requestId
                                )
                            );
    }

    @ExceptionHandler(UsernameExistsException.class)
    public ResponseEntity<?> userNameExists(UsernameExistsException e){
        return buildError(
            HttpStatus.valueOf(e.statusCode()), 
            CODE_ERROR.USER_ALREADY_EXISTS, 
            "User account already exists",
            e.requestId()
            );
    }
}
