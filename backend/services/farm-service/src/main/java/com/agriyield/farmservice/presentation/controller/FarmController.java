package com.agriyield.farmservice.presentation.controller;

import com.agriyield.farmservice.application.port.incoming.FarmServicePort;
import com.agriyield.farmservice.domain.model.*;
import com.agriyield.farmservice.infrastructure.config.JwtUtils;
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
    private final JwtUtils jwtUtils;

    @PostMapping
    public ResponseEntity<ApiResponse<FarmResponse>> registerFarm(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody RegisterFarmRequest request) {

        UUID farmerId = jwtUtils.extractUserId(authHeader);
        log.info("POST /api/v1/farms — farmer: {}", farmerId);

        Farm farm = farmService.registerFarm(
            farmerId,
            request.getFarmName(),
            request.getCropType(),
            request.getKebeleCode(),
            request.getRegion(),
            request.getExpectedHarvestDate(),
            request.getGeoJsonPolygon());

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Farm registered successfully",
                toFarmResponse(farm)));
    }

    @PostMapping(value = "/{farmId}/photos",
                 consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<FarmPhotoResponse>> uploadPhoto(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable UUID farmId,
            @RequestParam("photo") MultipartFile photo,
            @RequestParam("photo_type") String photoType) {

        UUID farmerId = jwtUtils.extractUserId(authHeader);
        FarmPhoto farmPhoto = farmService.uploadPhoto(
            farmId, farmerId, photo, photoType);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Photo uploaded successfully",
                toPhotoResponse(farmPhoto)));
    }

    // No cropCycleId needed — auto-fetched from active crop cycle
    @PostMapping("/{farmId}/input-needs")
    public ResponseEntity<ApiResponse<InputNeedResponse>> submitInputNeeds(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable UUID farmId,
            @Valid @RequestBody InputNeedRequest request) {

        UUID farmerId = jwtUtils.extractUserId(authHeader);
        log.info("POST /api/v1/farms/{}/input-needs — farmer: {}",
            farmId, farmerId);

        List<FarmServicePort.InputNeedItemRequest> items = request.getItems()
            .stream()
            .map(item -> new FarmServicePort.InputNeedItemRequest(
                item.getProductCategory(),
                item.getProductName(),
                item.getQuantity(),
                item.getUnit(),
                item.getEstimatedPriceEtb(),
                item.getSequenceOrder()))
            .collect(Collectors.toList());

        InputNeed inputNeed = farmService.submitInputNeeds(
            farmId, farmerId, items);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Input needs submitted successfully",
                toInputNeedResponse(inputNeed)));
    }

    @GetMapping("/{farmId}")
    public ResponseEntity<ApiResponse<FarmResponse>> getFarmById(
            @PathVariable UUID farmId) {
        Farm farm = farmService.getFarmById(farmId);
        return ResponseEntity.ok(ApiResponse.success(toFarmResponse(farm)));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<FarmResponse>>> getMyFarms(
            @RequestHeader("Authorization") String authHeader) {
        UUID farmerId = jwtUtils.extractUserId(authHeader);
        List<FarmResponse> responses = farmService.getMyFarms(farmerId)
            .stream().map(this::toFarmResponse).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/{farmId}/digital-twin")
    public ResponseEntity<ApiResponse<DigitalTwinResponse>> getDigitalTwin(
            @PathVariable UUID farmId) {
        FarmDocument doc = farmService.getDigitalTwin(farmId);
        return ResponseEntity.ok(ApiResponse.success(
            toDigitalTwinResponse(doc)));
    }

    @PostMapping("/{farmId}/confirm-planting")
    public ResponseEntity<ApiResponse<CropCycleResponse>> confirmPlanting(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable UUID farmId,
            @Valid @RequestBody ConfirmPlantingRequest request) {

        UUID farmerId = jwtUtils.extractUserId(authHeader);
        CropCycle cropCycle = farmService.confirmPlanting(
            farmId, farmerId, request.getPlantingDate());

        return ResponseEntity.ok(ApiResponse.success(
            "Planting confirmed successfully",
            toCropCycleResponse(cropCycle)));
    }

    @GetMapping("/{farmId}/agri-score")
    public ResponseEntity<ApiResponse<AgriScoreResponse>> getAgriScore(
            @PathVariable UUID farmId) {
        AgriScore score = farmService.getAgriScore(farmId);
        return ResponseEntity.ok(ApiResponse.success(
            toAgriScoreResponse(score)));
    }

    private FarmResponse toFarmResponse(Farm farm) {
        return FarmResponse.builder()
            .id(farm.getId()).farmerId(farm.getFarmerId())
            .farmName(farm.getFarmName())
            .cropType(farm.getCropType().getValue())
            .areaHectares(farm.getAreaHectares())
            .status(farm.getStatus().getValue())
            .kebeleCode(farm.getKebeleCode()).region(farm.getRegion())
            .gpsCentroidLat(farm.getGpsCentroidLat())
            .gpsCentroidLng(farm.getGpsCentroidLng())
            .satelliteVerified(farm.getSatelliteVerified())
            .satelliteVerifiedAt(farm.getSatelliteVerifiedAt())
            .createdAt(farm.getCreatedAt()).updatedAt(farm.getUpdatedAt())
            .build();
    }

    private FarmPhotoResponse toPhotoResponse(FarmPhoto photo) {
        return FarmPhotoResponse.builder()
            .id(photo.getId()).farmId(photo.getFarmId())
            .photoUrl(photo.getPhotoUrl())
            .gpsLat(photo.getGpsLat()).gpsLng(photo.getGpsLng())
            .photoType(photo.getPhotoType().getValue())
            .gpsVerified(photo.getGpsVerified())
            .uploadedAt(photo.getUploadedAt()).build();
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
                        .sequenceOrder(item.getSequenceOrder()).build())
                    .collect(Collectors.toList())
                : List.of();

        return InputNeedResponse.builder()
            .id(inputNeed.getId()).farmId(inputNeed.getFarmId())
            .cropCycleId(inputNeed.getCropCycleId())
            .totalAmountEtb(inputNeed.getTotalAmountEtb())
            .fundedAmountEtb(inputNeed.getFundedAmountEtb())
            .status(inputNeed.getStatus().getValue())
            .createdAt(inputNeed.getCreatedAt()).items(itemResponses).build();
    }

    private CropCycleResponse toCropCycleResponse(CropCycle c) {
        return CropCycleResponse.builder()
            .id(c.getId()).farmId(c.getFarmId())
            .seasonName(c.getSeasonName())
            .plantingDate(c.getPlantingDate())
            .expectedHarvestDate(c.getExpectedHarvestDate())
            .actualHarvestDate(c.getActualHarvestDate())
            .status(c.getStatus().getValue())
            .createdAt(c.getCreatedAt()).build();
    }

    private AgriScoreResponse toAgriScoreResponse(AgriScore s) {
        return AgriScoreResponse.builder()
            .id(s.getId()).farmerId(s.getFarmerId())
            .cropCycleId(s.getCropCycleId()).score(s.getScore())
            .voucherDisciplinePts(s.getVoucherDisciplinePts())
            .yieldAccuracyPts(s.getYieldAccuracyPts())
            .contractFulfillmentPts(s.getContractFulfillmentPts())
            .repaymentCompletionPts(s.getRepaymentCompletionPts())
            .seasonCompletionPts(s.getSeasonCompletionPts())
            .agronomistAssessmentPts(s.getAgronomistAssessmentPts())
            .calculatedAt(s.getCalculatedAt()).build();
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
                    .lat(r.getLat()).lng(r.getLng())
                    .uploadedAt(r.getUploadedAt()).build())
                .collect(Collectors.toList()))
            .agronomistReports(doc.getAgronomistReports().stream()
                .map(r -> DigitalTwinResponse.AgronomistReportResponse.builder()
                    .reportId(r.getReportId())
                    .agronomistId(r.getAgronomistId())
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
