package com.agriyield.farmservice.presentation.controller;

import com.agriyield.farmservice.application.port.incoming.FarmServicePort;
import com.agriyield.farmservice.domain.model.*;
import com.agriyield.farmservice.presentation.dto.request.*;
import com.agriyield.farmservice.presentation.dto.response.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/v1/farms")
@RequiredArgsConstructor
public class FarmController {

    private final FarmServicePort farmService;

    // SRS Page 22 — POST /api/v1/farms
    // X-User-Id injected by API Gateway after JWT validation
    @PostMapping
    public ResponseEntity<ApiResponse<FarmResponse>> registerFarm(
            @RequestHeader("X-User-Id") String farmerId,
            @Valid @RequestBody RegisterFarmRequest request) {

        log.info("POST /api/v1/farms — farmer: {}", farmerId);

        Farm farm = farmService.registerFarm(
            UUID.fromString(farmerId),
            request.getFarmName(),
            request.getCropType(),
            request.getKebeleCode(),
            request.getRegion(),
            request.getExpectedHarvestDate(),
            request.getGeoJsonPolygon()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Farm registered successfully", toFarmResponse(farm)));
    }

    // SRS Page 22 — POST /api/v1/farms/{farm_id}/photos
    @PostMapping(value = "/{farmId}/photos",
                 consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<FarmPhotoResponse>> uploadPhoto(
            @RequestHeader("X-User-Id") String farmerId,
            @PathVariable UUID farmId,
            @RequestParam("photo") MultipartFile photo,
            @RequestParam("photo_type") String photoType) {

        log.info("POST /api/v1/farms/{}/photos — farmer: {}", farmId, farmerId);

        FarmPhoto farmPhoto = farmService.uploadPhoto(
            farmId,
            UUID.fromString(farmerId),
            photo,
            photoType
        );

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Photo uploaded successfully",
                toPhotoResponse(farmPhoto)));
    }

