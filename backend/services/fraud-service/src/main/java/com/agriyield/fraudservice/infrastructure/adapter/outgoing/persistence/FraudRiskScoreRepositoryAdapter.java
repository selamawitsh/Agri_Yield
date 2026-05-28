package com.agriyield.fraudservice.infrastructure.adapter.outgoing.persistence;

import com.agriyield.fraudservice.application.port.outgoing.FraudRiskScoreRepositoryPort;
import com.agriyield.fraudservice.domain.model.FraudRiskScore;
import com.agriyield.fraudservice.infrastructure.adapter.outgoing.persistence.mapper.FraudEntityMapper;
import com.agriyield.fraudservice.infrastructure.repository.JpaFraudRiskScoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class FraudRiskScoreRepositoryAdapter implements FraudRiskScoreRepositoryPort {

    private final JpaFraudRiskScoreRepository jpaRepository;
    private final FraudEntityMapper mapper;

    @Override
    public FraudRiskScore save(FraudRiskScore score) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(score)));
    }

    @Override
    public Optional<FraudRiskScore> findByEntityId(UUID entityId, String entityType) {
        return jpaRepository.findByEntityIdAndEntityType(entityId, entityType)
            .map(mapper::toDomain);
    }
}
