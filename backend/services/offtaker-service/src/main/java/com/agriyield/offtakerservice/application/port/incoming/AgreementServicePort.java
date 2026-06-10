package com.agriyield.offtakerservice.application.port.incoming;

import com.agriyield.offtakerservice.domain.model.PurchaseAgreement;

import java.util.UUID;

public interface AgreementServicePort {
    PurchaseAgreement getById(UUID agreementId);
    PurchaseAgreement signAgreement(UUID agreementId, UUID signingUserId, String role);
}
