package com.agriyield.userservice.infrastructure.repository;

import com.agriyield.userservice.infrastructure.adapter.outgoing.persistence.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaUserRepository
        extends JpaRepository<UserEntity, UUID> {

    Optional<UserEntity> findByPhone(String phone);

    Optional<UserEntity> findByFaydaId(String faydaId);

    boolean existsByPhone(String phone);

    boolean existsByFaydaId(String faydaId);

    @Query("SELECT u FROM UserEntity u WHERE " +
           "(:role IS NULL OR u.role = :role) AND " +
           "(:status IS NULL OR u.accountStatus = :status)")
    Page<UserEntity> findAllWithFilters(
        @Param("role") String role,
        @Param("status") String status,
        Pageable pageable);

    long count();

    long countByRole(String role);

    long countByKycStatus(String kycStatus);

    long countByAccountStatus(String accountStatus);
}
