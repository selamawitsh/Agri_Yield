package com.agriyield.geospatialservice.infrastructure.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "ndvi_readings")
@CompoundIndexes({
    @CompoundIndex(name = "farm_date_idx", def = "{'farmId': 1, 'recordedDate': -1}"),
    @CompoundIndex(name = "farm_ndvi_idx", def = "{'farmId': 1, 'ndviValue': -1}")
})
public class NdviReadingDocument {

    @Id
    private String id;

    @Field("farmId")
    private String farmId;

    @Field("ndviValue")
    private double ndviValue;

    @Field("cloudCoverage")
    private double cloudCoverage;

    @Field("sentinelSceneId")
    private String sentinelSceneId;

    @Field("recordedDate")
    private LocalDate recordedDate;

    @Field("createdAt")
    private LocalDateTime createdAt;
}
