package com.agriyield.farmservice.infrastructure.adapter.outgoing.persistence;

import com.agriyield.farmservice.application.port.outgoing.CropCycleRepositoryPort;
import com.agriyield.farmservice.domain.model.CropCycle;
import com.agriyield.farmservice.infrastructure.adapter.outgoing.persistence.mapper.FarmEntityMapper;
import com.agriyield.farmservice.infrastructure.repository.JpaCropCycleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CropCycleRepositoryAdapter implements CropCycleRepositoryPort {

    private final JpaCropCycleRepository jpaCropCycleRepository;
    private final FarmEntityMapper mapper;

    @Override
    public CropCycle save(CropCycle cropCycle) {
        return mapper.toDomain(jpaCropCycleRepository.save(mapper.toEntity(cropCycle)));
    }

    @Override
    public Optional<CropCycle> findById(UUID cropCycleId) {
        return jpaCropCycleRepository.findById(cropCycleId).map(mapper::toDomain);
    }

    @Override
    public Optional<CropCycle> findActiveByFarmId(UUID farmId) {
        return jpaCropCycleRepository.findActiveByFarmId(farmId).map(mapper::toDomain);
    }

    @Override
    public List<CropCycle> findAllByFarmId(UUID farmId) {
        return jpaCropCycleRepository.findByFarmId(farmId)
            .stream().map(mapper::toDomain).collect(Collectors.toList());
    }
}
