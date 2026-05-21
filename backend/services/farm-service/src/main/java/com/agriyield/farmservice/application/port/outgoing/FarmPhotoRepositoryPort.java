package com.agriyield.farmservice.application.port.outgoing;

import com.agriyield.farmservice.domain.model.FarmPhoto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FarmPhotoRepositoryPort {

    FarmPhoto save(FarmPhoto farmPhoto);

    Optional<FarmPhoto> findById(UUID photoId);

    List<FarmPhoto> findAllByFarmId(UUID farmId);
}
