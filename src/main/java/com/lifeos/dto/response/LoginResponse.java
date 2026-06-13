package com.lifeos.dto.response;

import java.util.UUID;

public class LoginResponse {

    private String accessToken;
    private String tokenType;
    private UUID userId;
    private String username;
    private String fullName;
    private String email;

    public LoginResponse() {
    }

    public LoginResponse(String accessToken,
                         String tokenType,
                         UUID userId,
                         String username,
                         String fullName,
                         String email) {

        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.userId = userId;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}