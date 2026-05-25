package com.agriyield.merchantservice.domain.model;

import com.agriyield.merchantservice.domain.enums.SubscriptionTier;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantProfile {
    private UUID id;
    private UUID userId;
    private String businessName;
    private String businessLicenseNumber;
    private Double storeGpsLat;
    private Double storeGpsLng;
    private boolean isPhysicallyVerified;
    private OffsetDateTime physicallyVerifiedAt;
    private UUID verifiedByAgentId;
    private SubscriptionTier subscriptionTier;
    private String telebirrAccount;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
