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
    private final PhotoStoragePort photoStoragePort;
    private final EventPublisherPort eventPublisher;

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

        // Step 1: Verify farmer exists via gRPC to user-service
        boolean farmerExists = userServicePort.verifyFarmerExists(farmerId);
        if (!farmerExists) {
            throw new BusinessException(
                "Farmer not found with id: " + farmerId, "FARMER_NOT_FOUND");
        }

        // Step 2: Parse and validate GeoJSON polygon
        if (geoJsonPolygon == null || geoJsonPolygon.isBlank()) {
            throw new BusinessException(
                "Farm GPS polygon is required", "MISSING_POLYGON");
        }

        // Step 3: Calculate centroid from polygon
        // Simplified centroid — geospatial service does the satellite verification
        BigDecimal[] centroid = calculateCentroid(geoJsonPolygon);

        // Step 4: Create farm — area set to 0 until satellite verifies
        // SRS Page 18: area_hectares is satellite-verified, NOT farmer claim
        Farm farm = Farm.builder()
            .id(UUID.randomUUID())
            .farmerId(farmerId)
            .farmName(farmName)
            .cropType(CropType.fromValue(cropType))
            .areaHectares(BigDecimal.ZERO)
            .status(FarmStatus.PENDING_VERIFICATION)
            .kebeleCode(kebeleCode)
            .region(region)
            .gpsCentroidLat(centroid[0])
            .gpsCentroidLng(centroid[1])
            .geoJsonPolygon(geoJsonPolygon)
            .satelliteVerified(false)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        Farm savedFarm = farmRepository.save(farm);
        log.info("Farm saved with ID: {}", savedFarm.getId());

        // Step 5: Create initial crop cycle in PLANNING status
        CropCycle cropCycle = CropCycle.builder()
            .id(UUID.randomUUID())
            .farmId(savedFarm.getId())
            .seasonName(generateSeasonName())
            .expectedHarvestDate(expectedHarvestDate)
            .status(CropCycleStatus.PLANNING)
            .createdAt(LocalDateTime.now())
            .build();

        cropCycleRepository.save(cropCycle);
        log.info("Crop cycle created for farm: {}", savedFarm.getId());

        // Step 6: Create MongoDB digital twin document
        FarmDocument farmDocument = FarmDocument.builder()
            .farmId(savedFarm.getId().toString())
            .geoJsonPolygon(geoJsonPolygon)
            .build();

        farmDocumentRepository.save(farmDocument);
        log.info("Farm digital twin created in MongoDB for farm: {}", savedFarm.getId());

        // Step 7: Publish farm.registered event
        // SRS Section 5.2 — triggers geospatial-service to start monitoring
        eventPublisher.publishFarmRegistered(savedFarm);

        return savedFarm;
    }

    @Override
    @Transactional
    public FarmPhoto uploadPhoto(UUID farmId,
                                 UUID farmerId,
                                 MultipartFile photo,
                                 String photoType) {

        log.info("Uploading photo for farm: {}, type: {}", farmId, photoType);

        // Step 1: Verify farm exists and belongs to this farmer
        Farm farm = farmRepository.findById(farmId)
            .orElseThrow(() -> new FarmNotFoundException(farmId.toString()));

        if (!farm.getFarmerId().equals(farmerId)) {
            throw new BusinessException(
                "Farm does not belong to this farmer", "UNAUTHORIZED_FARM_ACCESS");
        }

        // Step 2: Extract GPS from EXIF — for now use farm centroid as fallback
        // Real EXIF extraction would use metadata-extractor library
        BigDecimal gpsLat = farm.getGpsCentroidLat();
        BigDecimal gpsLng = farm.getGpsCentroidLng();

        // Step 3: Upload photo to MinIO (stubbed)
        String photoUrl = photoStoragePort.uploadPhoto(farmId, photoType, photo);

        // Step 4: Save photo record
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

        // Step 5: Call fraud-service GPS verification (SRS Page 22)
        FraudServicePort.GpsVerificationResult gpsResult =
            fraudServicePort.verifyGpsConsistency(
                farmId, gpsLat, gpsLng, farm.getGeoJsonPolygon());

        if (gpsResult.isConsistent()) {
            savedPhoto.markGpsVerified();
            farmPhotoRepository.save(savedPhoto);
            log.info("GPS verified for photo: {}", savedPhoto.getId());
        } else {
            log.warn("GPS mismatch for photo: {}, distance: {}m",
                savedPhoto.getId(), gpsResult.distanceFromBoundaryMeters());
        }

        // Step 6: Append to MongoDB digital twin photo history
        farmDocumentRepository.appendPhotoRecord(farmId.toString(),
            FarmDocument.PhotoRecord.builder()
                .photoId(savedPhoto.getId().toString())
                .type(photoType)
                .url(photoUrl)
                .lat(gpsLat.doubleValue())
                .lng(gpsLng.doubleValue())
                .uploadedAt(savedPhoto.getUploadedAt().toString())
                .build());

        // Step 7: Publish crop.photo.uploaded event
        eventPublisher.publishCropPhotoUploaded(savedPhoto, farm);

        return savedPhoto;
    }

    @Override
    @Transactional
    public InputNeed submitInputNeeds(UUID farmId,
                                      UUID farmerId,
                                      UUID cropCycleId,
                                      List<InputNeedItemRequest> items) {

        log.info("Submitting input needs for farm: {}", farmId);

        // Step 1: Verify farm exists and belongs to farmer
        Farm farm = farmRepository.findById(farmId)
            .orElseThrow(() -> new FarmNotFoundException(farmId.toString()));

        if (!farm.getFarmerId().equals(farmerId)) {
            throw new BusinessException(
                "Farm does not belong to this farmer", "UNAUTHORIZED_FARM_ACCESS");
        }

        // Step 2: Verify crop cycle exists
        CropCycle cropCycle = cropCycleRepository.findById(cropCycleId)
            .orElseThrow(() -> new BusinessException(
                "Crop cycle not found: " + cropCycleId, "CROP_CYCLE_NOT_FOUND"));

        if (!cropCycle.getFarmId().equals(farmId)) {
            throw new BusinessException(
                "Crop cycle does not belong to this farm", "INVALID_CROP_CYCLE");
        }

        // Step 3: Validate items are not empty
        if (items == null || items.isEmpty()) {
            throw new BusinessException(
                "At least one input need item is required", "EMPTY_INPUT_NEEDS");
        }

        // Step 4: Calculate total amount
        BigDecimal totalAmount = items.stream()
            .map(InputNeedItemRequest::estimatedPriceEtb)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Step 5: Create InputNeed record
        InputNeed inputNeed = InputNeed.builder()
            .id(UUID.randomUUID())
            .farmId(farmId)
            .cropCycleId(cropCycleId)
            .totalAmountEtb(totalAmount)
            .fundedAmountEtb(BigDecimal.ZERO)
            .status(InputNeedStatus.OPEN)
            .createdAt(LocalDateTime.now())
            .build();

        InputNeed savedInputNeed = inputNeedRepository.save(inputNeed);

        // Step 6: Create and save each InputNeedItem
        List<InputNeedItem> savedItems = items.stream()
            .map(itemReq -> InputNeedItem.builder()
                .id(UUID.randomUUID())
                .inputNeedId(savedInputNeed.getId())
                .productCategory(ProductCategory.fromValue(itemReq.productCategory()))
                .productName(itemReq.productName())
                .quantity(itemReq.quantity())
                .unit(itemReq.unit())
                .estimatedPriceEtb(itemReq.estimatedPriceEtb())
                .sequenceOrder(itemReq.sequenceOrder())
                .build())
            .collect(Collectors.toList());

        inputNeedItemRepository.saveAll(savedItems);
        savedInputNeed.setItems(savedItems);

        log.info("Input needs saved: {} items, total: {} ETB",
            savedItems.size(), totalAmount);

        // Step 7: Publish input.needs.created event
        eventPublisher.publishInputNeedsCreated(farm, savedInputNeed);

        return savedInputNeed;
    }

    @Override
    @Transactional(readOnly = true)
    public Farm getFarmById(UUID farmId) {
        return farmRepository.findById(farmId)
            .orElseThrow(() -> new FarmNotFoundException(farmId.toString()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Farm> getMyFarms(UUID farmerId) {
        log.info("Getting all farms for farmer: {}", farmerId);
        return farmRepository.findByFarmerId(farmerId);
    }

    @Override
    @Transactional(readOnly = true)
    public FarmDocument getDigitalTwin(UUID farmId) {
        log.info("Getting digital twin for farm: {}", farmId);

        // Verify farm exists first
        farmRepository.findById(farmId)
            .orElseThrow(() -> new FarmNotFoundException(farmId.toString()));

        return farmDocumentRepository.findByFarmId(farmId.toString())
            .orElseThrow(() -> new BusinessException(
                "Digital twin not found for farm: " + farmId,
                "DIGITAL_TWIN_NOT_FOUND"));
    }

    @Override
    @Transactional
    public CropCycle confirmPlanting(UUID farmId,
                                     UUID farmerId,
                                     LocalDate plantingDate) {

        log.info("Confirming planting for farm: {}, date: {}", farmId, plantingDate);

        // Step 1: Verify farm
        Farm farm = farmRepository.findById(farmId)
            .orElseThrow(() -> new FarmNotFoundException(farmId.toString()));

        if (!farm.getFarmerId().equals(farmerId)) {
            throw new BusinessException(
                "Farm does not belong to this farmer", "UNAUTHORIZED_FARM_ACCESS");
        }

        // Step 2: Find active crop cycle
        CropCycle cropCycle = cropCycleRepository.findActiveByFarmId(farmId)
            .orElseThrow(() -> new BusinessException(
                "No active crop cycle found for farm: " + farmId,
                "NO_ACTIVE_CROP_CYCLE"));

        // Step 3: Confirm planting
        cropCycle.confirmPlanting(plantingDate);
        CropCycle savedCycle = cropCycleRepository.save(cropCycle);

        // Step 4: Update farm status to GROWING
        farm.startGrowing();
        farmRepository.save(farm);

        log.info("Planting confirmed for farm: {}, cycle: {}",
            farmId, savedCycle.getId());

        return savedCycle;
    }

    @Override
    @Transactional(readOnly = true)
    public AgriScore getAgriScore(UUID farmId) {
        log.info("Getting agri-score for farm: {}", farmId);

        Farm farm = farmRepository.findById(farmId)
            .orElseThrow(() -> new FarmNotFoundException(farmId.toString()));

        return agriScoreRepository.findLatestByFarmerId(farm.getFarmerId())
            .orElseThrow(() -> new BusinessException(
                "No agri-score found for farmer of farm: " + farmId,
                "AGRI_SCORE_NOT_FOUND"));
    }

    // SRS Page 23 — Agri-Score calculation triggered at season completion
    @Transactional
    public AgriScore calculateAndSaveAgriScore(UUID farmerId,
                                                UUID cropCycleId,
                                                int voucherDisciplinePts,
                                                int yieldAccuracyPts,
                                                int contractFulfillmentPts,
                                                int repaymentCompletionPts,
                                                int agronomistAssessmentPts) {

        // Flat 100 points for completing any season
        int seasonCompletionPts = 100;

        int totalScore = AgriScore.calculate(
            voucherDisciplinePts,
            yieldAccuracyPts,
            contractFulfillmentPts,
            repaymentCompletionPts,
            seasonCompletionPts,
            agronomistAssessmentPts);

        AgriScore agriScore = AgriScore.builder()
            .id(UUID.randomUUID())
            .farmerId(farmerId)
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

        AgriScore saved = agriScoreRepository.save(agriScore);

        // SRS Page 23 — update cumulative score on user-service via gRPC
        double cumulativeScore = agriScoreRepository.calculateCumulativeScore(farmerId);
        userServicePort.updateAgriScore(farmerId, (int) cumulativeScore, cropCycleId);

        log.info("Agri-Score calculated for farmer: {}, score: {}", farmerId, totalScore);

        return saved;
    }

    // Simplified centroid calculation from GeoJSON polygon string
    // Real calculation done by geospatial-service after satellite verification
    private BigDecimal[] calculateCentroid(String geoJsonPolygon) {
        // Return placeholder centroid — geospatial-service will correct this
        // after satellite verification via the farm.registered event
        return new BigDecimal[]{
            new BigDecimal("9.0300"),   // approximate Ethiopia center lat
            new BigDecimal("38.7400")   // approximate Ethiopia center lng
        };
    }

    private String generateSeasonName() {
        int year = LocalDate.now().getYear();
        int month = LocalDate.now().getMonthValue();
        // Ethiopian seasons: Kiremt (Jun-Sep), Belg (Feb-May), Bega (Oct-Jan)
        String season = (month >= 6 && month <= 9) ? "Kiremt" :
                        (month >= 2 && month <= 5) ? "Belg" : "Bega";
        return season + "_" + year;
    }
}
