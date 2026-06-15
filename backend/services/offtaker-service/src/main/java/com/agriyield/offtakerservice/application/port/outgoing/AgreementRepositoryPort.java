package com.agriyield.offtakerservice.application.port.outgoing;

import com.agriyield.offtakerservice.domain.model.PurchaseAgreement;

import java.util.Optional;
import java.util.UUID;

public interface AgreementRepositoryPort {
    PurchaseAgreement save(PurchaseAgreement agreement);
    Optional<PurchaseAgreement> findById(UUID id);

    /**
     * FIX: findByBidId was missing from the port. AgreementServiceImpl already
     * uses bidRepository.findByBidId() (non-Optional, raw throw) — this port method
     * is the clean version used for looking up an agreement by bid context.
     */
    Optional<PurchaseAgreement> findByBidId(UUID bidId);
}