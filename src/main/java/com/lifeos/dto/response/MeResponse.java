package com.lifeos.dto.response;

import com.lifeos.entity.AuthProvider;

import java.util.UUID;

public class MeResponse {

    private UUID userId;
    private String username;
    private String fullName;
    private String email;
    private String profilePicture;
    private AuthProvider provider;

    public MeResponse() {
    }

    public MeResponse(
            UUID userId,
            String username,
            String fullName,
            String email,
            String profilePicture,
            AuthProvider provider) {

        this.userId = userId;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.profilePicture = profilePicture;
        this.provider = provider;
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

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public AuthProvider getProvider() {
        return provider;
    }

    public void setProvider(AuthProvider provider) {
        this.provider = provider;
    }
}