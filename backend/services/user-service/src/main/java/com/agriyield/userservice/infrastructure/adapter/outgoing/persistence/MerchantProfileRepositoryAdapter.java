package com.agriyield.userservice.infrastructure.adapter.outgoing.persistence;

import com.agriyield.userservice.application.port.outgoing.MerchantProfileRepositoryPort;
import com.agriyield.userservice.infrastructure.adapter.outgoing.persistence.entity.MerchantProfileEntity;
import com.agriyield.userservice.infrastructure.repository.JpaMerchantProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
                .storeGpsLat(BigDecimal.ZERO)
                .storeGpsLng(BigDecimal.ZERO)
                .isPhysicallyVerified(false)
                .isPremium(false)
                .subscriptionTier("BASIC")
                .telebirrAccount("")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        jpaRepo.save(profile);

        log.info("Created default merchant profile for: {}", userId);
    }

    @Override
    public void createMerchantProfile(
            UUID userId,
            String businessName,
            String businessLicenseNumber,
            BigDecimal storeGpsLat,
            BigDecimal storeGpsLng,
            String telebirrAccount,
            String kebeleCode
    ) {

        MerchantProfileEntity profile = MerchantProfileEntity.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .businessName(businessName)
                .businessLicenseNumber(businessLicenseNumber)
                .storeGpsLat(storeGpsLat)
                .storeGpsLng(storeGpsLng)
                .telebirrAccount(telebirrAccount)
                .kebeleCode(kebeleCode)
                .isPhysicallyVerified(false)
                .isPremium(false)
                .subscriptionTier("BASIC")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        jpaRepo.save(profile);

        log.info("Created merchant profile for: {}", userId);
    }
}