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

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final EmailService emailService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    public AuthController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          JwtTokenProvider jwtTokenProvider,
                          EmailService emailService ,
                          PasswordResetTokenRepository passwordResetTokenRepository) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.emailService = emailService;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
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
                .findByUsername(request.getUsername())
                .orElse(null);

        if (user == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse("Invalid username or password"));
        }

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse("Invalid username or password"));
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

        MeResponse response = new MeResponse(
                userPrincipal.getId(),
                userPrincipal.getUsername(),
                userPrincipal.getFullName(),
                userPrincipal.getEmail()
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

}
