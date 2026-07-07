package com.lifeos.controller;
import java.util.Random;
import com.lifeos.dto.request.LoginRequest;
import com.lifeos.dto.request.SignUpRequest;
import com.lifeos.dto.response.AuthResponse;
import com.lifeos.dto.response.LoginResponse;
import com.lifeos.entity.User;
import com.lifeos.repository.UserRepository;
import com.lifeos.security.JwtTokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import org.springframework.transaction.annotation.Transactional;

import com.lifeos.dto.request.ForgotPasswordRequest;
import com.lifeos.entity.PasswordResetToken;
import com.lifeos.repository.PasswordResetTokenRepository;

import java.time.LocalDateTime;
import java.util.UUID;

import com.lifeos.exception.UserNotFoundException;

import com.lifeos.dto.response.MeResponse;
import com.lifeos.security.UserPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.lifeos.service.EmailService;
import jakarta.validation.Valid;

import com.lifeos.dto.request.ResetPasswordRequest;
import com.lifeos.entity.PasswordResetToken;

import com.lifeos.dto.request.VerifyOtpRequest;

import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;

import com.lifeos.dto.request.GoogleLoginRequest;
import com.lifeos.service.GoogleAuthService;

import com.lifeos.dto.request.SendEmailChangeOtpRequest;
import com.lifeos.entity.EmailChangeToken;
import com.lifeos.repository.EmailChangeTokenRepository;

import com.lifeos.dto.request.VerifyEmailChangeOtpRequest;
import com.lifeos.dto.response.MessageResponse;
import com.lifeos.entity.EmailChangeToken;

import com.lifeos.entity.DeleteAccountToken;
import com.lifeos.repository.DeleteAccountTokenRepository;
import com.lifeos.dto.request.VerifyDeleteAccountOtpRequest;
import com.lifeos.repository.TaskRepository;



