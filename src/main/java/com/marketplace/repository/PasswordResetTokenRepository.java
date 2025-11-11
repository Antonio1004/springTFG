package com.marketplace.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.marketplace.model.PasswordResetToken;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
}