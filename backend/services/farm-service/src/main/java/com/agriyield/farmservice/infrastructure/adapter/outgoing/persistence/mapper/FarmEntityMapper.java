package com.agriyield.farmservice.infrastructure.adapter.outgoing.persistence.mapper;

import com.agriyield.farmservice.domain.enums.*;
import com.agriyield.farmservice.domain.model.*;
import com.agriyield.farmservice.infrastructure.adapter.outgoing.persistence.entity.*;
import org.springframework.stereotype.Component;

@Component
public class FarmEntityMapper {

    // Farm mappings
    public Farm toDomain(FarmEntity entity) {
        if (entity == null) return null;
        return Farm.builder()
            .id(entity.getId())
            .farmerId(entity.getFarmerId())
            .farmName(entity.getFarmName())
            .cropType(CropType.fromValue(entity.getCropType()))
            .areaHectares(entity.getAreaHectares())
            .status(FarmStatus.valueOf(entity.getStatus()))
            .kebeleCode(entity.getKebeleCode())
            .region(entity.getRegion())
            .gpsCentroidLat(entity.getGpsCentroidLat())
            .gpsCentroidLng(entity.getGpsCentroidLng())
            .geoJsonPolygon(entity.getGeoJsonPolygon())
            .satelliteVerified(entity.getSatelliteVerified())
            .satelliteVerifiedAt(entity.getSatelliteVerifiedAt())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .build();
    }

    public FarmEntity toEntity(Farm domain) {
        if (domain == null) return null;
        return FarmEntity.builder()
            .id(domain.getId())
            .farmerId(domain.getFarmerId())
            .farmName(domain.getFarmName())
            .cropType(domain.getCropType().getValue())
            .areaHectares(domain.getAreaHectares())
            .status(domain.getStatus().getValue())
            .kebeleCode(domain.getKebeleCode())
            .region(domain.getRegion())
            .gpsCentroidLat(domain.getGpsCentroidLat())
            .gpsCentroidLng(domain.getGpsCentroidLng())
            .geoJsonPolygon(domain.getGeoJsonPolygon())
            .satelliteVerified(domain.getSatelliteVerified())
            .satelliteVerifiedAt(domain.getSatelliteVerifiedAt())
            .createdAt(domain.getCreatedAt())
            .updatedAt(domain.getUpdatedAt())
            .build();
    }

    // CropCycle mappings
    public CropCycle toDomain(CropCycleEntity entity) {
        if (entity == null) return null;
        return CropCycle.builder()
            .id(entity.getId())
            .farmId(entity.getFarmId())
            .seasonName(entity.getSeasonName())
            .plantingDate(entity.getPlantingDate())
            .expectedHarvestDate(entity.getExpectedHarvestDate())
            .actualHarvestDate(entity.getActualHarvestDate())
            .status(CropCycleStatus.valueOf(entity.getStatus()))
            .createdAt(entity.getCreatedAt())
            .build();
    }

    public CropCycleEntity toEntity(CropCycle domain) {
        if (domain == null) return null;
        return CropCycleEntity.builder()
            .id(domain.getId())
            .farmId(domain.getFarmId())
            .seasonName(domain.getSeasonName())
            .plantingDate(domain.getPlantingDate())
            .expectedHarvestDate(domain.getExpectedHarvestDate())
            .actualHarvestDate(domain.getActualHarvestDate())
            .status(domain.getStatus().getValue())
            .createdAt(domain.getCreatedAt())
            .build();
    }

    // InputNeed mappings
    public InputNeed toDomain(InputNeedEntity entity) {
        if (entity == null) return null;
        return InputNeed.builder()
            .id(entity.getId())
            .farmId(entity.getFarmId())
            .cropCycleId(entity.getCropCycleId())
            .totalAmountEtb(entity.getTotalAmountEtb())
            .fundedAmountEtb(entity.getFundedAmountEtb())
            .status(InputNeedStatus.valueOf(entity.getStatus()))
            .createdAt(entity.getCreatedAt())
            .build();
    }

