package com.agriyield.userservice.infrastructure.adapter.outgoing.persistence.mapper;

import com.agriyield.userservice.core.domain.enums.*;
import com.agriyield.userservice.core.domain.model.Otp;
import com.agriyield.userservice.core.domain.model.User;
import com.agriyield.userservice.infrastructure.adapter.outgoing.persistence.entity.OtpEntity;
import com.agriyield.userservice.infrastructure.adapter.outgoing.persistence.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class EntityDomainMapper {
    
    public User toDomain(UserEntity entity) {
        if (entity == null) return null;
        
        return User.builder()
            .id(entity.getId())
            .phone(entity.getPhone())
            .email(entity.getEmail())
            .faydaId(entity.getFaydaId())
            .passwordHash(entity.getPasswordHash())
            .role(UserRole.fromValue(entity.getRole()))
            .kycStatus(KycStatus.valueOf(entity.getKycStatus()))
            .accountStatus(AccountStatus.valueOf(entity.getAccountStatus()))
            .preferredLanguage(PreferredLanguage.fromCode(entity.getPreferredLanguage()))
            .faydaVerifiedAt(entity.getFaydaVerifiedAt())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .build();
    }
    
    public UserEntity toEntity(User domain) {
        if (domain == null) return null;
        
        return UserEntity.builder()
            .id(domain.getId())
            .phone(domain.getPhone())
            .email(domain.getEmail())
            .faydaId(domain.getFaydaId())
            .passwordHash(domain.getPasswordHash())
            .role(domain.getRole().getValue())
            .kycStatus(domain.getKycStatus().getValue())
            .accountStatus(domain.getAccountStatus().getValue())
            .preferredLanguage(domain.getPreferredLanguage().getCode())
            .faydaVerifiedAt(domain.getFaydaVerifiedAt())
            .createdAt(domain.getCreatedAt())
            .updatedAt(domain.getUpdatedAt())
            .build();
    }
    
    public Otp toDomain(OtpEntity entity) {
        if (entity == null) return null;
        
        return Otp.builder()
            .id(entity.getId())
            .userId(entity.getUserId())
            .otpCode(entity.getOtpCode())
            .purpose(OtpPurpose.valueOf(entity.getPurpose()))
            .expiresAt(entity.getExpiresAt())
            .usedAt(entity.getUsedAt())
            .createdAt(entity.getCreatedAt())
            .build();
    }
    
    public OtpEntity toEntity(Otp domain) {
        if (domain == null) return null;
        
        return OtpEntity.builder()
            .id(domain.getId())
            .userId(domain.getUserId())
            .otpCode(domain.getOtpCode())
            .purpose(domain.getPurpose().getValue())
            .expiresAt(domain.getExpiresAt())
            .usedAt(domain.getUsedAt())
            .createdAt(domain.getCreatedAt())
            .build();
    }
}
