package com.agriyield.fraudservice.infrastructure.adapter.outgoing.persistence;

import com.agriyield.fraudservice.application.port.outgoing.GpsLogRepositoryPort;
import com.agriyield.fraudservice.domain.model.GpsLog;
import com.agriyield.fraudservice.infrastructure.adapter.outgoing.persistence.mapper.FraudEntityMapper;
import com.agriyield.fraudservice.infrastructure.repository.JpaGpsLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GpsLogRepositoryAdapter implements GpsLogRepositoryPort {

    private final JpaGpsLogRepository jpaRepository;
    private final FraudEntityMapper mapper;

    @Override
    public GpsLog save(GpsLog log) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(log)));
    }

    @Override
    public Optional<GpsLog> findLatestByEntityId(UUID entityId, String entityType) {
        return jpaRepository
            .findFirstByEntityIdAndEntityTypeOrderByRecordedAtDesc(entityId, entityType)
            .map(mapper::toDomain);
    }

    @Override
    public List<GpsLog> findRecentByEntityId(UUID entityId, String entityType, int limit) {
        return jpaRepository
            .findByEntityIdAndEntityTypeOrderByRecordedAtDesc(
                entityId, entityType, PageRequest.of(0, limit))
            .stream().map(mapper::toDomain).collect(Collectors.toList());
    }
}
