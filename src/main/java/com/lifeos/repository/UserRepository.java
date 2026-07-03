package com.lifeos.repository;

import com.lifeos.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    // Check if username already exists
    boolean existsByUsername(String username);

    // Check if email already exists
    boolean existsByEmail(String email);

    // Find by username
    Optional<User> findByUsername(String username);

    // Find by email
    Optional<User> findByEmail(String email);

    // Login using username or email
    Optional<User> findByUsernameOrEmail(String username, String email);

    // Find Google account by Google Provider ID
    Optional<User> findByProviderId(String providerId);
}