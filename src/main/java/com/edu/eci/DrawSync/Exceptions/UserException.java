package com.edu.eci.DrawSync.Exceptions;

public class UserException extends RuntimeException{
    public static final String EMAIL_ALREADY_EXISTS = "Email already in use";
    
    public UserException(String msg){
        super(msg);
    }
}
