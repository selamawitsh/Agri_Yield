package com.agriyield.faydagateway.application.port.outgoing;

import com.agriyield.faydagateway.domain.model.IdentityVerificationResult;
import com.agriyield.faydagateway.domain.model.KycData;

public interface FaydaApiPort {
    IdentityVerificationResult verifyIdentity(String faydaId, String phone, String fullName);
    KycData pullKycData(String faydaId);
}
