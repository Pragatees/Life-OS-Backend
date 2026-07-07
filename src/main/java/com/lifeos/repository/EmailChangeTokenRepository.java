package com.lifeos.repository;

import com.lifeos.entity.EmailChangeToken;
import com.lifeos.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface EmailChangeTokenRepository
        extends JpaRepository<EmailChangeToken, UUID> {

    Optional<EmailChangeToken> findByToken(String token);

    @Transactional
    void deleteByUser(User user);

    @Transactional
    void deleteByExpiryDateBefore(LocalDateTime dateTime);
}