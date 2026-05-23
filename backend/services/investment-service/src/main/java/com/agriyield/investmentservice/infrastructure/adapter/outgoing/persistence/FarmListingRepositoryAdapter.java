package com.agriyield.investmentservice.infrastructure.adapter.outgoing.persistence;

import com.agriyield.investmentservice.application.port.outgoing.FarmListingRepositoryPort;
import com.agriyield.investmentservice.domain.model.FarmListing;
import com.agriyield.investmentservice.infrastructure.adapter.outgoing.persistence.mapper.InvestmentEntityMapper;
import com.agriyield.investmentservice.infrastructure.repository.JpaFarmListingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FarmListingRepositoryAdapter implements FarmListingRepositoryPort {

    private final JpaFarmListingRepository jpaRepository;
    private final InvestmentEntityMapper mapper;

    @Override
    public FarmListing save(FarmListing listing) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(listing)));
    }

    @Override
    public Optional<FarmListing> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<FarmListing> findByInputNeedId(UUID inputNeedId) {
        return jpaRepository.findByInputNeedId(inputNeedId).map(mapper::toDomain);
    }

    @Override
    public List<FarmListing> findAllOpen() {
        return jpaRepository.findAllOpen()
            .stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<FarmListing> findByCropType(String cropType) {
        return jpaRepository.findByCropType(cropType)
            .stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<FarmListing> findByRegion(String region) {
        return jpaRepository.findByRegion(region)
            .stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<FarmListing> findByCropTypeAndRegion(String cropType, String region) {
        return jpaRepository.findByCropTypeAndRegion(cropType, region)
            .stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<FarmListing> findExpiredOpenListings() {
        return jpaRepository.findExpiredOpenListings(LocalDateTime.now())
            .stream().map(mapper::toDomain).collect(Collectors.toList());
    }
}
