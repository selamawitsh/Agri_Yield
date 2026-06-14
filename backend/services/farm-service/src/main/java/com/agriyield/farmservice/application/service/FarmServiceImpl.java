package com.agriyield.farmservice.application.service;

import com.agriyield.farmservice.application.port.incoming.FarmServicePort;
import com.agriyield.farmservice.application.port.outgoing.*;
import com.agriyield.farmservice.domain.enums.*;
import com.agriyield.farmservice.domain.exception.BusinessException;
import com.agriyield.farmservice.domain.exception.FarmNotFoundException;
import com.agriyield.farmservice.domain.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class FarmServiceImpl implements FarmServicePort {

    private final FarmRepositoryPort farmRepository;
    private final CropCycleRepositoryPort cropCycleRepository;
    private final InputNeedRepositoryPort inputNeedRepository;
    private final InputNeedItemRepositoryPort inputNeedItemRepository;
    private final FarmPhotoRepositoryPort farmPhotoRepository;
    private final AgriScoreRepositoryPort agriScoreRepository;
    private final FarmDocumentRepositoryPort farmDocumentRepository;
    private final UserServicePort userServicePort;
    private final FraudServicePort fraudServicePort;
    private final GeospatialServicePort geospatialServicePort;
    private final PhotoStoragePort photoStoragePort;
    private final EventPublisherPort eventPublisher;

    // =========================================================================
    // FS-01 — Register farm
    // =========================================================================
    @Override
    @Transactional
    public Farm registerFarm(UUID farmerId,
                             String farmName,
                             String cropType,
                             String kebeleCode,
                             String region,
                             LocalDate expectedHarvestDate,
                             String geoJsonPolygon) {

        log.info("Registering farm for farmer: {}", farmerId);

        boolean farmerExists = userServicePort.verifyFarmerExists(farmerId);
        if (!farmerExists) {
            log.warn("Farmer not found via gRPC, proceeding (stub mode)");
        }

        if (geoJsonPolygon == null || geoJsonPolygon.isBlank()) {
            throw new BusinessException(
                    "Farm GPS polygon is required", "MISSING_POLYGON");
        }

        GeospatialServicePort.PolygonValidation validation;
        try {
            validation = geospatialServicePort.validatePolygon(geoJsonPolygon);
        } catch (Exception e) {
            log.error("Geospatial validatePolygon failed: {}", e.getMessage());
            throw new BusinessException(
                    "Could not validate farm boundary. Is geospatial-service running on port 9089?",
                    "GEOSPATIAL_UNAVAILABLE");
        }
        if (!validation.valid()) {
            log.warn("Farm polygon rejected: {}", validation.message());
            throw new BusinessException(validation.message(), "INVALID_POLYGON");
        }

        BigDecimal centroidLat  = validation.centroidLat();
        BigDecimal centroidLng  = validation.centroidLng();
        BigDecimal areaHectares = validation.areaHectares();
        log.info("Polygon valid — area={} ha, centroid=({}, {})",
                areaHectares, centroidLat, centroidLng);

        UUID provisionalFarmId = UUID.randomUUID();
        try {
            GeospatialServicePort.SpatialOverlap overlap =
                    geospatialServicePort.detectSpatialOverlap(
                            provisionalFarmId, geoJsonPolygon, centroidLat, centroidLng);
            if (overlap.hasOverlap()) {
                log.warn("Spatial overlap detected: {}", overlap.message());
                throw new BusinessException(overlap.message(), "SPATIAL_OVERLAP");
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.warn("Overlap check skipped (geospatial): {}", e.getMessage());
        }

        Farm farm = Farm.builder()
                .id(provisionalFarmId)
                .farmerId(farmerId)
                .farmName(farmName)
                .cropType(CropType.fromValue(cropType))
                .areaHectares(areaHectares)
                .status(FarmStatus.PENDING_VERIFICATION)
                .kebeleCode(kebeleCode)
                .region(region)
                .gpsCentroidLat(centroidLat)
                .gpsCentroidLng(centroidLng)
                .geoJsonPolygon(geoJsonPolygon)
                .satelliteVerified(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Farm savedFarm = farmRepository.save(farm);
        log.info("Farm saved: {}", savedFarm.getId());

        // Auto-create first crop cycle during registration
        CropCycle cropCycle = CropCycle.builder()
                .id(UUID.randomUUID())
                .farmId(savedFarm.getId())
                .seasonName(generateSeasonName())
                .expectedHarvestDate(expectedHarvestDate)
                .status(CropCycleStatus.PLANNING)
                .createdAt(LocalDateTime.now())
                .build();

        cropCycleRepository.save(cropCycle);

        FarmDocument farmDocument = FarmDocument.builder()
                .farmId(savedFarm.getId().toString())
                .geoJsonPolygon(geoJsonPolygon)
                .build();

        farmDocumentRepository.save(farmDocument);

        try {
            geospatialServicePort.registerFarmPolygon(
                    savedFarm.getId(),
                    geoJsonPolygon,
                    centroidLat,
                    centroidLng,
                    areaHectares);
        } catch (Exception e) {
            log.warn("Geospatial polygon registration failed (event will retry): {}",
                    e.getMessage());
        }

        eventPublisher.publishFarmRegistered(savedFarm);
        return savedFarm;
    }

    // =========================================================================
    // FS-04 — Upload photo
    // =========================================================================
    @Override
    @Transactional
    public FarmPhoto uploadPhoto(UUID farmId,
                                 UUID farmerId,
                                 MultipartFile photo,
                                 String photoType) {

        Farm farm = farmRepository.findById(farmId)
                .orElseThrow(() -> new FarmNotFoundException(farmId.toString()));

        if (!farm.getFarmerId().equals(farmerId)) {
            throw new BusinessException(
                    "Farm does not belong to this farmer",
                    "UNAUTHORIZED_FARM_ACCESS");
        }

        BigDecimal gpsLat = farm.getGpsCentroidLat();
        BigDecimal gpsLng = farm.getGpsCentroidLng();
        String photoUrl   = photoStoragePort.uploadPhoto(farmId, photoType, photo);

        FarmPhoto farmPhoto = FarmPhoto.builder()
                .id(UUID.randomUUID())
                .farmId(farmId)
                .photoUrl(photoUrl)
                .gpsLat(gpsLat)
                .gpsLng(gpsLng)
                .photoType(PhotoType.valueOf(photoType))
                .gpsVerified(false)
                .uploadedAt(LocalDateTime.now())
                .build();

        FarmPhoto savedPhoto = farmPhotoRepository.save(farmPhoto);

        FraudServicePort.GpsVerificationResult gpsResult =
                fraudServicePort.verifyGpsConsistency(
                        farmId, gpsLat, gpsLng, farm.getGeoJsonPolygon());

        if (gpsResult.isConsistent()) {
            savedPhoto.markGpsVerified();
            farmPhotoRepository.save(savedPhoto);
        }

        farmDocumentRepository.appendPhotoRecord(farmId.toString(),
                FarmDocument.PhotoRecord.builder()
                        .photoId(savedPhoto.getId().toString())
                        .type(photoType).url(photoUrl)
                        .lat(gpsLat.doubleValue()).lng(gpsLng.doubleValue())
                        .uploadedAt(savedPhoto.getUploadedAt().toString())
                        .build());

        eventPublisher.publishCropPhotoUploaded(savedPhoto, farm);
        return savedPhoto;
    }

    // =========================================================================
    // FS-06 — Submit input needs
    // =========================================================================
    @Override
    @Transactional
    public InputNeed submitInputNeeds(UUID farmId,
                                      UUID farmerId,
                                      List<InputNeedItemRequest> items) {

        Farm farm = farmRepository.findById(farmId)
                .orElseThrow(() -> new FarmNotFoundException(farmId.toString()));

        if (!farm.getFarmerId().equals(farmerId)) {
            throw new BusinessException(
                    "Farm does not belong to this farmer",
                    "UNAUTHORIZED_FARM_ACCESS");
        }

        CropCycle cropCycle = cropCycleRepository.findActiveByFarmId(farmId)
                .orElseThrow(() -> new BusinessException(
                        "No active crop cycle found. Register the farm first.",
                        "NO_ACTIVE_CROP_CYCLE"));

        if (items == null || items.isEmpty()) {
            throw new BusinessException(
                    "At least one input item is required",
                    "EMPTY_INPUT_NEEDS");
        }

        BigDecimal totalAmount = items.stream()
                .map(InputNeedItemRequest::estimatedPriceEtb)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        InputNeed inputNeed = InputNeed.builder()
                .id(UUID.randomUUID())
                .farmId(farmId)
                .cropCycleId(cropCycle.getId())
                .totalAmountEtb(totalAmount)
                .fundedAmountEtb(BigDecimal.ZERO)
                .status(InputNeedStatus.OPEN)
                .createdAt(LocalDateTime.now())
                .build();

        InputNeed savedInputNeed = inputNeedRepository.save(inputNeed);

        List<InputNeedItem> savedItems = items.stream()
                .map(i -> InputNeedItem.builder()
                        .id(UUID.randomUUID())
                        .inputNeedId(savedInputNeed.getId())
                        .productCategory(ProductCategory.fromValue(i.productCategory()))
                        .productName(i.productName())
                        .quantity(i.quantity())
                        .unit(i.unit())
                        .estimatedPriceEtb(i.estimatedPriceEtb())
                        .sequenceOrder(i.sequenceOrder())
                        .build())
                .collect(Collectors.toList());

        inputNeedItemRepository.saveAll(savedItems);
        savedInputNeed.setItems(savedItems);

        log.info("Input needs submitted: {} items, total: {} ETB",
                savedItems.size(), totalAmount);

        eventPublisher.publishInputNeedsCreated(farm, savedInputNeed, cropCycle.getSeasonName());
        return savedInputNeed;
    }

    // =========================================================================
    // View input needs
    // =========================================================================
    @Override
    @Transactional(readOnly = true)
    public List<InputNeed> getInputNeeds(UUID farmId) {
        farmRepository.findById(farmId)
                .orElseThrow(() -> new FarmNotFoundException(farmId.toString()));

        List<InputNeed> inputNeeds = inputNeedRepository.findAllByFarmId(farmId);
        for (InputNeed inputNeed : inputNeeds) {
            inputNeed.setItems(
                    inputNeedItemRepository.findAllByInputNeedId(inputNeed.getId()));
        }
        return inputNeeds;
    }

    // =========================================================================
    // FS-02 — Get farm by ID
    // =========================================================================
    @Override
    @Transactional(readOnly = true)
    public Farm getFarmById(UUID farmId) {
        return farmRepository.findById(farmId)
                .orElseThrow(() -> new FarmNotFoundException(farmId.toString()));
    }

    // =========================================================================
    // FS-03 — Get my farms
    // =========================================================================
    @Override
    @Transactional(readOnly = true)
    public List<Farm> getMyFarms(UUID farmerId) {
        return farmRepository.findByFarmerId(farmerId);
    }

    // =========================================================================
    // FS-05 — Create new crop cycle for a new season
    // =========================================================================
    @Override
    @Transactional
    public CropCycle createCropCycle(UUID farmId,
                                     UUID farmerId,
                                     String seasonName,
                                     LocalDate expectedHarvestDate) {

        log.info("Creating new crop cycle for farm: {}", farmId);

        Farm farm = farmRepository.findById(farmId)
                .orElseThrow(() -> new FarmNotFoundException(farmId.toString()));

        if (!farm.getFarmerId().equals(farmerId)) {
            throw new BusinessException(
                    "Farm does not belong to this farmer",
                    "UNAUTHORIZED_FARM_ACCESS");
        }

        // Reject if an active cycle is already running
        cropCycleRepository.findActiveByFarmId(farmId).ifPresent(existing -> {
            if (existing.getStatus() == CropCycleStatus.PLANNING ||
                    existing.getStatus() == CropCycleStatus.FUNDED   ||
                    existing.getStatus() == CropCycleStatus.PLANTED  ||
                    existing.getStatus() == CropCycleStatus.GROWING) {
                throw new BusinessException(
                        "An active crop cycle already exists for this farm. " +
                                "Complete or close the current season first.",
                        "ACTIVE_CYCLE_EXISTS");
            }
        });

        String resolvedSeasonName = (seasonName != null && !seasonName.isBlank())
                ? seasonName
                : generateSeasonName();

        CropCycle cropCycle = CropCycle.builder()
                .id(UUID.randomUUID())
                .farmId(farmId)
                .seasonName(resolvedSeasonName)
                .expectedHarvestDate(expectedHarvestDate)
                .status(CropCycleStatus.PLANNING)
                .createdAt(LocalDateTime.now())
                .build();

        CropCycle saved = cropCycleRepository.save(cropCycle);
        log.info("Crop cycle created: {} for farm: {}", saved.getId(), farmId);
        return saved;
    }

    // =========================================================================
    // FS-05 — Get all crop cycles for a farm
    // =========================================================================
    @Override
    @Transactional(readOnly = true)
    public List<CropCycle> getCropCycles(UUID farmId) {
        farmRepository.findById(farmId)
                .orElseThrow(() -> new FarmNotFoundException(farmId.toString()));
        return cropCycleRepository.findAllByFarmId(farmId);
    }

    // =========================================================================
    // FS-08 — Digital twin
    // =========================================================================
    @Override
    @Transactional(readOnly = true)
    public FarmDocument getDigitalTwin(UUID farmId) {
        farmRepository.findById(farmId)
                .orElseThrow(() -> new FarmNotFoundException(farmId.toString()));

        return farmDocumentRepository.findByFarmId(farmId.toString())
                .orElseThrow(() -> new BusinessException(
                        "Digital twin not found for farm: " + farmId,
                        "DIGITAL_TWIN_NOT_FOUND"));
    }

    // =========================================================================
    // FS-07 — Confirm planting
    // =========================================================================
    @Override
    @Transactional
    public CropCycle confirmPlanting(UUID farmId,
                                     UUID farmerId,
                                     LocalDate plantingDate) {

        Farm farm = farmRepository.findById(farmId)
                .orElseThrow(() -> new FarmNotFoundException(farmId.toString()));

        if (!farm.getFarmerId().equals(farmerId)) {
            throw new BusinessException(
                    "Farm does not belong to this farmer",
                    "UNAUTHORIZED_FARM_ACCESS");
        }

        CropCycle cropCycle = cropCycleRepository.findActiveByFarmId(farmId)
                .orElseThrow(() -> new BusinessException(
                        "No active crop cycle found for farm: " + farmId,
                        "NO_ACTIVE_CROP_CYCLE"));

        cropCycle.confirmPlanting(plantingDate);
        CropCycle savedCycle = cropCycleRepository.save(cropCycle);

        farm.startGrowing();
        farmRepository.save(farm);

        eventPublisher.publishFarmPlanted(farm, savedCycle);
        log.info("Planting confirmed + farm.planted event published for farm: {}", farmId);
        return savedCycle;
    }

    // =========================================================================
    // FS-09 — Get Agri-Score
    // =========================================================================
    @Override
    @Transactional(readOnly = true)
    public AgriScore getAgriScore(UUID farmId) {
        Farm farm = farmRepository.findById(farmId)
                .orElseThrow(() -> new FarmNotFoundException(farmId.toString()));

        return agriScoreRepository.findLatestByFarmerId(farm.getFarmerId())
                .orElseThrow(() -> new BusinessException(
                        "No agri-score yet. Complete your first season to earn one.",
                        "AGRI_SCORE_NOT_FOUND"));
    }

    // =========================================================================
    // FS-11 — Search farms
    // =========================================================================
    @Override
    @Transactional(readOnly = true)
    public List<Farm> searchFarms(String region,
                                  String cropType,
                                  String status) {
        log.info("Searching farms — region: {}, cropType: {}, status: {}",
                region, cropType, status);
        return farmRepository.searchFarms(
                region   != null && region.isBlank()   ? null : region,
                cropType != null && cropType.isBlank() ? null : cropType,
                status   != null && status.isBlank()   ? null : status);
    }

    // =========================================================================
    // SRS §3.3 — Satellite verified (triggered by SatelliteVerifiedListener)
    // =========================================================================
    @Override
    @Transactional
    public void markSatelliteVerified(UUID farmId,
                                      boolean verified,
                                      double verifiedAreaHectares) {
        log.info("markSatelliteVerified farm={} verified={} area={}ha",
                farmId, verified, verifiedAreaHectares);

        Farm farm = farmRepository.findById(farmId)
                .orElseThrow(() -> new FarmNotFoundException(farmId.toString()));

        if (verified) {
            // SRS §3.3.1: area_hectares is satellite-verified — NOT the farmer's claim
            if (verifiedAreaHectares > 0) {
                farm.setAreaHectares(BigDecimal.valueOf(verifiedAreaHectares)
                        .setScale(4, java.math.RoundingMode.HALF_UP));
            }
            farm.verify(); // sets status=VERIFIED, satelliteVerified=true, satelliteVerifiedAt=now()
        } else {
            // Satellite rejected — not agricultural land
            farm.setStatus(FarmStatus.FAILED);
            farm.setUpdatedAt(LocalDateTime.now());
            log.warn("Farm {} REJECTED by satellite — not agricultural land", farmId);
        }

        farmRepository.save(farm);
        log.info("Farm {} satellite status saved → {}", farmId, farm.getStatus().getValue());
    }

    // =========================================================================
    // SRS §3.3.4 — Agri-Score recalculation (triggered by HarvestConfirmedListener)
    //
    // Formula (max 900 pts):
    //   season_completion_pts   = 100  (flat — any completed season)
    //   contract_fulfillment    = 0-200
    //   repayment_completion    = 0-200
    //   agronomist_assessment   = 0-50  (rating 1-5 → 10pts each)
    //   voucher_discipline      = 0-150 (updated separately by voucher-service)
    //   yield_accuracy          = 0-200 (updated separately by geospatial-service)
    // =========================================================================
    @Override
    @Transactional
    public void calculateAndSaveAgriScore(UUID farmId,
                                          UUID cropCycleId,
                                          boolean contractFulfilled,
                                          boolean repaymentCompleted,
                                          int agronomistRating) {

        log.info("Calculating Agri-Score — farm: {}, cropCycle: {}", farmId, cropCycleId);

        Farm farm = farmRepository.findById(farmId)
                .orElseThrow(() -> new FarmNotFoundException(farmId.toString()));

        // SRS §3.3.4 — flat 100 pts for completing any season
        int seasonCompletionPts = 100;

        // SRS §3.3.4 — 0-200 pts for fulfilling off-taker contract
        int contractFulfillmentPts = contractFulfilled ? 200 : 0;

        // SRS §3.3.4 — 0-200 pts for completing investor repayment
        int repaymentCompletionPts = repaymentCompleted ? 200 : 0;

        // SRS §3.3.4 — 0-50 pts from agronomist rating (rating 1-5 → 10 pts each)
        // Clamp between 0 and 50 defensively
        int agronomistAssessmentPts = Math.min(50, Math.max(0, agronomistRating * 10));

        // voucher_discipline_pts (max 150) — set to 0 here, updated by voucher-service
        // yield_accuracy_pts     (max 200) — set to 0 here, updated by geospatial-service
        int voucherDisciplinePts = 0;
        int yieldAccuracyPts     = 0;

        int totalScore = seasonCompletionPts
                + contractFulfillmentPts
                + repaymentCompletionPts
                + agronomistAssessmentPts
                + voucherDisciplinePts
                + yieldAccuracyPts;

        // SRS §3.3.1: agri_score CHECK (agri_score >= 0 AND agri_score <= 900)
        totalScore = Math.min(900, Math.max(0, totalScore));

        AgriScore agriScore = AgriScore.builder()
                .id(UUID.randomUUID())
                .farmerId(farm.getFarmerId())
                .cropCycleId(cropCycleId)
                .score(totalScore)
                .voucherDisciplinePts(voucherDisciplinePts)
                .yieldAccuracyPts(yieldAccuracyPts)
                .contractFulfillmentPts(contractFulfillmentPts)
                .repaymentCompletionPts(repaymentCompletionPts)
                .seasonCompletionPts(seasonCompletionPts)
                .agronomistAssessmentPts(agronomistAssessmentPts)
                .calculatedAt(LocalDateTime.now())
                .build();

        agriScoreRepository.save(agriScore);
        log.info("Agri-Score saved: {} pts for farmer: {} (farm: {})",
                totalScore, farm.getFarmerId(), farmId);

        // Mark crop cycle as HARVESTED and record actual harvest date
        cropCycleRepository.findById(cropCycleId).ifPresent(cycle -> {
            cycle.setStatus(CropCycleStatus.HARVESTED);
            cycle.setActualHarvestDate(LocalDate.now());
            cropCycleRepository.save(cycle);
            log.info("CropCycle {} marked HARVESTED", cropCycleId);
        });

        // Move farm to HARVESTED status
        farm.setStatus(FarmStatus.HARVESTED);
        farm.setUpdatedAt(LocalDateTime.now());
        farmRepository.save(farm);
        log.info("Farm {} status → HARVESTED", farmId);
    }

    // =========================================================================
    // Private helpers
    // =========================================================================
    private String generateSeasonName() {
        int year  = LocalDate.now().getYear();
        int month = LocalDate.now().getMonthValue();
        String season = (month >= 6 && month <= 9) ? "Kiremt" :
                (month >= 2 && month <= 5) ? "Belg"   : "Bega";
        return season + "_" + year;
    }
}