package com.agriyield.farmservice.infrastructure.adapter.outgoing.persistence;

import com.agriyield.farmservice.application.port.outgoing.FarmPhotoRepositoryPort;
import com.agriyield.farmservice.domain.model.FarmPhoto;
import com.agriyield.farmservice.infrastructure.adapter.outgoing.persistence.mapper.FarmEntityMapper;
import com.agriyield.farmservice.infrastructure.repository.JpaFarmPhotoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FarmPhotoRepositoryAdapter implements FarmPhotoRepositoryPort {

    private final JpaFarmPhotoRepository jpaFarmPhotoRepository;
    private final FarmEntityMapper mapper;

    @Override
    public FarmPhoto save(FarmPhoto farmPhoto) {
        return mapper.toDomain(jpaFarmPhotoRepository.save(mapper.toEntity(farmPhoto)));
    }

    @Override
    public Optional<FarmPhoto> findById(UUID photoId) {
        return jpaFarmPhotoRepository.findById(photoId).map(mapper::toDomain);
    }

    @Override
    public List<FarmPhoto> findAllByFarmId(UUID farmId) {
        return jpaFarmPhotoRepository.findByFarmId(farmId)
            .stream().map(mapper::toDomain).collect(Collectors.toList());
    }
}
