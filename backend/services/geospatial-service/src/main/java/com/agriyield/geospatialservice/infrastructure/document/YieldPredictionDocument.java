package com.agriyield.geospatialservice.infrastructure.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "yield_predictions")
public class YieldPredictionDocument {

    @Id
    private String id;

    @Indexed
    @Field("farmId")
    private String farmId;

    @Field("cropCycleId")
    private String cropCycleId;

    @Field("cropType")
    private String cropType;

    @Field("predictedYieldMin")
    private double predictedYieldMin;

    @Field("predictedYieldMax")
    private double predictedYieldMax;

    @Field("predictedYieldMean")
    private double predictedYieldMean;

    @Field("totalYieldMinQuintals")
    private double totalYieldMinQuintals;

    @Field("totalYieldMaxQuintals")
    private double totalYieldMaxQuintals;

    @Field("totalYieldMeanQuintals")
    private double totalYieldMeanQuintals;

    @Field("confidencePct")
    private int confidencePct;

    @Field("weeksToHarvest")
    private int weeksToHarvest;

    @Field("modelVersion")
    private String modelVersion;

    @Field("ndviPeak")
    private double ndviPeak;

    @Field("ndviGrowthRate")
    private double ndviGrowthRate;

    @Field("totalRainfallMm")
    private double totalRainfallMm;

    @Field("avgTempC")
    private double avgTempC;

    @Field("altitudeM")
    private double altitudeM;

    @Field("inputQuality")
    private String inputQuality;

    @Field("predictedAt")
    private LocalDateTime predictedAt;

    @Field("createdAt")
    private LocalDateTime createdAt;
}
