package com.agriyield.farmservice.infrastructure.adapter.outgoing.persistence;

import com.agriyield.farmservice.application.port.outgoing.AgriScoreRepositoryPort;
import com.agriyield.farmservice.domain.model.AgriScore;
import com.agriyield.farmservice.infrastructure.adapter.outgoing.persistence.mapper.FarmEntityMapper;
import com.agriyield.farmservice.infrastructure.repository.JpaAgriScoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AgriScoreRepositoryAdapter implements AgriScoreRepositoryPort {

    private final JpaAgriScoreRepository jpaAgriScoreRepository;
    private final FarmEntityMapper mapper;

    @Override
    public AgriScore save(AgriScore agriScore) {
        return mapper.toDomain(jpaAgriScoreRepository.save(mapper.toEntity(agriScore)));
    }

    @Override
    public Optional<AgriScore> findLatestByFarmerId(UUID farmerId) {
        return jpaAgriScoreRepository
            .findTopByFarmerIdOrderByCalculatedAtDesc(farmerId)
            .map(mapper::toDomain);
    }

    @Override
    public List<AgriScore> findAllByFarmerId(UUID farmerId) {
        return jpaAgriScoreRepository
            .findByFarmerIdOrderByCalculatedAtDesc(farmerId)
            .stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public double calculateCumulativeScore(UUID farmerId) {
        Double avg = jpaAgriScoreRepository.calculateAverageScore(farmerId);
        return avg != null ? avg : 50.0;
    }
}
