package com.agriyield.userservice.infrastructure.adapter.outgoing.persistence;

import com.agriyield.userservice.application.port.outgoing.MerchantProfileRepositoryPort;
import com.agriyield.userservice.infrastructure.adapter.outgoing.persistence.entity.MerchantProfileEntity;
import com.agriyield.userservice.infrastructure.repository.JpaMerchantProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class MerchantProfileRepositoryAdapter
        implements MerchantProfileRepositoryPort {

    private final JpaMerchantProfileRepository jpaRepo;

    @Override
    public void createDefaultProfile(UUID userId) {
        MerchantProfileEntity profile = MerchantProfileEntity.builder()
            .id(UUID.randomUUID())
            .userId(userId)
            .businessName("")
            .businessLicenseNumber("")
            .isPhysicallyVerified(false)
            .subscriptionTier("BASIC")
            .telebirrAccount("")
            .build();
        jpaRepo.save(profile);
        log.info("Created merchant profile for: {}", userId);
    }
}