    public InputNeedEntity toEntity(InputNeed domain) {
        if (domain == null) return null;
        return InputNeedEntity.builder()
            .id(domain.getId())
            .farmId(domain.getFarmId())
            .cropCycleId(domain.getCropCycleId())
            .totalAmountEtb(domain.getTotalAmountEtb())
            .fundedAmountEtb(domain.getFundedAmountEtb())
            .status(domain.getStatus().getValue())
            .createdAt(domain.getCreatedAt())
            .build();
    }

    // InputNeedItem mappings
    public InputNeedItem toDomain(InputNeedItemEntity entity) {
        if (entity == null) return null;
        return InputNeedItem.builder()
            .id(entity.getId())
            .inputNeedId(entity.getInputNeedId())
            .productCategory(ProductCategory.fromValue(entity.getProductCategory()))
            .productName(entity.getProductName())
            .quantity(entity.getQuantity())
            .unit(entity.getUnit())
            .estimatedPriceEtb(entity.getEstimatedPriceEtb())
            .sequenceOrder(entity.getSequenceOrder())
            .build();
    }

    public InputNeedItemEntity toEntity(InputNeedItem domain) {
        if (domain == null) return null;
        return InputNeedItemEntity.builder()
            .id(domain.getId())
            .inputNeedId(domain.getInputNeedId())
            .productCategory(domain.getProductCategory().getValue())
            .productName(domain.getProductName())
            .quantity(domain.getQuantity())
            .unit(domain.getUnit())
            .estimatedPriceEtb(domain.getEstimatedPriceEtb())
            .sequenceOrder(domain.getSequenceOrder())
            .build();
    }

    // FarmPhoto mappings
    public FarmPhoto toDomain(FarmPhotoEntity entity) {
        if (entity == null) return null;
        return FarmPhoto.builder()
            .id(entity.getId())
            .farmId(entity.getFarmId())
            .photoUrl(entity.getPhotoUrl())
            .gpsLat(entity.getGpsLat())
            .gpsLng(entity.getGpsLng())
            .photoType(PhotoType.valueOf(entity.getPhotoType()))
            .gpsVerified(entity.getGpsVerified())
            .uploadedAt(entity.getUploadedAt())
            .build();
    }

    public FarmPhotoEntity toEntity(FarmPhoto domain) {
        if (domain == null) return null;
        return FarmPhotoEntity.builder()
            .id(domain.getId())
            .farmId(domain.getFarmId())
            .photoUrl(domain.getPhotoUrl())
            .gpsLat(domain.getGpsLat())
            .gpsLng(domain.getGpsLng())
            .photoType(domain.getPhotoType().getValue())
            .gpsVerified(domain.getGpsVerified())
            .uploadedAt(domain.getUploadedAt())
            .build();
    }

    // AgriScore mappings
    public AgriScore toDomain(AgriScoreEntity entity) {
        if (entity == null) return null;
        return AgriScore.builder()
            .id(entity.getId())
            .farmerId(entity.getFarmerId())
            .cropCycleId(entity.getCropCycleId())
            .score(entity.getScore())
            .voucherDisciplinePts(entity.getVoucherDisciplinePts())
            .yieldAccuracyPts(entity.getYieldAccuracyPts())
            .contractFulfillmentPts(entity.getContractFulfillmentPts())
            .repaymentCompletionPts(entity.getRepaymentCompletionPts())
            .seasonCompletionPts(entity.getSeasonCompletionPts())
            .agronomistAssessmentPts(entity.getAgronomistAssessmentPts())
            .calculatedAt(entity.getCalculatedAt())
            .build();
    }

    public AgriScoreEntity toEntity(AgriScore domain) {
        if (domain == null) return null;
        return AgriScoreEntity.builder()
            .id(domain.getId())
            .farmerId(domain.getFarmerId())
            .cropCycleId(domain.getCropCycleId())
            .score(domain.getScore())
            .voucherDisciplinePts(domain.getVoucherDisciplinePts())
            .yieldAccuracyPts(domain.getYieldAccuracyPts())
            .contractFulfillmentPts(domain.getContractFulfillmentPts())
            .repaymentCompletionPts(domain.getRepaymentCompletionPts())
            .seasonCompletionPts(domain.getSeasonCompletionPts())
            .agronomistAssessmentPts(domain.getAgronomistAssessmentPts())
            .calculatedAt(domain.getCalculatedAt())
            .build();
    }
}
