package com.agriyield.geospatialservice.infrastructure.repository;

import com.agriyield.geospatialservice.infrastructure.document.FarmBoundaryDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MongoFarmBoundaryRepository
    extends MongoRepository<FarmBoundaryDocument, String> {

    Optional<FarmBoundaryDocument> findByFarmId(String farmId);
}
