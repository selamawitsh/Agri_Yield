package com.agriyield.farmservice.infrastructure.adapter.outgoing.persistence;

import com.agriyield.farmservice.application.port.outgoing.InputNeedRepositoryPort;
import com.agriyield.farmservice.domain.model.InputNeed;
import com.agriyield.farmservice.infrastructure.adapter.outgoing.persistence.mapper.FarmEntityMapper;
import com.agriyield.farmservice.infrastructure.repository.JpaInputNeedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class InputNeedRepositoryAdapter implements InputNeedRepositoryPort {

    private final JpaInputNeedRepository jpaInputNeedRepository;
    private final FarmEntityMapper mapper;

    @Override
    public InputNeed save(InputNeed inputNeed) {
        return mapper.toDomain(jpaInputNeedRepository.save(mapper.toEntity(inputNeed)));
    }

    @Override
    public Optional<InputNeed> findById(UUID inputNeedId) {
        return jpaInputNeedRepository.findById(inputNeedId).map(mapper::toDomain);
    }

    @Override
    public Optional<InputNeed> findByFarmIdAndCropCycleId(UUID farmId, UUID cropCycleId) {
        return jpaInputNeedRepository.findByFarmIdAndCropCycleId(farmId, cropCycleId)
            .map(mapper::toDomain);
    }

    @Override
    public List<InputNeed> findAllByFarmId(UUID farmId) {
        return jpaInputNeedRepository.findByFarmId(farmId)
            .stream().map(mapper::toDomain).collect(Collectors.toList());
    }
}
