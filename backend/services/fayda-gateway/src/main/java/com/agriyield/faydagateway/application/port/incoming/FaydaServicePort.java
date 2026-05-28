package com.agriyield.faydagateway.application.port.incoming;

import com.agriyield.faydagateway.domain.model.IdentityVerificationResult;
import com.agriyield.faydagateway.domain.model.KycData;

public interface FaydaServicePort {
    IdentityVerificationResult verifyIdentity(String faydaId, String phone, String fullName);
    KycData pullKycData(String faydaId);
}
