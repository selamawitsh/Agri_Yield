package com.agriyield.userservice.infrastructure.adapter.outgoing.persistence.mapper;

import com.agriyield.userservice.domain.enums.*;
import com.agriyield.userservice.domain.model.*;
import com.agriyield.userservice.infrastructure.adapter.outgoing.persistence.entity.*;
import org.springframework.stereotype.Component;

@Component
public class EntityDomainMapper {

    public User toDomain(UserEntity e) {
        if (e == null) return null;
        return User.builder()
            .id(e.getId())
            .phone(e.getPhone())
            .email(e.getEmail())
            .faydaId(e.getFaydaId())
            .passwordHash(e.getPasswordHash())
            .role(UserRole.fromValue(e.getRole()))
            .kycStatus(KycStatus.valueOf(e.getKycStatus()))
            .accountStatus(AccountStatus.valueOf(e.getAccountStatus()))
            .faydaVerifiedAt(e.getFaydaVerifiedAt())
            .preferredLanguage(PreferredLanguage.fromCode(
                e.getPreferredLanguage()))
            .lastLoginAt(e.getLastLoginAt())
            .riskTolerance(e.getRiskTolerance())
            .investmentGoal(e.getInvestmentGoal())
            .agriScore(e.getAgriScore())
            .createdAt(e.getCreatedAt())
            .updatedAt(e.getUpdatedAt())
            .build();
    }

    public UserEntity toEntity(User d) {
        if (d == null) return null;
        return UserEntity.builder()
            .id(d.getId())
            .phone(d.getPhone())
            .email(d.getEmail())
            .faydaId(d.getFaydaId())
            .passwordHash(d.getPasswordHash())
            .role(d.getRole().getValue())
            .kycStatus(d.getKycStatus().getValue())
            .accountStatus(d.getAccountStatus().getValue())
            .faydaVerifiedAt(d.getFaydaVerifiedAt())
            .preferredLanguage(d.getPreferredLanguage().getCode())
            .lastLoginAt(d.getLastLoginAt())
            .riskTolerance(d.getRiskTolerance())
            .investmentGoal(d.getInvestmentGoal())
            .agriScore(d.getAgriScore())
            .createdAt(d.getCreatedAt())
            .updatedAt(d.getUpdatedAt())
            .build();
    }

    public Otp toDomain(OtpEntity e) {
        if (e == null) return null;
        return Otp.builder()
            .id(e.getId())
            .userId(e.getUserId())
            .otpCode(e.getOtpCode())
            .purpose(OtpPurpose.valueOf(e.getPurpose()))
            .expiresAt(e.getExpiresAt())
            .usedAt(e.getUsedAt())
            .createdAt(e.getCreatedAt())
            .build();
    }

    public OtpEntity toEntity(Otp d) {
        if (d == null) return null;
        return OtpEntity.builder()
            .id(d.getId())
            .userId(d.getUserId())
            .otpCode(d.getOtpCode())
            .purpose(d.getPurpose().getValue())
            .expiresAt(d.getExpiresAt())
            .usedAt(d.getUsedAt())
            .createdAt(d.getCreatedAt())
            .build();
    }

    public BankAccount toDomain(BankAccountEntity e) {
        if (e == null) return null;
        return BankAccount.builder()
            .id(e.getId())
            .userId(e.getUserId())
            .accountType(e.getAccountType())
            .accountNumber(e.getAccountNumber())
            .accountHolderName(e.getAccountHolderName())
            .isVerified(e.getIsVerified())
            .isDefault(e.getIsDefault())
            .verifiedAt(e.getVerifiedAt())
            .createdAt(e.getCreatedAt())
            .build();
    }

    public BankAccountEntity toEntity(BankAccount d) {
        if (d == null) return null;
        return BankAccountEntity.builder()
            .id(d.getId())
            .userId(d.getUserId())
            .accountType(d.getAccountType())
            .accountNumber(d.getAccountNumber())
            .accountHolderName(d.getAccountHolderName())
            .isVerified(d.getIsVerified())
            .isDefault(d.getIsDefault())
            .verifiedAt(d.getVerifiedAt())
            .createdAt(d.getCreatedAt())
            .build();
    }
}
