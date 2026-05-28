package com.agriyield.geospatialservice.infrastructure.repository;

import com.agriyield.geospatialservice.infrastructure.document.NdviReadingDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MongoNdviRepository extends MongoRepository<NdviReadingDocument, String> {

    Optional<NdviReadingDocument> findFirstByFarmIdOrderByRecordedDateDesc(String farmId);

    List<NdviReadingDocument> findByFarmIdAndRecordedDateAfterOrderByRecordedDateAsc(
        String farmId, LocalDate since);

    List<NdviReadingDocument> findTop10ByFarmIdOrderByRecordedDateDesc(String farmId);

    Optional<NdviReadingDocument> findFirstByFarmIdOrderByNdviValueDesc(String farmId);

    @Query(value = "{}", fields = "{'farmId': 1}")
    List<NdviReadingDocument> findAllDistinctFarmIds();
}
