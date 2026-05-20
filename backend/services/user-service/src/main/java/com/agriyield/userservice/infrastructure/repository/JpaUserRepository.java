package com.agriyield.userservice.infrastructure.repository;

import com.agriyield.userservice.infrastructure.adapter.outgoing.persistence.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaUserRepository extends JpaRepository<UserEntity, UUID> {
    
    Optional<UserEntity> findByPhone(String phone);
    Optional<UserEntity> findByFaydaId(String faydaId);
    Optional<UserEntity> findByEmail(String email);
    boolean existsByPhone(String phone);
    boolean existsByFaydaId(String faydaId);
    
    @Modifying
    @Query("UPDATE UserEntity u SET u.accountStatus = :status WHERE u.id = :userId")
    void updateAccountStatus(@Param("userId") UUID userId, @Param("status") String status);
    
    @Modifying
    @Query("UPDATE UserEntity u SET u.kycStatus = :kycStatus, u.faydaVerifiedAt = CURRENT_TIMESTAMP WHERE u.id = :userId")
    void updateKycStatus(@Param("userId") UUID userId, @Param("kycStatus") String kycStatus);
    
    // Admin query methods
    Page<UserEntity> findByRole(String role, Pageable pageable);
    Page<UserEntity> findByAccountStatus(String status, Pageable pageable);
    Page<UserEntity> findByRoleAndAccountStatus(String role, String status, Pageable pageable);
    
    long countByRole(String role);
    long countByKycStatus(String kycStatus);
    long countByAccountStatus(String accountStatus);
}
