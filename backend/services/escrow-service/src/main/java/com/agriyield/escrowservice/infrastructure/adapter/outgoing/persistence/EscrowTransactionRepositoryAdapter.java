package com.agriyield.escrowservice.infrastructure.adapter.outgoing.persistence;

import com.agriyield.escrowservice.application.port.outgoing.EscrowTransactionRepositoryPort;
import com.agriyield.escrowservice.domain.model.EscrowTransaction;
import com.agriyield.escrowservice.infrastructure.adapter.outgoing.persistence.mapper.EscrowEntityMapper;
import com.agriyield.escrowservice.infrastructure.repository.JpaEscrowTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EscrowTransactionRepositoryAdapter implements EscrowTransactionRepositoryPort {

    private final JpaEscrowTransactionRepository jpaRepository;
    private final EscrowEntityMapper mapper;

    @Override
    public EscrowTransaction save(EscrowTransaction transaction) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(transaction)));
    }

    @Override
    public List<EscrowTransaction> findByEscrowAccountId(UUID escrowAccountId) {
        return jpaRepository
                .findByEscrowAccountIdOrderByCreatedAtAsc(escrowAccountId)
                .stream().map(mapper::toDomain).collect(Collectors.toList());
    }
}