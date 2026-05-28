package com.agriyield.weatherservice.infrastructure.adapter.outgoing.persistence;

import com.agriyield.weatherservice.application.port.outgoing.DroughtConditionRepositoryPort;
import com.agriyield.weatherservice.domain.model.DroughtCondition;
import com.agriyield.weatherservice.infrastructure.adapter.outgoing.persistence.mapper.WeatherEntityMapper;
import com.agriyield.weatherservice.infrastructure.repository.JpaDroughtConditionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DroughtConditionRepositoryAdapter implements DroughtConditionRepositoryPort {

    private final JpaDroughtConditionRepository jpaRepository;
    private final WeatherEntityMapper mapper;

    @Override
    public DroughtCondition save(DroughtCondition condition) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(condition)));
    }

    @Override
    public Optional<DroughtCondition> findByFarmId(UUID farmId) {
        return jpaRepository.findByFarmId(farmId).map(mapper::toDomain);
    }

    @Override
    public List<DroughtCondition> findAllTriggered() {
        return jpaRepository.findByIsTriggeredTrue()
                .stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public DroughtCondition findOrCreateByFarmId(UUID farmId) {
        return jpaRepository.findByFarmId(farmId)
                .map(mapper::toDomain)
                .orElseGet(() -> {
                    DroughtCondition newCondition = DroughtCondition.builder()
                            .id(UUID.randomUUID())
                            .farmId(farmId)
                            .consecutiveDryDays(0)
                            .droughtThresholdDays(30)
                            .isTriggered(false)
                            .lastChecked(OffsetDateTime.now())
                            .build();
                    return save(newCondition);
                });
    }
}
