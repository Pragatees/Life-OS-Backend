package com.lifeos.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.lifeos.dto.response.GoogleUserInfo;
import com.lifeos.dto.response.LoginResponse;
import com.lifeos.entity.AuthProvider;
import com.lifeos.entity.User;
import com.lifeos.repository.UserRepository;
import com.lifeos.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class GoogleAuthService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${google.client-id}")
    private String googleClientId;

    public GoogleAuthService(UserRepository userRepository,
                             JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * Verify Google ID Token and extract user information.
     */
    public GoogleUserInfo verifyGoogleToken(String idToken) {

        try {

            GoogleIdTokenVerifier verifier =
                    new GoogleIdTokenVerifier.Builder(
                            GoogleNetHttpTransport.newTrustedTransport(),
                            GsonFactory.getDefaultInstance()
                    )
                            .setAudience(Collections.singletonList(googleClientId))
                            .build();

            GoogleIdToken googleIdToken = verifier.verify(idToken);

            if (googleIdToken == null) {
                throw new RuntimeException("Invalid Google ID Token.");
            }

            GoogleIdToken.Payload payload = googleIdToken.getPayload();

            return new GoogleUserInfo(
                    payload.getSubject(),
                    payload.getEmail(),
                    (String) payload.get("name"),
                    (String) payload.get("picture"),
                    payload.getEmailVerified()
            );

        } catch (Exception e) {
            throw new RuntimeException("Google authentication failed.", e);
        }
    }

    /**
     * Login using Google.
     */
    public LoginResponse loginWithGoogle(String idToken) {

        GoogleUserInfo googleUser = verifyGoogleToken(idToken);

        if (googleUser.getEmail() == null || googleUser.getEmail().isBlank()) {
            throw new RuntimeException("Google account email not found.");
        }

        User user = userRepository
                .findByEmail(googleUser.getEmail())
                .orElse(null);

        /*
         * First Google Login
         */
        if (user == null) {

            user = new User();

            user.setUsername(
                    generateUsername(googleUser.getEmail())
            );

            user.setFullName(
                    googleUser.getFullName()
            );

            user.setEmail(
                    googleUser.getEmail()
            );

            // Google users don't have a local password.
            user.setPassword(null);

            user.setProvider(AuthProvider.GOOGLE);

            user.setProviderId(
                    googleUser.getGoogleId()
            );

            user.setProfilePicture(
                    googleUser.getPicture()
            );

            user.setEmailVerified(
                    googleUser.getEmailVerified()
            );

            userRepository.save(user);

        } else {

            /*
             * Existing LOCAL account.
             * Link Google account if it hasn't been linked before.
             */
            if (user.getProviderId() == null) {

                user.setProviderId(
                        googleUser.getGoogleId()
                );

                user.setProfilePicture(
                        googleUser.getPicture()
                );

                user.setEmailVerified(
                        googleUser.getEmailVerified()
                );

                userRepository.save(user);
            }
        }

        String jwt = jwtTokenProvider.generateToken(user);

        return new LoginResponse(
                jwt,
                "Bearer",
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getEmail()
        );
    }

    /**
     * Generate a unique username from email.
     */
    private String generateUsername(String email) {

        String baseUsername =
                email.substring(0, email.indexOf("@"));

        String username = baseUsername;

        int counter = 1;

        while (userRepository.existsByUsername(username)) {

            username = baseUsername + counter;
            counter++;
        }

        return username;
    }
}