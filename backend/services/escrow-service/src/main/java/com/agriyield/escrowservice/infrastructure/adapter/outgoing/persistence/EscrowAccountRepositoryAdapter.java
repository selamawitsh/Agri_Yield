package com.agriyield.escrowservice.infrastructure.adapter.outgoing.persistence;

import com.agriyield.escrowservice.application.port.outgoing.EscrowAccountRepositoryPort;
import com.agriyield.escrowservice.domain.model.EscrowAccount;
import com.agriyield.escrowservice.infrastructure.adapter.outgoing.persistence.mapper.EscrowEntityMapper;
import com.agriyield.escrowservice.infrastructure.repository.JpaEscrowAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EscrowAccountRepositoryAdapter implements EscrowAccountRepositoryPort {

    private final JpaEscrowAccountRepository jpaRepository;
    private final EscrowEntityMapper mapper;

    @Override
    public EscrowAccount save(EscrowAccount escrowAccount) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(escrowAccount)));
    }

    @Override
    public Optional<EscrowAccount> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<EscrowAccount> findByInvestmentId(UUID investmentId) {
        return jpaRepository.findByInvestmentId(investmentId).map(mapper::toDomain);
    }

    @Override
    public List<EscrowAccount> findByFarmerId(UUID farmerId) {
        return jpaRepository.findByFarmerId(farmerId)
                .stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<EscrowAccount> findByInvestorId(UUID investorId) {
        return jpaRepository.findByInvestorId(investorId)
                .stream().map(mapper::toDomain).collect(Collectors.toList());
    }
}