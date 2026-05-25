package com.agriyield.merchantservice.presentation.dto.response;

import com.agriyield.merchantservice.domain.model.MerchantProfile;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class MerchantProfileResponse {
    private UUID id;
    private UUID userId;
    private String businessName;
    private String businessLicenseNumber;
    private Double storeGpsLat;
    private Double storeGpsLng;
    private boolean isPhysicallyVerified;
    private OffsetDateTime physicallyVerifiedAt;
    private String subscriptionTier;
    private String telebirrAccount;
    private OffsetDateTime createdAt;

    public static MerchantProfileResponse from(MerchantProfile domain) {
        return MerchantProfileResponse.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .businessName(domain.getBusinessName())
                .businessLicenseNumber(domain.getBusinessLicenseNumber())
                .storeGpsLat(domain.getStoreGpsLat())
                .storeGpsLng(domain.getStoreGpsLng())
                .isPhysicallyVerified(domain.isPhysicallyVerified())
                .physicallyVerifiedAt(domain.getPhysicallyVerifiedAt())
                .subscriptionTier(domain.getSubscriptionTier().name())
                .telebirrAccount(domain.getTelebirrAccount())
                .createdAt(domain.getCreatedAt())
                .build();
    }
}
