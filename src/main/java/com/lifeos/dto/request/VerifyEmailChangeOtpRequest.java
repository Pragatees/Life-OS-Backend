package com.lifeos.dto.request;

import jakarta.validation.constraints.NotBlank;

public class VerifyEmailChangeOtpRequest {

    @NotBlank(message = "OTP is required")
    private String token;

    public VerifyEmailChangeOtpRequest() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}