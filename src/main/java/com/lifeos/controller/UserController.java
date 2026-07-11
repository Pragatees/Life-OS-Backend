package com.lifeos.controller;

import com.lifeos.dto.request.ChangePasswordRequest;
import com.lifeos.dto.request.DeleteAccountRequest;
import com.lifeos.dto.request.SendDeleteAccountOtpRequest;
import com.lifeos.dto.request.SendEmailChangeOtpRequest;
import com.lifeos.dto.request.UpdateEmailRequest;
import com.lifeos.dto.request.UpdateProfileRequest;
import com.lifeos.dto.request.VerifyDeleteAccountOtpRequest;
import com.lifeos.dto.request.VerifyEmailChangeOtpRequest;
import com.lifeos.dto.request.VerifyPasswordRequest;

import com.lifeos.dto.response.MeResponse;
import com.lifeos.dto.response.MessageResponse;

import com.lifeos.entity.DeleteAccountToken;
import com.lifeos.entity.EmailChangeToken;
import com.lifeos.entity.User;

import com.lifeos.repository.DeleteAccountTokenRepository;
import com.lifeos.repository.EmailChangeTokenRepository;
import com.lifeos.repository.TaskRepository;
import com.lifeos.repository.UserRepository;

import com.lifeos.security.UserPrincipal;

import com.lifeos.service.CloudinaryService;
import com.lifeos.service.EmailService;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.lifeos.repository.GoalRepository;
import com.lifeos.repository.NoteRepository;


