package com.agriyield.fraudservice.infrastructure.adapter.outgoing.persistence;

import com.agriyield.fraudservice.application.port.outgoing.FraudAlertRepositoryPort;
import com.agriyield.fraudservice.domain.model.FraudAlert;
import com.agriyield.fraudservice.infrastructure.adapter.outgoing.persistence.mapper.FraudEntityMapper;
import com.agriyield.fraudservice.infrastructure.repository.JpaFraudAlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FraudAlertRepositoryAdapter implements FraudAlertRepositoryPort {

    private final JpaFraudAlertRepository jpaRepository;
    private final FraudEntityMapper mapper;

    @Override
    public FraudAlert save(FraudAlert alert) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(alert)));
    }

    @Override
    public Optional<FraudAlert> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<FraudAlert> findAll(String severity, boolean unresolvedOnly,
                                     int page, int size) {
        return jpaRepository.findWithFilters(severity, unresolvedOnly,
                PageRequest.of(page, size))
            .stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<FraudAlert> findByEntityId(UUID entityId, String entityType) {
        return jpaRepository
            .findByEntityIdAndEntityTypeOrderByCreatedAtDesc(entityId, entityType)
            .stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public long countUnresolvedByEntityId(UUID entityId) {
        return jpaRepository.countByEntityIdAndResolvedFalse(entityId);
    }
}
