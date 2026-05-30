package com.agriyield.merchantservice.infrastructure.adapter.outgoing.persistence;

import com.agriyield.merchantservice.application.port.outgoing.MerchantProfileRepositoryPort;
import com.agriyield.merchantservice.domain.enums.SubscriptionTier;
import com.agriyield.merchantservice.domain.model.MerchantProfile;
import com.agriyield.merchantservice.infrastructure.adapter.outgoing.persistence.entity.MerchantProfileEntity;
import com.agriyield.merchantservice.infrastructure.repository.JpaMerchantProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MerchantProfileRepositoryAdapter implements MerchantProfileRepositoryPort {

    private final JpaMerchantProfileRepository jpaRepo;

    @Override
    public Optional<MerchantProfile> findByUserId(UUID userId) {
        return jpaRepo.findByUserId(userId).map(this::toDomain);
    }

    @Override
    public Optional<MerchantProfile> findById(UUID id) {
        return jpaRepo.findById(id).map(this::toDomain);
    }

    @Override
    public List<MerchantProfile> findAll() {
        return jpaRepo.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public MerchantProfile save(MerchantProfile profile) {
        return toDomain(jpaRepo.save(toEntity(profile)));
    }

    private MerchantProfile toDomain(MerchantProfileEntity e) {
        return MerchantProfile.builder()
                .id(e.getId())
                .userId(e.getUserId())
                .businessName(e.getBusinessName())
                .businessLicenseNumber(e.getBusinessLicenseNumber())
                .storeGpsLat(e.getStoreGpsLat() != null ? e.getStoreGpsLat().doubleValue() : null)
                .storeGpsLng(e.getStoreGpsLng() != null ? e.getStoreGpsLng().doubleValue() : null)
                .isPhysicallyVerified(Boolean.TRUE.equals(e.getIsPhysicallyVerified()))
                .physicallyVerifiedAt(e.getPhysicallyVerifiedAt())
                .verifiedByAgentId(e.getVerifiedByAgentId())
                .subscriptionTier(parseSubscriptionTier(e.getSubscriptionTier()))
                .telebirrAccount(e.getTelebirrAccount())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }

    private MerchantProfileEntity toEntity(MerchantProfile p) {
        return MerchantProfileEntity.builder()
                .id(p.getId())
                .userId(p.getUserId())
                .businessName(p.getBusinessName() != null ? p.getBusinessName() : "")
                .businessLicenseNumber(p.getBusinessLicenseNumber() != null ? p.getBusinessLicenseNumber() : "")
                .storeGpsLat(p.getStoreGpsLat() != null ? BigDecimal.valueOf(p.getStoreGpsLat()) : BigDecimal.ZERO)
                .storeGpsLng(p.getStoreGpsLng() != null ? BigDecimal.valueOf(p.getStoreGpsLng()) : BigDecimal.ZERO)
                .isPhysicallyVerified(p.isPhysicallyVerified())
                .physicallyVerifiedAt(p.getPhysicallyVerifiedAt())
                .verifiedByAgentId(p.getVerifiedByAgentId())
                .subscriptionTier(p.getSubscriptionTier() != null ? p.getSubscriptionTier().name() : "BASIC")
                .telebirrAccount(p.getTelebirrAccount())
                .isPremium(false)
                .build();
    }

    private SubscriptionTier parseSubscriptionTier(String value) {
        try {
            return value != null ? SubscriptionTier.valueOf(value) : SubscriptionTier.BASIC;
        } catch (IllegalArgumentException e) {
            return SubscriptionTier.BASIC;
        }
    }
}
