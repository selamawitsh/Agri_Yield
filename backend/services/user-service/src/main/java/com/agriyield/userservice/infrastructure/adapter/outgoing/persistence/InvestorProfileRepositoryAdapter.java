package com.agriyield.userservice.infrastructure.adapter.outgoing.persistence;

import com.agriyield.userservice.core.port.outgoing.InvestorProfileRepositoryPort;
import com.agriyield.userservice.infrastructure.adapter.outgoing.persistence.entity.InvestorProfileEntity;
import com.agriyield.userservice.infrastructure.repository.JpaInvestorProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class InvestorProfileRepositoryAdapter implements InvestorProfileRepositoryPort {
    
    private final JpaInvestorProfileRepository jpaRepository;
    
    @Override
    public void createDefaultProfile(UUID userId) {
        // Check if profile already exists
        if (jpaRepository.findByUserId(userId).isPresent()) {
            return;
        }
        
        InvestorProfileEntity profile = InvestorProfileEntity.builder()
            .id(UUID.randomUUID())
            .userId(userId)
            .agriScore(50)
            .totalSeasonsCompleted(0)
            .preferredLanguage("am")
            .riskTolerance("MODERATE")
            .totalInvestedEtb(BigDecimal.ZERO)
            .totalReturnedEtb(BigDecimal.ZERO)
            .build();
        jpaRepository.save(profile);
    }
    
    @Override
    public void updateRiskTolerance(UUID userId, String riskTolerance) {
        jpaRepository.findByUserId(userId).ifPresent(profile -> {
            profile.setRiskTolerance(riskTolerance);
            jpaRepository.save(profile);
        });
    }
    
    @Override
    public void updateInvestmentGoal(UUID userId, String investmentGoal) {
        jpaRepository.findByUserId(userId).ifPresent(profile -> {
            profile.setInvestmentGoal(investmentGoal);
            jpaRepository.save(profile);
        });
    }
}
