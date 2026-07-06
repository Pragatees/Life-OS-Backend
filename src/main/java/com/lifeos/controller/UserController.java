package com.lifeos.controller;

import com.lifeos.dto.request.UpdateProfileRequest;
import com.lifeos.dto.response.MeResponse;
import com.lifeos.entity.User;
import com.lifeos.repository.UserRepository;
import com.lifeos.security.UserPrincipal;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.crypto.password.PasswordEncoder;
import com.lifeos.dto.response.MessageResponse;
import com.lifeos.dto.request.VerifyPasswordRequest;
import com.lifeos.dto.request.UpdateEmailRequest;

import com.lifeos.dto.request.ChangePasswordRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.lifeos.repository.TaskRepository;
import com.lifeos.dto.request.DeleteAccountRequest;

import org.springframework.transaction.annotation.Transactional;

import com.lifeos.service.CloudinaryService;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TaskRepository taskRepository;
    private final CloudinaryService cloudinaryService;

    public UserController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          TaskRepository taskRepository,
                          CloudinaryService cloudinaryService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.taskRepository = taskRepository;
        this.cloudinaryService = cloudinaryService;
    }

    @PatchMapping("/profile")
    public ResponseEntity<MeResponse> updateProfile(
            @RequestBody UpdateProfileRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        /*
         * Update Full Name
         */
        if (request.getFullName() != null &&
                !request.getFullName().trim().isEmpty()) {

            user.setFullName(request.getFullName());
        }

        /*
         * Update Username
         */
        if (request.getUsername() != null &&
                !request.getUsername().trim().isEmpty()) {

            /*
             * Check whether username already exists
             */
            if (!user.getUsername().equals(request.getUsername()) &&
                    userRepository.existsByUsername(request.getUsername())) {

                return ResponseEntity.badRequest().body(
                        new MeResponse(
                                null,
                                null,
                                "Username already exists",
                                null
                        )
                );
            }

            user.setUsername(request.getUsername());
        }

        User updatedUser = userRepository.save(user);

        MeResponse response = new MeResponse(
                updatedUser.getId(),
                updatedUser.getUsername(),
                updatedUser.getFullName(),
                updatedUser.getEmail()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-password")
    public ResponseEntity<MessageResponse> verifyPassword(
            @RequestBody VerifyPasswordRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {

            return ResponseEntity.badRequest()
                    .body(new MessageResponse(
                            "Incorrect password"));
        }

        return ResponseEntity.ok(
                new MessageResponse(
                        "Password verified successfully"));
    }

    @PatchMapping("/email")
    public ResponseEntity<MessageResponse> updateEmail(
            @RequestBody UpdateEmailRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        String newEmail = request.getNewEmail();

        if (newEmail == null || newEmail.trim().isEmpty()) {

            return ResponseEntity.badRequest()
                    .body(new MessageResponse(
                            "Email cannot be empty"));
        }

        if (userRepository.existsByEmail(newEmail)) {

            return ResponseEntity.badRequest()
                    .body(new MessageResponse(
                            "Email already exists"));
        }

        user.setEmail(newEmail);

        userRepository.save(user);

        return ResponseEntity.ok(
                new MessageResponse(
                        "Email updated successfully"));
    }
    @PatchMapping("/password")
    public ResponseEntity<MessageResponse> changePassword(
            @RequestBody ChangePasswordRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        /*
         * Verify current password
         */
        if (!passwordEncoder.matches(
                request.getCurrentPassword(),
                user.getPassword())) {

            return ResponseEntity.badRequest()
                    .body(new MessageResponse(
                            "Current password is incorrect"));
        }

        /*
         * Check if new password matches confirm password
         */
        if (!request.getNewPassword()
                .equals(request.getConfirmPassword())) {

            return ResponseEntity.badRequest()
                    .body(new MessageResponse(
                            "New password and confirm password do not match"));
        }

        /*
         * Prevent using the same password
         */
        if (passwordEncoder.matches(
                request.getNewPassword(),
                user.getPassword())) {

            return ResponseEntity.badRequest()
                    .body(new MessageResponse(
                            "New password must be different from current password"));
        }

        /*
         * Update password
         */
        user.setPassword(
                passwordEncoder.encode(
                        request.getNewPassword()));

        userRepository.save(user);

        return ResponseEntity.ok(
                new MessageResponse(
                        "Password updated successfully. Please login again."));
    }
    @Transactional
    @DeleteMapping("/account")
    public ResponseEntity<MessageResponse> deleteAccount(
            @RequestBody DeleteAccountRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        /*
         * Verify Password
         */
        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {

            return ResponseEntity.badRequest()
                    .body(new MessageResponse(
                            "Incorrect password"));
        }

        /*
         * Delete all user tasks
         */
        taskRepository.deleteByUser(user);

        /*
         * Delete user account
         */
        userRepository.delete(user);

        return ResponseEntity.ok(
                new MessageResponse(
                        "Account and all associated tasks deleted successfully"));
    }
    @PostMapping("/profile-picture")
    public ResponseEntity<?> uploadProfilePicture(
            @RequestParam("image") MultipartFile image,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        try {

            User user = userRepository.findById(userPrincipal.getId())
                    .orElseThrow(() ->
                            new RuntimeException("User not found"));

            String imageUrl = cloudinaryService.uploadProfilePicture(image);

            user.setProfilePicture(imageUrl);

            userRepository.save(user);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Profile picture updated successfully");
            response.put("profilePicture", imageUrl);

            return ResponseEntity.ok(response);

        } catch (IOException e) {

            return ResponseEntity.internalServerError().body(
                    new MessageResponse("Failed to upload profile picture")
            );
        }
    }
}