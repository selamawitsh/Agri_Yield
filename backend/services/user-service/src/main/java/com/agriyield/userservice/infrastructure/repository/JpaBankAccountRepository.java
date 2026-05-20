package com.agriyield.userservice.infrastructure.repository;

import com.agriyield.userservice.infrastructure.adapter.outgoing.persistence.entity.BankAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaBankAccountRepository extends JpaRepository<BankAccountEntity, UUID> {
    
    List<BankAccountEntity> findByUserId(UUID userId);
    
    Optional<BankAccountEntity> findByUserIdAndAccountType(UUID userId, String accountType);
    
    Optional<BankAccountEntity> findByUserIdAndIsDefaultTrue(UUID userId);
    
    @Modifying
    @Query("UPDATE BankAccountEntity b SET b.isDefault = false WHERE b.userId = :userId")
    void clearDefaultFlag(@Param("userId") UUID userId);
    
    long countByUserIdAndIsVerifiedTrue(UUID userId);
}
