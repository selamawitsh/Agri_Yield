package com.agriyield.escrowservice.infrastructure.repository;

import com.agriyield.escrowservice.infrastructure.adapter.outgoing.persistence.entity.EscrowTransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JpaEscrowTransactionRepository extends JpaRepository<EscrowTransactionEntity, UUID> {

    List<EscrowTransactionEntity> findByEscrowAccountIdOrderByCreatedAtAsc(UUID escrowAccountId);
}