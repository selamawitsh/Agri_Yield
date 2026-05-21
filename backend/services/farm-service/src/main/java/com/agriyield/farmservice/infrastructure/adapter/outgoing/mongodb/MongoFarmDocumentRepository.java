package com.agriyield.farmservice.infrastructure.adapter.outgoing.mongodb;

import com.agriyield.farmservice.infrastructure.adapter.outgoing.mongodb.document.FarmDocumentEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MongoFarmDocumentRepository extends MongoRepository<FarmDocumentEntity, String> {

    Optional<FarmDocumentEntity> findByFarmId(String farmId);
}
