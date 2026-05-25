package com.agriyield.userservice.infrastructure.adapter.outgoing.persistence;

import com.agriyield.userservice.application.port.outgoing.InvestorProfileRepositoryPort;
import com.agriyield.userservice.infrastructure.adapter.outgoing.persistence.entity.InvestorProfileEntity;
import com.agriyield.userservice.infrastructure.repository.JpaInvestorProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class InvestorProfileRepositoryAdapter
        implements InvestorProfileRepositoryPort {

    private final JpaInvestorProfileRepository jpaRepo;

    @Override
    public void createDefaultProfile(UUID userId) {

        InvestorProfileEntity profile = InvestorProfileEntity.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .riskTolerance("MODERATE")
                .investmentGoal("")
                .totalInvestedEtb(BigDecimal.ZERO)
                .totalReturnedEtb(BigDecimal.ZERO)
                .build();

        jpaRepo.save(profile);

        log.info("Created investor profile for user: {}", userId);
    }

    @Override
    public void updateRiskTolerance(UUID userId, String riskTolerance) {

        jpaRepo.findByUserId(userId).ifPresent(profile -> {
            profile.setRiskTolerance(riskTolerance);
            jpaRepo.save(profile);
        });
    }

    @Override
    public void updateInvestmentGoal(UUID userId, String investmentGoal) {

        jpaRepo.findByUserId(userId).ifPresent(profile -> {
            profile.setInvestmentGoal(investmentGoal);
            jpaRepo.save(profile);
        });
    }
}