package com.agriyield.userservice.domain.model;

import com.agriyield.userservice.domain.enums.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private UUID id;
    private String phone;
    private String email;
    private String faydaId;
    private String passwordHash;
    private UserRole role;
    private KycStatus kycStatus;
    private AccountStatus accountStatus;
    private LocalDateTime faydaVerifiedAt;
    private PreferredLanguage preferredLanguage;
    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Investor-specific
    private String riskTolerance;
    private String investmentGoal;

    // Farmer-specific
    private Integer agriScore;

    public boolean isActive() {
        return this.accountStatus == AccountStatus.ACTIVE;
    }

    public boolean isKycVerified() {
        return this.kycStatus == KycStatus.VERIFIED;
    }

    public void activate() {
        this.accountStatus = AccountStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    public void suspend() {
        this.accountStatus = AccountStatus.SUSPENDED;
        this.updatedAt = LocalDateTime.now();
    }

    public void verifyKyc() {
        this.kycStatus = KycStatus.VERIFIED;
        this.faydaVerifiedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void rejectKyc() {
        this.kycStatus = KycStatus.REJECTED;
        this.updatedAt = LocalDateTime.now();
    }
}
