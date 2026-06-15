package com.lifeos.dto.request;

public class UpdateProfileRequest {

    private String fullName;

    private String username;

    public UpdateProfileRequest() {
    }

    public String getFullName() {

        return fullName;
    }

    public void setFullName(String fullName) {

        this.fullName = fullName;
    }

    public String getUsername() {

        return username;
    }

    public void setUsername(String username) {

        this.username = username;
    }
}