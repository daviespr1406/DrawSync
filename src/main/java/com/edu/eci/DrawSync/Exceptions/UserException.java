package com.edu.eci.DrawSync.Exceptions;

public class UserException extends RuntimeException{
    public static final String EMAIL_ALREADY_EXISTS = "Email already in use";
    public static final String USERNAME_ALREADY_EXISTS = "Username already in use";
    public static final String USERNAME_NO_EXISTS = "Username does not exits";
    private CODE_ERROR code;
    public UserException(String msg, CODE_ERROR code){
        super(msg);
        this.code = code;
    }

    public CODE_ERROR getCode() {
        return code;
    }
}
