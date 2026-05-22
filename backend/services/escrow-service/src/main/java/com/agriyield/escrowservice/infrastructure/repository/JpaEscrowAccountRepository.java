package com.agriyield.escrowservice.infrastructure.repository;

import com.agriyield.escrowservice.infrastructure.adapter.outgoing.persistence.entity.EscrowAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaEscrowAccountRepository extends JpaRepository<EscrowAccountEntity, UUID> {

    Optional<EscrowAccountEntity> findByInvestmentId(UUID investmentId);

    List<EscrowAccountEntity> findByFarmerId(UUID farmerId);

    List<EscrowAccountEntity> findByInvestorId(UUID investorId);
}