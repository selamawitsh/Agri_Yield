package com.agriyield.userservice.infrastructure.repository;

import com.agriyield.userservice.infrastructure.adapter.outgoing.persistence.entity.InvestorProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaInvestorProfileRepository extends JpaRepository<InvestorProfileEntity, UUID> {
    
    Optional<InvestorProfileEntity> findByUserId(UUID userId);
    
    @Modifying
    @Query("UPDATE InvestorProfileEntity i SET i.totalInvestedEtb = i.totalInvestedEtb + :amount WHERE i.userId = :userId")
    void addToTotalInvested(@Param("userId") UUID userId, @Param("amount") BigDecimal amount);
    
    @Modifying
    @Query("UPDATE InvestorProfileEntity i SET i.totalReturnedEtb = i.totalReturnedEtb + :amount WHERE i.userId = :userId")
    void addToTotalReturned(@Param("userId") UUID userId, @Param("amount") BigDecimal amount);
}
