package com.edu.eci.DrawSync.Exceptions;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<?> buildError(HttpStatus status, CODE_ERROR code, String message){
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
                                    "status",status.value()
                                )
                            );
    }

    @ExceptionHandler(UserException.class)
    public ResponseEntity<?> userExceptions(UserException e){
        return buildError(HttpStatus.BAD_REQUEST, e.getCode(), e.getMessage());
    }


}
