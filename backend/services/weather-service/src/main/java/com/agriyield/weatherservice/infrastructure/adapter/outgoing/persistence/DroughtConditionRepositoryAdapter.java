package com.agriyield.weatherservice.infrastructure.adapter.outgoing.persistence;

import com.agriyield.weatherservice.application.port.outgoing.DroughtConditionRepositoryPort;
import com.agriyield.weatherservice.domain.model.DroughtCondition;
import com.agriyield.weatherservice.infrastructure.adapter.outgoing.persistence.entity.DroughtConditionEntity;
import com.agriyield.weatherservice.infrastructure.adapter.outgoing.persistence.mapper.WeatherEntityMapper;
import com.agriyield.weatherservice.infrastructure.repository.JpaDroughtConditionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class DroughtConditionRepositoryAdapter implements DroughtConditionRepositoryPort {

    private final JpaDroughtConditionRepository jpaRepository;
    private final WeatherEntityMapper mapper;

    @Override
    public DroughtCondition save(DroughtCondition condition) {
        // FIX: guard — never save a domain object with a null ID.
        // This would cause @GeneratedValue to assign a new ID on every call
        // which broke the unique constraint on farm_id.
        if (condition.getId() == null) {
            throw new IllegalStateException(
                    "DroughtCondition must have an ID before saving. " +
                            "Assign via UUID.randomUUID() in findOrCreateByFarmId.");
        }
        DroughtConditionEntity entity = mapper.toEntity(condition);
        DroughtConditionEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<DroughtCondition> findByFarmId(UUID farmId) {
        return jpaRepository.findByFarmId(farmId)
                .map(mapper::toDomain);
    }

    @Override
    public List<DroughtCondition> findAllTriggered() {
        return jpaRepository.findByIsTriggeredTrue()
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public DroughtCondition findOrCreateByFarmId(UUID farmId) {
        return jpaRepository.findByFarmId(farmId)
                .map(entity -> {
                    log.debug("Found existing drought condition for farm: {}", farmId);
                    return mapper.toDomain(entity);
                })
                .orElseGet(() -> {
                    log.info("Creating new drought condition for farm: {}", farmId);

                    // FIX: assign the ID here in the domain object so that:
                    // 1. The entity gets the same ID every time for this farm
                    // 2. Hibernate sees a non-null ID and uses merge/save correctly
                    // 3. No second INSERT attempt happens for the same farm_id
                    DroughtCondition newCondition = DroughtCondition.builder()
                            .id(UUID.randomUUID())
                            .farmId(farmId)
                            .consecutiveDryDays(0)
                            .droughtThresholdDays(30)
                            .isTriggered(false)
                            .lastChecked(OffsetDateTime.now())
                            .build();

                    // Save directly via JPA to avoid any double-save path
                    DroughtConditionEntity entity = mapper.toEntity(newCondition);
                    DroughtConditionEntity saved = jpaRepository.save(entity);
                    log.info("Created drought condition id={} for farm={}",
                            saved.getId(), farmId);
                    return mapper.toDomain(saved);
                });
    }
}