package com.agriyield.userservice.infrastructure.adapter.outgoing.persistence;

import com.agriyield.userservice.core.port.outgoing.MerchantProfileRepositoryPort;
import com.agriyield.userservice.infrastructure.adapter.outgoing.persistence.entity.MerchantProfileEntity;
import com.agriyield.userservice.infrastructure.repository.JpaMerchantProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MerchantProfileRepositoryAdapter implements MerchantProfileRepositoryPort {
    
    private final JpaMerchantProfileRepository jpaRepository;
    
    @Override
    public void createDefaultProfile(UUID userId) {
        MerchantProfileEntity profile = MerchantProfileEntity.builder()
            .id(UUID.randomUUID())
            .userId(userId)
            .businessName("Pending Setup")
            .businessLicenseNumber("PENDING_" + UUID.randomUUID().toString().substring(0, 8))
            .storeGpsLat(BigDecimal.ZERO)
            .storeGpsLng(BigDecimal.ZERO)
            .isPhysicallyVerified(false)
            .subscriptionTier("BASIC")
            .isPremium(false)
            .build();
        jpaRepository.save(profile);
    }
}
