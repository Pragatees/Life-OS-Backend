package com.lifeos.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class SendEmailChangeOtpRequest {

    @NotBlank(message = "New email is required")
    @Email(message = "Invalid email format")
    private String newEmail;

    public SendEmailChangeOtpRequest() {
    }

    public String getNewEmail() {
        return newEmail;
    }

    public void setNewEmail(String newEmail) {
        this.newEmail = newEmail;
    }
}