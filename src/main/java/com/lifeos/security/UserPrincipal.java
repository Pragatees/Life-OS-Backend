package com.lifeos.security;

import com.lifeos.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

public class UserPrincipal implements UserDetails {

    private final UUID id;
    private final String username;
    private final String fullName;
    private final String email;
    private final String password;
    private final String profilePicture;

    public UserPrincipal(UUID id,
                         String username,
                         String fullName,
                         String email,
                         String password,
                         String profilePicture) {

        this.id = id;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.profilePicture = profilePicture;
    }

    public static UserPrincipal create(User user) {

        return new UserPrincipal(
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getEmail(),
                user.getPassword(),
                user.getProfilePicture()
        );
    }

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getFullName() {
        return fullName;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}