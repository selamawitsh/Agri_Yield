package com.agriyield.userservice.infrastructure.repository;

import com.agriyield.userservice.infrastructure.adapter.outgoing.persistence.entity.OtpEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaOtpRepository extends JpaRepository<OtpEntity, UUID> {
    
    Optional<OtpEntity> findByUserIdAndOtpCodeAndPurposeAndUsedAtIsNull(
        UUID userId, String otpCode, String purpose);
    
    @Modifying
    @Query("UPDATE OtpEntity o SET o.usedAt = CURRENT_TIMESTAMP WHERE o.userId = :userId AND o.purpose = :purpose AND o.usedAt IS NULL")
    void invalidateOldOtps(@Param("userId") UUID userId, @Param("purpose") String purpose);
    
    long countByUserIdAndPurposeAndCreatedAtAfter(
        UUID userId, String purpose, LocalDateTime since);
}
