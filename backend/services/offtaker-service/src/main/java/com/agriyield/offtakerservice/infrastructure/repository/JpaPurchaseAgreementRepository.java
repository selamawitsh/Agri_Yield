package com.agriyield.offtakerservice.infrastructure.repository;

import com.agriyield.offtakerservice.infrastructure.adapter.outgoing.persistence.entity.PurchaseAgreementEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface JpaPurchaseAgreementRepository extends JpaRepository<PurchaseAgreementEntity, UUID> {
    Optional<PurchaseAgreementEntity> findByBidId(UUID bidId);
}
