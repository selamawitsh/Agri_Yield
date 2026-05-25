package com.agriyield.merchantservice.application.port.outgoing;

import com.agriyield.merchantservice.domain.model.MerchantProfile;

import java.util.Optional;
import java.util.UUID;

public interface UserServicePort {
    Optional<MerchantProfile> getMerchantProfileByUserId(UUID userId);
    Optional<MerchantProfile> getMerchantProfileById(UUID merchantId);
    boolean merchantExists(UUID merchantId);
}
