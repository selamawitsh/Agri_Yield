package com.agriyield.investmentservice.infrastructure.adapter.outgoing.persistence;

import com.agriyield.investmentservice.application.port.outgoing.InvestmentRepositoryPort;
import com.agriyield.investmentservice.domain.model.Investment;
import com.agriyield.investmentservice.infrastructure.adapter.outgoing.persistence.mapper.InvestmentEntityMapper;
import com.agriyield.investmentservice.infrastructure.repository.JpaInvestmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class InvestmentRepositoryAdapter implements InvestmentRepositoryPort {

    private final JpaInvestmentRepository jpaRepository;
    private final InvestmentEntityMapper mapper;

    @Override
    public Investment save(Investment investment) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(investment)));
    }

    @Override
    public Optional<Investment> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Investment> findByInvestorId(UUID investorId) {
        return jpaRepository.findByInvestorId(investorId)
            .stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<Investment> findByFarmId(UUID farmId) {
        return jpaRepository.findFirstByFarmId(farmId).map(mapper::toDomain);
    }

    @Override
    public List<Investment> findAllByFarmId(UUID farmId) {
        return jpaRepository.findAllByFarmId(farmId)
            .stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public boolean existsByInvestorIdAndFarmId(UUID investorId, UUID farmId) {
        return jpaRepository.existsByInvestorIdAndFarmId(investorId, farmId);
    }
}
