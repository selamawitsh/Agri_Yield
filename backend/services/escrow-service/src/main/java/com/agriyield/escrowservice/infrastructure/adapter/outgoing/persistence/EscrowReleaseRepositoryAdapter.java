package com.agriyield.escrowservice.infrastructure.adapter.outgoing.persistence;

import com.agriyield.escrowservice.application.port.outgoing.EscrowReleaseRepositoryPort;
import com.agriyield.escrowservice.domain.model.EscrowRelease;
import com.agriyield.escrowservice.infrastructure.adapter.outgoing.persistence.mapper.EscrowEntityMapper;
import com.agriyield.escrowservice.infrastructure.repository.JpaEscrowReleaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EscrowReleaseRepositoryAdapter implements EscrowReleaseRepositoryPort {

    private final JpaEscrowReleaseRepository jpaRepository;
    private final EscrowEntityMapper mapper;

    @Override
    public EscrowRelease save(EscrowRelease release) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(release)));
    }

    @Override
    public List<EscrowRelease> findByEscrowAccountId(UUID escrowAccountId) {
        return jpaRepository
                .findByEscrowAccountIdOrderByReleasedAtAsc(escrowAccountId)
                .stream().map(mapper::toDomain).collect(Collectors.toList());
    }
}