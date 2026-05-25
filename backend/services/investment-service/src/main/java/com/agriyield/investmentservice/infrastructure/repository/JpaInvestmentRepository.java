package com.agriyield.investmentservice.infrastructure.repository;

import com.agriyield.investmentservice.infrastructure.adapter.outgoing.persistence.entity.InvestmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaInvestmentRepository extends JpaRepository<InvestmentEntity, UUID> {

    List<InvestmentEntity> findByInvestorId(UUID investorId);

    Optional<InvestmentEntity> findFirstByFarmId(UUID farmId);

    List<InvestmentEntity> findAllByFarmId(UUID farmId);

    boolean existsByInvestorIdAndFarmId(UUID investorId, UUID farmId);
}
