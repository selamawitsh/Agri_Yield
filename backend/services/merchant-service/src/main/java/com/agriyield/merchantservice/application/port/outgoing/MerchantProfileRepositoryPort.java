package com.agriyield.merchantservice.application.port.outgoing;

import com.agriyield.merchantservice.domain.model.MerchantProfile;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MerchantProfileRepositoryPort {
    Optional<MerchantProfile> findByUserId(UUID userId);
    Optional<MerchantProfile> findById(UUID id);
    List<MerchantProfile> findAll();
    MerchantProfile save(MerchantProfile profile);
}