import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TaskRepository taskRepository;
    private final CloudinaryService cloudinaryService;
    private final DeleteAccountTokenRepository deleteAccountTokenRepository;
    private final EmailChangeTokenRepository emailChangeTokenRepository;
    private final EmailService emailService;
    private final GoalRepository goalRepository;
    private final NoteRepository noteRepository;

    public UserController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          TaskRepository taskRepository,
                          GoalRepository goalRepository,
                          NoteRepository noteRepository,
                          CloudinaryService cloudinaryService,
                          DeleteAccountTokenRepository deleteAccountTokenRepository,
                          EmailChangeTokenRepository emailChangeTokenRepository,
                          EmailService emailService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.taskRepository = taskRepository;
        this.goalRepository = goalRepository;
        this.noteRepository = noteRepository;
        this.cloudinaryService = cloudinaryService;
        this.deleteAccountTokenRepository = deleteAccountTokenRepository;
        this.emailChangeTokenRepository = emailChangeTokenRepository;
        this.emailService = emailService;
    }

    // ------------------------------------------------------------------
    // Get Current Logged-in User   (moved from AuthController: GET /me)
    // ------------------------------------------------------------------
    @GetMapping("/me")
    public ResponseEntity<MeResponse> getCurrentUser(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        MeResponse response = new MeResponse(
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getEmail(),
                user.getProfilePicture(),
                user.getProvider()
        );

        return ResponseEntity.ok(response);
    }

    // ------------------------------------------------------------------
    // Update Profile (full name / username)
    // ------------------------------------------------------------------
    @PatchMapping("/profile")
    public ResponseEntity<MeResponse> updateProfile(
            @RequestBody UpdateProfileRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update Full Name
        if (request.getFullName() != null && !request.getFullName().trim().isEmpty()) {
            user.setFullName(request.getFullName());
        }

        // Update Username
        if (request.getUsername() != null && !request.getUsername().trim().isEmpty()) {

            // Check whether username already exists
            if (!user.getUsername().equals(request.getUsername()) &&
                    userRepository.existsByUsername(request.getUsername())) {

                return ResponseEntity.badRequest().body(
                        new MeResponse(null, null, "Username already exists", null, null, null)
                );
            }

            user.setUsername(request.getUsername());
        }

        User updatedUser = userRepository.save(user);

        MeResponse response = new MeResponse(
                updatedUser.getId(),
                updatedUser.getUsername(),
                updatedUser.getFullName(),
                updatedUser.getEmail(),
                updatedUser.getProfilePicture(),
                updatedUser.getProvider()
        );

        return ResponseEntity.ok(response);
    }

    // ------------------------------------------------------------------
    // Verify Password
    // ------------------------------------------------------------------
    @PostMapping("/verify-password")
    public ResponseEntity<MessageResponse> verifyPassword(
            @RequestBody VerifyPasswordRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Incorrect password"));
        }

        return ResponseEntity.ok(new MessageResponse("Password verified successfully"));
    }

    // ------------------------------------------------------------------
    // Update Email
    // ------------------------------------------------------------------
    @PatchMapping("/email")
    public ResponseEntity<MessageResponse> updateEmail(
            @RequestBody UpdateEmailRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String newEmail = request.getNewEmail();

        if (newEmail == null || newEmail.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Email cannot be empty"));
        }

        if (userRepository.existsByEmail(newEmail)) {
            return ResponseEntity.badRequest().body(new MessageResponse("Email already exists"));
        }

        user.setEmail(newEmail);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("Email updated successfully"));
    }

    // ------------------------------------------------------------------
    // Change Email via OTP - Step 1: Send OTP  (moved from AuthController)
    // ------------------------------------------------------------------
    @PostMapping("/change-email/send-otp")
    public ResponseEntity<MessageResponse> sendEmailChangeOtp(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody SendEmailChangeOtpRequest request) {

        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Same email
        if (user.getEmail().equalsIgnoreCase(request.getNewEmail())) {
            return ResponseEntity.badRequest().body(
                    new MessageResponse("New email cannot be the same as your current email."));
        }

        // Email already exists
        if (userRepository.existsByEmail(request.getNewEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Email is already in use."));
        }

        // Delete previous OTP
        emailChangeTokenRepository.deleteByUser(user);

        // Generate 6-digit OTP
        String otp = String.format("%06d", new Random().nextInt(1000000));

        // Send email first
        try {
            emailService.sendEmail(
                    request.getNewEmail(),
                    "Life OS - Email Change Verification Code",
                    """
                    Hello,

                    We received a request to change the email address for your Life OS account.

                    Your verification code is:

                    %s

                    This code is valid for 15 minutes.

                    If you didn't request this change,
                    you can safely ignore this email.

                    Regards,
                    Life OS Team
                    """.formatted(otp));

        } catch (Exception e) {
            e.printStackTrace();

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Failed to send verification email."));
        }

        // Save OTP
        EmailChangeToken token = new EmailChangeToken();
        token.setUser(user);
        token.setNewEmail(request.getNewEmail());
        token.setToken(otp);
        token.setExpiryDate(LocalDateTime.now().plusMinutes(15));

        emailChangeTokenRepository.save(token);

        return ResponseEntity.ok(new MessageResponse("Verification code sent successfully."));
    }

    // ------------------------------------------------------------------
    // Change Email via OTP - Step 2: Verify OTP  (moved from AuthController)
    // ------------------------------------------------------------------
    @Transactional
    @PostMapping("/change-email/verify-otp")
    public ResponseEntity<MessageResponse> verifyEmailChangeOtp(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody VerifyEmailChangeOtpRequest request) {

        // Find logged-in user
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Find OTP
        EmailChangeToken emailChangeToken = emailChangeTokenRepository
                .findByToken(request.getToken())
                .orElse(null);

        // Invalid OTP
        if (emailChangeToken == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("Invalid verification code."));
        }

        // Security check: ensure OTP belongs to logged-in user
        if (!emailChangeToken.getUser().getId().equals(user.getId())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Invalid verification code."));
        }

        // Already used
        if (emailChangeToken.isUsed()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Verification code has already been used."));
        }

        // Expired
        if (emailChangeToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            emailChangeTokenRepository.delete(emailChangeToken);
            return ResponseEntity.badRequest().body(new MessageResponse("Verification code has expired."));
        }

        // Update email
        user.setEmail(emailChangeToken.getNewEmail());
        userRepository.save(user);

        // Delete OTP after successful verification
        emailChangeTokenRepository.delete(emailChangeToken);

        return ResponseEntity.ok(new MessageResponse("Email updated successfully."));
    }

    // ------------------------------------------------------------------
    // Change Password
    // ------------------------------------------------------------------
    @PatchMapping("/password")
    public ResponseEntity<MessageResponse> changePassword(
            @RequestBody ChangePasswordRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Current password is incorrect"));
        }

        // Check if new password matches confirm password
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            return ResponseEntity.badRequest().body(
                    new MessageResponse("New password and confirm password do not match"));
        }

        // Prevent using the same password
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body(
                    new MessageResponse("New password must be different from current password"));
        }

        // Update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("Password updated successfully. Please login again."));
    }

    // ------------------------------------------------------------------
    // Delete Account (direct, password-based)
    // ------------------------------------------------------------------
    @Transactional
    @DeleteMapping("/account")
    public ResponseEntity<MessageResponse> deleteAccount(
            @RequestBody DeleteAccountRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Verify Password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Incorrect password"));
        }

        // Delete all user tasks
        taskRepository.deleteByUser(user);

        // Delete user account
        userRepository.delete(user);

        return ResponseEntity.ok(new MessageResponse("Account and all associated tasks deleted successfully"));
    }

    // ------------------------------------------------------------------
    // Upload Profile Picture
    // ------------------------------------------------------------------
    @PostMapping("/profile-picture")
    public ResponseEntity<?> uploadProfilePicture(
            @RequestParam("image") MultipartFile image,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        try {
            User user = userRepository.findById(userPrincipal.getId())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            String imageUrl = cloudinaryService.uploadProfilePicture(image);

            user.setProfilePicture(imageUrl);
            userRepository.save(user);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Profile picture updated successfully");
            response.put("profilePicture", imageUrl);

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(
                    new MessageResponse("Failed to upload profile picture"));
        }
    }

    // ------------------------------------------------------------------
    // Delete Account via OTP - Step 1: Send OTP
    // ------------------------------------------------------------------
    @PostMapping("/delete-account/send-otp")
    public ResponseEntity<MessageResponse> sendDeleteAccountOtp(
            @Valid @RequestBody SendDeleteAccountOtpRequest request) {

        User user = userRepository.findByEmail(request.getEmail()).orElse(null);

        if (user == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("No account found with this email."));
        }

        deleteAccountTokenRepository.deleteByUser(user);

        String otp = String.format("%06d", new Random().nextInt(1000000));

        try {
            emailService.sendEmail(
                    user.getEmail(),
                    "Life OS - Delete Account Verification Code",
                    """
                    Hello,

                    We received a request to permanently delete your Life OS account.

                    Your verification code is:

                    %s

                    This code is valid for 15 minutes.

                    If you did not request this, please ignore this email.

                    Regards,
                    Life OS Team
                    """.formatted(otp));

        } catch (Exception e) {
            e.printStackTrace();

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Failed to send verification email."));
        }

        DeleteAccountToken token = new DeleteAccountToken();
        token.setUser(user);
        token.setToken(otp);
        token.setExpiryDate(LocalDateTime.now().plusMinutes(15));

        deleteAccountTokenRepository.save(token);

        return ResponseEntity.ok(new MessageResponse("Verification code sent successfully."));
    }

    // ------------------------------------------------------------------
    // Delete Account via OTP - Step 2: Verify OTP
    // ------------------------------------------------------------------
    @Transactional
    @PostMapping("/delete-account/verify-otp")
    public ResponseEntity<MessageResponse> verifyDeleteAccountOtp(
            @Valid @RequestBody VerifyDeleteAccountOtpRequest request) {

        DeleteAccountToken deleteToken = deleteAccountTokenRepository
                .findByToken(request.getToken())
                .orElse(null);

        if (deleteToken == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("Invalid verification code."));
        }

        if (deleteToken.isUsed()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Verification code has already been used."));
        }

        if (deleteToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            deleteAccountTokenRepository.delete(deleteToken);
            return ResponseEntity.badRequest().body(new MessageResponse("Verification code has expired."));
        }

        User user = deleteToken.getUser();

        // Delete the OTP token first
        deleteAccountTokenRepository.delete(deleteToken);

        // Delete all user-related data
        taskRepository.deleteByUser(user);
        goalRepository.deleteByUser(user);
        noteRepository.deleteByUser(user);

        // Finally delete the user
        userRepository.delete(user);

        return ResponseEntity.ok(new MessageResponse("Account deleted successfully."));
    }
}