package com.lifeos.dto.response;

public class GoogleUserInfo {

    private String googleId;
    private String email;
    private String fullName;
    private String picture;
    private Boolean emailVerified;

    public GoogleUserInfo() {
    }

    public GoogleUserInfo(String googleId,
                          String email,
                          String fullName,
                          String picture,
                          Boolean emailVerified) {

        this.googleId = googleId;
        this.email = email;
        this.fullName = fullName;
        this.picture = picture;
        this.emailVerified = emailVerified;
    }

    public String getGoogleId() {
        return googleId;
    }

    public String getEmail() {
        return email;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPicture() {
        return picture;
    }

    public Boolean getEmailVerified() {
        return emailVerified;
    }
}