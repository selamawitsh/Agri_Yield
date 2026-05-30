package com.agriyield.merchantservice.infrastructure.adapter.outgoing.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "merchant_profiles", schema = "merchant_service")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantProfileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    @Column(name = "business_name", nullable = false)
    private String businessName;

    @Column(name = "business_license_number", nullable = false, unique = true)
    private String businessLicenseNumber;

    @Column(name = "kebele_code")
    private String kebeleCode;

    @Column(name = "store_gps_lat", precision = 10, scale = 7)
    private BigDecimal storeGpsLat;

    @Column(name = "store_gps_lng", precision = 10, scale = 7)
    private BigDecimal storeGpsLng;

    @Column(name = "is_physically_verified", nullable = false)
    private Boolean isPhysicallyVerified;

    @Column(name = "physically_verified_at")
    private OffsetDateTime physicallyVerifiedAt;

    @Column(name = "verified_by_agent_id")
    private UUID verifiedByAgentId;

    @Column(name = "subscription_tier", nullable = false)
    private String subscriptionTier;

    @Column(name = "cbe_account")
    private String cbeAccount;

    @Column(name = "telebirr_account")
    private String telebirrAccount;

    @Column(name = "is_premium", nullable = false)
    private Boolean isPremium;

    @Column(name = "premium_expiry")
    private OffsetDateTime premiumExpiry;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;
}
