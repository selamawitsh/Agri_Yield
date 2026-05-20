package com.agriyield.userservice.core.domain.model;

import com.agriyield.userservice.core.domain.enums.AccountStatus;
import com.agriyield.userservice.core.domain.enums.KycStatus;
import com.agriyield.userservice.core.domain.enums.PreferredLanguage;
import com.agriyield.userservice.core.domain.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    
    // Investor-specific fields
    private String riskTolerance;
    private String investmentGoal;
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