    // SRS Page 22 — POST /api/v1/farms/{farm_id}/input-needs
    @PostMapping("/{farmId}/input-needs")
    public ResponseEntity<ApiResponse<InputNeedResponse>> submitInputNeeds(
            @RequestHeader("X-User-Id") String farmerId,
            @PathVariable UUID farmId,
            @Valid @RequestBody InputNeedRequest request) {

        log.info("POST /api/v1/farms/{}/input-needs — farmer: {}", farmId, farmerId);

        List<FarmServicePort.InputNeedItemRequest> items = request.getItems().stream()
            .map(item -> new FarmServicePort.InputNeedItemRequest(
                item.getProductCategory(),
                item.getProductName(),
                item.getQuantity(),
                item.getUnit(),
                item.getEstimatedPriceEtb(),
                item.getSequenceOrder()
            ))
            .collect(Collectors.toList());

        InputNeed inputNeed = farmService.submitInputNeeds(
            farmId,
            UUID.fromString(farmerId),
            request.getCropCycleId(),
            items
        );

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Input needs submitted successfully",
                toInputNeedResponse(inputNeed)));
    }

    // SRS Page 22 — GET /api/v1/farms/{farm_id}
    @GetMapping("/{farmId}")
    public ResponseEntity<ApiResponse<FarmResponse>> getFarmById(
            @PathVariable UUID farmId) {

        log.info("GET /api/v1/farms/{}", farmId);
        Farm farm = farmService.getFarmById(farmId);
        return ResponseEntity.ok(ApiResponse.success(toFarmResponse(farm)));
    }

    // SRS Page 22 — GET /api/v1/farms/my
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<FarmResponse>>> getMyFarms(
            @RequestHeader("X-User-Id") String farmerId) {

        log.info("GET /api/v1/farms/my — farmer: {}", farmerId);
        List<Farm> farms = farmService.getMyFarms(UUID.fromString(farmerId));
        List<FarmResponse> responses = farms.stream()
            .map(this::toFarmResponse)
            .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    // SRS Page 22 — GET /api/v1/farms/{farm_id}/digital-twin
    @GetMapping("/{farmId}/digital-twin")
    public ResponseEntity<ApiResponse<DigitalTwinResponse>> getDigitalTwin(
            @PathVariable UUID farmId) {

        log.info("GET /api/v1/farms/{}/digital-twin", farmId);
        FarmDocument doc = farmService.getDigitalTwin(farmId);
        return ResponseEntity.ok(ApiResponse.success(toDigitalTwinResponse(doc)));
    }

    // SRS Page 22 — POST /api/v1/farms/{farm_id}/confirm-planting
    @PostMapping("/{farmId}/confirm-planting")
    public ResponseEntity<ApiResponse<CropCycleResponse>> confirmPlanting(
            @RequestHeader("X-User-Id") String farmerId,
            @PathVariable UUID farmId,
            @Valid @RequestBody ConfirmPlantingRequest request) {

        log.info("POST /api/v1/farms/{}/confirm-planting — farmer: {}", farmId, farmerId);

        CropCycle cropCycle = farmService.confirmPlanting(
            farmId,
            UUID.fromString(farmerId),
            request.getPlantingDate()
        );

        return ResponseEntity.ok(
            ApiResponse.success("Planting confirmed successfully",
                toCropCycleResponse(cropCycle)));
    }

    // SRS Page 22 — GET /api/v1/farms/{farm_id}/agri-score
    @GetMapping("/{farmId}/agri-score")
    public ResponseEntity<ApiResponse<AgriScoreResponse>> getAgriScore(
            @PathVariable UUID farmId) {

        log.info("GET /api/v1/farms/{}/agri-score", farmId);
        AgriScore score = farmService.getAgriScore(farmId);
        return ResponseEntity.ok(ApiResponse.success(toAgriScoreResponse(score)));
    }

    // Mappers — domain to response DTOs

    private FarmResponse toFarmResponse(Farm farm) {
        return FarmResponse.builder()
            .id(farm.getId())
            .farmerId(farm.getFarmerId())
            .farmName(farm.getFarmName())
            .cropType(farm.getCropType().getValue())
            .areaHectares(farm.getAreaHectares())
            .status(farm.getStatus().getValue())
            .kebeleCode(farm.getKebeleCode())
            .region(farm.getRegion())
            .gpsCentroidLat(farm.getGpsCentroidLat())
            .gpsCentroidLng(farm.getGpsCentroidLng())
            .satelliteVerified(farm.getSatelliteVerified())
            .satelliteVerifiedAt(farm.getSatelliteVerifiedAt())
            .createdAt(farm.getCreatedAt())
            .updatedAt(farm.getUpdatedAt())
            .build();
    }

    private FarmPhotoResponse toPhotoResponse(FarmPhoto photo) {
        return FarmPhotoResponse.builder()
            .id(photo.getId())
            .farmId(photo.getFarmId())
            .photoUrl(photo.getPhotoUrl())
            .gpsLat(photo.getGpsLat())
            .gpsLng(photo.getGpsLng())
            .photoType(photo.getPhotoType().getValue())
            .gpsVerified(photo.getGpsVerified())
            .uploadedAt(photo.getUploadedAt())
            .build();
    }

    private InputNeedResponse toInputNeedResponse(InputNeed inputNeed) {
        List<InputNeedResponse.InputNeedItemResponse> itemResponses =
            inputNeed.getItems() != null
                ? inputNeed.getItems().stream()
                    .map(item -> InputNeedResponse.InputNeedItemResponse.builder()
                        .id(item.getId())
                        .productCategory(item.getProductCategory().getValue())
                        .productName(item.getProductName())
                        .quantity(item.getQuantity())
                        .unit(item.getUnit())
                        .estimatedPriceEtb(item.getEstimatedPriceEtb())
                        .sequenceOrder(item.getSequenceOrder())
                        .build())
                    .collect(Collectors.toList())
                : List.of();

        return InputNeedResponse.builder()
            .id(inputNeed.getId())
            .farmId(inputNeed.getFarmId())
            .cropCycleId(inputNeed.getCropCycleId())
            .totalAmountEtb(inputNeed.getTotalAmountEtb())
            .fundedAmountEtb(inputNeed.getFundedAmountEtb())
            .status(inputNeed.getStatus().getValue())
            .createdAt(inputNeed.getCreatedAt())
            .items(itemResponses)
            .build();
    }

    private CropCycleResponse toCropCycleResponse(CropCycle cropCycle) {
        return CropCycleResponse.builder()
            .id(cropCycle.getId())
            .farmId(cropCycle.getFarmId())
            .seasonName(cropCycle.getSeasonName())
            .plantingDate(cropCycle.getPlantingDate())
            .expectedHarvestDate(cropCycle.getExpectedHarvestDate())
            .actualHarvestDate(cropCycle.getActualHarvestDate())
            .status(cropCycle.getStatus().getValue())
            .createdAt(cropCycle.getCreatedAt())
            .build();
    }

    private AgriScoreResponse toAgriScoreResponse(AgriScore score) {
        return AgriScoreResponse.builder()
            .id(score.getId())
            .farmerId(score.getFarmerId())
            .cropCycleId(score.getCropCycleId())
            .score(score.getScore())
            .voucherDisciplinePts(score.getVoucherDisciplinePts())
            .yieldAccuracyPts(score.getYieldAccuracyPts())
            .contractFulfillmentPts(score.getContractFulfillmentPts())
            .repaymentCompletionPts(score.getRepaymentCompletionPts())
            .seasonCompletionPts(score.getSeasonCompletionPts())
            .agronomistAssessmentPts(score.getAgronomistAssessmentPts())
            .calculatedAt(score.getCalculatedAt())
            .build();
    }

    private DigitalTwinResponse toDigitalTwinResponse(FarmDocument doc) {
        return DigitalTwinResponse.builder()
            .farmId(doc.getFarmId())
            .geoJsonPolygon(doc.getGeoJsonPolygon())
            .ndviHistory(doc.getNdviHistory().stream()
                .map(r -> DigitalTwinResponse.NdviReadingResponse.builder()
                    .date(r.getDate()).ndvi(r.getNdvi())
                    .cloudCoverage(r.getCloudCoverage()).build())
                .collect(Collectors.toList()))
            .photoHistory(doc.getPhotoHistory().stream()
                .map(r -> DigitalTwinResponse.PhotoRecordResponse.builder()
                    .photoId(r.getPhotoId()).type(r.getType()).url(r.getUrl())
                    .lat(r.getLat()).lng(r.getLng()).uploadedAt(r.getUploadedAt()).build())
                .collect(Collectors.toList()))
            .agronomistReports(doc.getAgronomistReports().stream()
                .map(r -> DigitalTwinResponse.AgronomistReportResponse.builder()
                    .reportId(r.getReportId()).agronomistId(r.getAgronomistId())
                    .visitDate(r.getVisitDate()).diagnosis(r.getDiagnosis())
                    .treatment(r.getTreatment()).rating(r.getRating()).build())
                .collect(Collectors.toList()))
            .weatherLog(doc.getWeatherLog().stream()
                .map(r -> DigitalTwinResponse.WeatherRecordResponse.builder()
                    .date(r.getDate()).rainfallMm(r.getRainfallMm())
                    .tempC(r.getTempC()).isDryDay(r.getIsDryDay()).build())
                .collect(Collectors.toList()))
            .build();
    }
}