@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final EmailService emailService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final GoogleAuthService googleAuthService;
    private final EmailChangeTokenRepository emailChangeTokenRepository;
    private final DeleteAccountTokenRepository deleteAccountTokenRepository;
    private final TaskRepository taskRepository;

    public AuthController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          JwtTokenProvider jwtTokenProvider,
                          EmailService emailService,
                          PasswordResetTokenRepository passwordResetTokenRepository,
                          GoogleAuthService googleAuthService,
                          EmailChangeTokenRepository emailChangeTokenRepository,
                          DeleteAccountTokenRepository deleteAccountTokenRepository,
                          TaskRepository taskRepository) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.emailService = emailService;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.googleAuthService = googleAuthService;
        this.emailChangeTokenRepository = emailChangeTokenRepository;
        this.deleteAccountTokenRepository = deleteAccountTokenRepository;
        this.taskRepository = taskRepository;
    }


    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> registerUser(@Valid @RequestBody SignUpRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new AuthResponse("Username already exists"));
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new AuthResponse("Email already exists"));
        }

        User user = new User();

        user.setUsername(request.getUsername());
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());

        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        userRepository.save(user);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new AuthResponse("User registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginRequest request) {

        User user = userRepository
                .findByUsernameOrEmail(
                        request.getUsernameOrEmail(),
                        request.getUsernameOrEmail()
                )
                .orElse(null);

        if (user == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse("Invalid username/email or password"));
        }

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse("Invalid username/email or password"));
        }

        String token = jwtTokenProvider.generateToken(user);

        LoginResponse response = new LoginResponse(
                token,
                "Bearer",
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getEmail()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<MeResponse> getCurrentUser(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

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

    @GetMapping("/test-email")
    public ResponseEntity<String> testEmail() {

        emailService.sendEmail(
                "haripragateesh7@gmail.com",
                "Life OS Email Test",
                "Congratulations! Life OS email integration is working."
        );

        return ResponseEntity.ok(
                "Test email sent successfully");
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElse(null);

        /*
         * Always return the same response.
         * This prevents attackers from discovering
         * whether an email exists.
         */
        if (user == null) {
            return ResponseEntity.ok(
                    "If an account exists, a password reset code has been sent.");
        }

        /*
         * Delete previous OTP
         */
        passwordResetTokenRepository.deleteByUser(user);

        /*
         * Generate 6-digit OTP
         */
        String otp = String.format("%06d",
                new Random().nextInt(1000000));

        /*
         * Send Email First
         */
        try {

            emailService.sendEmail(
                    user.getEmail(),
                    "Life OS - Password Reset Verification Code",

                    """
                    Hello,
    
                    We received a request to reset your Life OS account password.
    
                    Your verification code is:
    
                    %s
    
                    This code is valid for 15 minutes.
    
                    If you didn't request this password reset,
                    you can safely ignore this email.
    
                    Regards,
                    Life OS Team
                    """.formatted(otp));

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to send verification email.");
        }

        /*
         * Save OTP only after email is sent successfully
         */
        PasswordResetToken resetToken = new PasswordResetToken();

        resetToken.setUser(user);
        resetToken.setToken(otp);
        resetToken.setExpiryDate(
                LocalDateTime.now().plusMinutes(15));

        passwordResetTokenRepository.save(resetToken);

        return ResponseEntity.ok(
                "If an account exists, a password reset code has been sent.");
    }
    @Transactional
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {

        /*
         * Find token
         */
        PasswordResetToken resetToken =
                passwordResetTokenRepository
                        .findByToken(request.getToken())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Invalid reset token"));

        /*
         * Check if token already used
         */
        if (resetToken.isUsed()) {

            return ResponseEntity.badRequest()
                    .body("This reset link has already been used.");
        }

        /*
         * Check token expiry
         */
        if (resetToken.getExpiryDate()
                .isBefore(LocalDateTime.now())) {

            return ResponseEntity.badRequest()
                    .body("Reset link has expired.");
        }

        /*
         * Verify passwords match
         */
        if (!request.getNewPassword()
                .equals(request.getConfirmPassword())) {

            return ResponseEntity.badRequest()
                    .body("Passwords do not match.");
        }

        User user = resetToken.getUser();

        /*
         * Prevent using same password
         */
        if (passwordEncoder.matches(
                request.getNewPassword(),
                user.getPassword())) {

            return ResponseEntity.badRequest()
                    .body("New password must be different from the current password.");
        }

        /*
         * Update password
         */
        user.setPassword(
                passwordEncoder.encode(
                        request.getNewPassword()));

        userRepository.save(user);

        /*
         * Mark token as used
         */
        resetToken.setUsed(true);

        passwordResetTokenRepository.save(resetToken);

        /*
         * Delete other reset tokens
         */
        passwordResetTokenRepository
                .deleteByUser(user);

        /*
         * Send notification email
         */
        emailService.sendEmail(
                user.getEmail(),
                "Life OS Password Changed",
                "Your Life OS password has been changed successfully.\n\n"
                        + "If this wasn't you, please contact support immediately.");

        return ResponseEntity.ok(
                "Password reset successfully.");
    }
    @PostMapping("/verify-reset-otp")
    public ResponseEntity<String> verifyResetOtp(
            @Valid @RequestBody VerifyOtpRequest request) {

        PasswordResetToken resetToken =
                passwordResetTokenRepository
                        .findByToken(request.getToken())
                        .orElse(null);

        if (resetToken == null) {
            return ResponseEntity.badRequest()
                    .body("Invalid OTP.");
        }

        if (resetToken.isUsed()) {
            return ResponseEntity.badRequest()
                    .body("OTP has already been used.");
        }

        if (resetToken.getExpiryDate()
                .isBefore(LocalDateTime.now())) {

            return ResponseEntity.badRequest()
                    .body("OTP has expired.");
        }

        return ResponseEntity.ok(
                "OTP verified successfully.");
    }

    @PostMapping("/google/login")
    public ResponseEntity<LoginResponse> googleLogin(
            @Valid @RequestBody GoogleLoginRequest request) {

        System.out.println("Controller reached");
        System.out.println(request.getIdToken());

        LoginResponse response =
                googleAuthService.loginWithGoogle(request.getIdToken());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/mail")
    public ResponseEntity<String> testMail() {

        try {

            emailService.sendEmail(
                    "your_email@gmail.com",
                    "Life OS Test",
                    "SMTP Test");

            return ResponseEntity.ok("Mail Sent");

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity.status(500)
                    .body(e.getClass().getName() + "\n" + e.getMessage());
        }
    }

    @PostMapping("/change-email/send-otp")
    public ResponseEntity<MessageResponse> sendEmailChangeOtp(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody SendEmailChangeOtpRequest request) {

        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        /*
         * Same email
         */
        if (user.getEmail().equalsIgnoreCase(request.getNewEmail())) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(
                            "New email cannot be the same as your current email."));
        }

        /*
         * Email already exists
         */
        if (userRepository.existsByEmail(request.getNewEmail())) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(
                            "Email is already in use."));
        }

        /*
         * Delete previous OTP
         */
        emailChangeTokenRepository.deleteByUser(user);

        /*
         * Generate 6-digit OTP
         */
        String otp = String.format("%06d",
                new Random().nextInt(1000000));

        /*
         * Send email first
         */
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
                    .body(new MessageResponse(
                            "Failed to send verification email."));
        }

        /*
         * Save OTP
         */
        EmailChangeToken token = new EmailChangeToken();

        token.setUser(user);
        token.setNewEmail(request.getNewEmail());
        token.setToken(otp);
        token.setExpiryDate(
                LocalDateTime.now().plusMinutes(15));

        emailChangeTokenRepository.save(token);

        return ResponseEntity.ok(
                new MessageResponse(
                        "Verification code sent successfully."));
    }

    @Transactional
    @PostMapping("/change-email/verify-otp")
    public ResponseEntity<MessageResponse> verifyEmailChangeOtp(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody VerifyEmailChangeOtpRequest request) {

        /*
         * Find logged-in user
         */
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        /*
         * Find OTP
         */
        EmailChangeToken emailChangeToken =
                emailChangeTokenRepository
                        .findByToken(request.getToken())
                        .orElse(null);

        /*
         * Invalid OTP
         */
        if (emailChangeToken == null) {

            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Invalid verification code."));
        }

        /*
         * Security check
         * Ensure OTP belongs to logged-in user
         */
        if (!emailChangeToken.getUser().getId().equals(user.getId())) {

            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Invalid verification code."));
        }

        /*
         * Already used
         */
        if (emailChangeToken.isUsed()) {

            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Verification code has already been used."));
        }

        /*
         * Expired
         */
        if (emailChangeToken.getExpiryDate()
                .isBefore(LocalDateTime.now())) {

            emailChangeTokenRepository.delete(emailChangeToken);

            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Verification code has expired."));
        }

        /*
         * Update email
         */
        user.setEmail(emailChangeToken.getNewEmail());

        userRepository.save(user);

        /*
         * Delete OTP after successful verification
         */
        emailChangeTokenRepository.delete(emailChangeToken);

        return ResponseEntity.ok(
                new MessageResponse("Email updated successfully."));
    }

    @Transactional
    @PostMapping("/delete-account/verify-otp")
    public ResponseEntity<MessageResponse> verifyDeleteAccountOtp(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody VerifyDeleteAccountOtpRequest request) {

        /*
         * Find logged-in user
         */
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        /*
         * Find OTP
         */
        DeleteAccountToken deleteToken =
                deleteAccountTokenRepository
                        .findByToken(request.getToken())
                        .orElse(null);

        /*
         * Invalid OTP
         */
        if (deleteToken == null) {

            return ResponseEntity.badRequest()
                    .body(new MessageResponse(
                            "Invalid verification code."));
        }

        /*
         * Ensure OTP belongs to logged-in user
         */
        if (!deleteToken.getUser().getId().equals(user.getId())) {

            return ResponseEntity.badRequest()
                    .body(new MessageResponse(
                            "Invalid verification code."));
        }

        /*
         * Already used
         */
        if (deleteToken.isUsed()) {

            return ResponseEntity.badRequest()
                    .body(new MessageResponse(
                            "Verification code has already been used."));
        }

        /*
         * Expired OTP
         */
        if (deleteToken.getExpiryDate()
                .isBefore(LocalDateTime.now())) {

            deleteAccountTokenRepository.delete(deleteToken);

            return ResponseEntity.badRequest()
                    .body(new MessageResponse(
                            "Verification code has expired."));
        }

        /*
         * Delete OTP
         */
        deleteAccountTokenRepository.delete(deleteToken);

        /*
         * Delete all tasks of the user
         */
        taskRepository.deleteByUser(user);

        /*
         * Delete user account
         */
        userRepository.delete(user);

        return ResponseEntity.ok(
                new MessageResponse(
                        "Account deleted successfully."));
    }

}
