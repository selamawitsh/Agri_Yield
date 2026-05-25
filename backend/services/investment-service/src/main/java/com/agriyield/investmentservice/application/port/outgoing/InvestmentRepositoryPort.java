package com.agriyield.investmentservice.application.port.outgoing;

import com.agriyield.investmentservice.domain.model.Investment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InvestmentRepositoryPort {

    Investment save(Investment investment);

    Optional<Investment> findById(UUID id);

    List<Investment> findByInvestorId(UUID investorId);

    Optional<Investment> findByFarmId(UUID farmId);

    List<Investment> findAllByFarmId(UUID farmId);

    boolean existsByInvestorIdAndFarmId(UUID investorId, UUID farmId);
}
