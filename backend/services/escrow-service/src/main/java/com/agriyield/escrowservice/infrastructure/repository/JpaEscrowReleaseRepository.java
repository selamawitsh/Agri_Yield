package com.agriyield.escrowservice.infrastructure.repository;

import com.agriyield.escrowservice.infrastructure.adapter.outgoing.persistence.entity.EscrowReleaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JpaEscrowReleaseRepository extends JpaRepository<EscrowReleaseEntity, UUID> {

    List<EscrowReleaseEntity> findByEscrowAccountIdOrderByReleasedAtAsc(UUID escrowAccountId);
}