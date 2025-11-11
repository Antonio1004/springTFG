package com.marketplace.repository;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.marketplace.model.VerificationCode;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {
    Optional<VerificationCode> findByCode(String token);
}
