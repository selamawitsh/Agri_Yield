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

        boolean farmerExists = userServicePort.verifyFarmerExists(farmerId);
        if (!farmerExists) {
            log.warn("Farmer not found via gRPC, proceeding (stub mode)");
        }

        if (geoJsonPolygon == null || geoJsonPolygon.isBlank()) {
            throw new BusinessException(
                "Farm GPS polygon is required", "MISSING_POLYGON");
        }

        BigDecimal[] centroid = calculateCentroid(geoJsonPolygon);

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
        eventPublisher.publishFarmRegistered(savedFarm);

        return savedFarm;
    }

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
        String photoUrl = photoStoragePort.uploadPhoto(farmId, photoType, photo);

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

        eventPublisher.publishInputNeedsCreated(farm, savedInputNeed);
        return savedInputNeed;
    }

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

    @Override
    @Transactional(readOnly = true)
    public Farm getFarmById(UUID farmId) {
        return farmRepository.findById(farmId)
            .orElseThrow(() -> new FarmNotFoundException(farmId.toString()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Farm> getMyFarms(UUID farmerId) {
        return farmRepository.findByFarmerId(farmerId);
    }

    // FS-05 — Create new crop cycle for a new season
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

        // Check no active cycle already running
        cropCycleRepository.findActiveByFarmId(farmId).ifPresent(existing -> {
            if (existing.getStatus() == com.agriyield.farmservice.domain.enums
                    .CropCycleStatus.PLANNING ||
                existing.getStatus() == com.agriyield.farmservice.domain.enums
                    .CropCycleStatus.FUNDED ||
                existing.getStatus() == com.agriyield.farmservice.domain.enums
                    .CropCycleStatus.PLANTED ||
                existing.getStatus() == com.agriyield.farmservice.domain.enums
                    .CropCycleStatus.GROWING) {
                throw new BusinessException(
                    "An active crop cycle already exists for this farm. " +
                    "Complete or close the current season first.",
                    "ACTIVE_CYCLE_EXISTS");
            }
        });

        // Use auto-generated season name if not provided
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

    // FS-05 — Get all crop cycles for a farm (full history)
    @Override
    @Transactional(readOnly = true)
    public List<CropCycle> getCropCycles(UUID farmId) {
        farmRepository.findById(farmId)
            .orElseThrow(() -> new FarmNotFoundException(farmId.toString()));
        return cropCycleRepository.findAllByFarmId(farmId);
    }

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

        log.info("Planting confirmed for farm: {}", farmId);
        return savedCycle;
    }

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

    // FS-11 — Search farms by region, crop type, status
    @Override
    @Transactional(readOnly = true)
    public List<Farm> searchFarms(String region,
                                   String cropType,
                                   String status) {
        log.info("Searching farms — region: {}, cropType: {}, status: {}",
            region, cropType, status);
        return farmRepository.searchFarms(
            region != null && region.isBlank() ? null : region,
            cropType != null && cropType.isBlank() ? null : cropType,
            status != null && status.isBlank() ? null : status);
    }

    private BigDecimal[] calculateCentroid(String geoJsonPolygon) {
        return new BigDecimal[]{
            new BigDecimal("9.0300"),
            new BigDecimal("38.7400")
        };
    }

    private String generateSeasonName() {
        int year = LocalDate.now().getYear();
        int month = LocalDate.now().getMonthValue();
        String season = (month >= 6 && month <= 9) ? "Kiremt" :
                        (month >= 2 && month <= 5) ? "Belg" : "Bega";
        return season + "_" + year;
    }
}
