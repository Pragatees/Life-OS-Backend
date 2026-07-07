package com.lifeos.scheduler;

import com.lifeos.repository.EmailChangeTokenRepository;
import com.lifeos.repository.PasswordResetTokenRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ExpiredTokenCleanupScheduler {

    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailChangeTokenRepository emailChangeTokenRepository;

    public ExpiredTokenCleanupScheduler(
            PasswordResetTokenRepository passwordResetTokenRepository,
            EmailChangeTokenRepository emailChangeTokenRepository) {

        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.emailChangeTokenRepository = emailChangeTokenRepository;
    }

    /*
     * Runs every 5 minutes.
     */
    @Scheduled(fixedRate = 300000)
    public void removeExpiredTokens() {

        LocalDateTime now = LocalDateTime.now();

        passwordResetTokenRepository.deleteByExpiryDateBefore(now);

        emailChangeTokenRepository.deleteByExpiryDateBefore(now);

        System.out.println("Expired OTP tokens cleaned at: " + now);
    }
}