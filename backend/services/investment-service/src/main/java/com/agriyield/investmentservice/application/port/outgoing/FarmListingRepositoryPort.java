package com.agriyield.investmentservice.application.port.outgoing;

import com.agriyield.investmentservice.domain.model.FarmListing;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FarmListingRepositoryPort {

    FarmListing save(FarmListing listing);

    Optional<FarmListing> findById(UUID id);

    Optional<FarmListing> findByInputNeedId(UUID inputNeedId);

    List<FarmListing> findAllOpen();

    List<FarmListing> findByCropType(String cropType);

    List<FarmListing> findByRegion(String region);

    List<FarmListing> findByCropTypeAndRegion(String cropType, String region);

    List<FarmListing> findExpiredOpenListings();
}
