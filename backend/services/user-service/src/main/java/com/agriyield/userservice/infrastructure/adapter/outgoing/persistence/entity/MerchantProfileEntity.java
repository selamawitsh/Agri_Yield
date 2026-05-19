package com.agriyield.userservice.infrastructure.adapter.outgoing.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "merchant_profiles")
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
    
    @Column(name = "business_name", nullable = false, length = 255)
    private String businessName;
    
    @Column(name = "business_license_number", nullable = false, unique = true, length = 100)
    private String businessLicenseNumber;
    
    @Column(name = "store_gps_lat", nullable = false, precision = 10, scale = 7)
    private BigDecimal storeGpsLat;
    
    @Column(name = "store_gps_lng", nullable = false, precision = 10, scale = 7)
    private BigDecimal storeGpsLng;
    
    @Column(name = "is_physically_verified", nullable = false)
    private Boolean isPhysicallyVerified;
    
    @Column(name = "physically_verified_at")
    private LocalDateTime physicallyVerifiedAt;
    
    @Column(name = "verified_by_agent_id")
    private UUID verifiedByAgentId;
    
    @Column(name = "subscription_tier", nullable = false, length = 20)
    private String subscriptionTier;
    
    @Column(name = "cbe_account", length = 30)
    private String cbeAccount;
    
    @Column(name = "telebirr_account", length = 30)
    private String telebirrAccount;
    
    @Column(name = "is_premium", nullable = false)
    private Boolean isPremium;
    
    @Column(name = "premium_expiry")
    private LocalDateTime premiumExpiry;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
