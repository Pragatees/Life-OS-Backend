@PostMapping("/forgot-password")
public ResponseEntity<String> forgotPassword(
        @Valid @RequestBody ForgotPasswordRequest request) {

    User user = userRepository.findByEmail(request.getEmail())
            .orElse(null);

    if (user != null) {

        passwordResetTokenRepository.deleteByUser(user);

        PasswordResetToken resetToken = new PasswordResetToken();

        String token = String.format("%06d",
                new Random().nextInt(1000000));

        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(15));

        passwordResetTokenRepository.save(resetToken);

        try {

            emailService.sendEmail(
                    user.getEmail(),
                    "Life OS Password Reset Code",
                    "Your Life OS password reset code is:\n\n"
                            + token
                            + "\n\nThis code expires in 15 minutes.");

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to send OTP email.");
        }
    }

    return ResponseEntity.ok(
            "If an account exists, a password reset code has been sent.");
}