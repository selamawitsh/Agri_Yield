package com.agriyield.offtakerservice.application.port.outgoing;

import com.agriyield.offtakerservice.domain.model.PurchaseAgreement;

import java.util.Optional;
import java.util.UUID;

public interface AgreementRepositoryPort {
    PurchaseAgreement save(PurchaseAgreement agreement);
    Optional<PurchaseAgreement> findById(UUID id);
    Optional<PurchaseAgreement> findByBidId(UUID bidId);
}
