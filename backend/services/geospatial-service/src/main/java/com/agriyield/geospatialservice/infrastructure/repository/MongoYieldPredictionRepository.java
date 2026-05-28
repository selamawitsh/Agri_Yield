package com.agriyield.geospatialservice.infrastructure.repository;

import com.agriyield.geospatialservice.infrastructure.document.YieldPredictionDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MongoYieldPredictionRepository
    extends MongoRepository<YieldPredictionDocument, String> {

    Optional<YieldPredictionDocument> findFirstByFarmIdOrderByCreatedAtDesc(String farmId);
}
