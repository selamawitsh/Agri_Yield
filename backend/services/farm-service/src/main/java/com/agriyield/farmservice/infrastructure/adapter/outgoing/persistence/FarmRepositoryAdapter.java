package com.agriyield.farmservice.infrastructure.adapter.outgoing.persistence;

import com.agriyield.farmservice.application.port.outgoing.FarmRepositoryPort;
import com.agriyield.farmservice.domain.model.Farm;
import com.agriyield.farmservice.infrastructure.adapter.outgoing.persistence.mapper.FarmEntityMapper;
import com.agriyield.farmservice.infrastructure.repository.JpaFarmRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FarmRepositoryAdapter implements FarmRepositoryPort {

    private final JpaFarmRepository jpaFarmRepository;
    private final FarmEntityMapper mapper;

    @Override
    public Farm save(Farm farm) {
        return mapper.toDomain(jpaFarmRepository.save(mapper.toEntity(farm)));
    }

    @Override
    public Optional<Farm> findById(UUID farmId) {
        return jpaFarmRepository.findById(farmId).map(mapper::toDomain);
    }

    @Override
    public List<Farm> findByFarmerId(UUID farmerId) {
        return jpaFarmRepository.findByFarmerId(farmerId)
            .stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public boolean existsById(UUID farmId) {
        return jpaFarmRepository.existsById(farmId);
    }

    @Override
    public List<Farm> findAllActive() {
        return jpaFarmRepository.findAllActive()
            .stream().map(mapper::toDomain).collect(Collectors.toList());
    }
}
