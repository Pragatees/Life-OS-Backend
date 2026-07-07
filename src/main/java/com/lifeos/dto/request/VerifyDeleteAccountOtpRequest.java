package com.lifeos.dto.request;

import jakarta.validation.constraints.NotBlank;

public class VerifyDeleteAccountOtpRequest {

    @NotBlank(message = "OTP is required")
    private String token;

    public VerifyDeleteAccountOtpRequest() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}