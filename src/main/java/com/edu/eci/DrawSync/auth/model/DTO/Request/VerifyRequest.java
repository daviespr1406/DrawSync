package com.edu.eci.DrawSync.auth.model.DTO.Request;

public class VerifyRequest {
    private String username;
    private String confirmationCode;

    public VerifyRequest() {
    }

    public VerifyRequest(String username, String confirmationCode) {
        this.username = username;
        this.confirmationCode = confirmationCode;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getConfirmationCode() {
        return confirmationCode;
    }

    public void setConfirmationCode(String confirmationCode) {
        this.confirmationCode = confirmationCode;
    }
}
