package com.agriyield.geospatialservice.application.service;

import com.agriyield.geospatialservice.application.port.incoming.GeospatialServicePort;
import com.agriyield.geospatialservice.application.port.outgoing.*;
import com.agriyield.geospatialservice.domain.exception.BusinessException;
import com.agriyield.geospatialservice.domain.exception.FarmNotFoundException;
import com.agriyield.geospatialservice.domain.model.FarmBoundary;
import com.agriyield.geospatialservice.domain.model.NdviReading;
import com.agriyield.geospatialservice.domain.model.YieldPrediction;
import com.agriyield.geospatialservice.domain.util.GeoJsonPolygonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeospatialServiceImpl implements GeospatialServicePort {

    private final NdviReadingRepositoryPort ndviRepository;
    private final YieldPredictionRepositoryPort yieldRepository;
    private final FarmBoundaryRepositoryPort boundaryRepository;
    private final CopernicusClientPort copernicusClient;
    private final FarmServicePort farmService;
    private final WeatherServicePort weatherService;
    private final AiServicePort aiService;
    private final RedisPort redis;
    private final GeospatialEventPublisherPort eventPublisher;

    @Value("${app.ndvi.cache-ttl-days:5}")
    private int ndviCacheTtlDays;

    @Value("${app.yield.harvest-readiness-ndvi-drop:0.08}")
    private double harvestNdviDropThreshold;

    @Value("${app.yield.harvest-readiness-days:10}")
    private int harvestReadinessDays;

    // ── StartMonitoring ───────────────────────────────────────────────────────
    // FIX: no longer calls gRPC on startup — avoids race condition where
    // geospatial-service receives the RabbitMQ event before farm-service
    // has committed the DB transaction.
    @Override
    public boolean startMonitoring(UUID farmId) {
        log.info("GS: startMonitoring farm={}", farmId);
        try {
            // Step 1: ensure GPS is in Redis
            // Priority: Redis cache → MongoDB boundary → gRPC (last resort only)
            Optional<String> cachedGps = redis.get("farm:gps:" + farmId);

            if (cachedGps.isEmpty()) {
                Optional<FarmBoundary> boundary = boundaryRepository.findByFarmId(farmId);
                if (boundary.isPresent()) {
                    // registerFarmPolygon() already saved boundary — use it, no gRPC needed
                    redis.set("farm:gps:" + farmId,
                            boundary.get().getCentroidLat() + "," + boundary.get().getCentroidLng(),
                            Duration.ofDays(365));
                    log.info("GS: GPS cached from MongoDB boundary for farm={}", farmId);
                } else {
                    // Boundary not saved yet — scheduler will retry in 5 days
                    log.warn("GS: no boundary found for farm={} — skipping initial NDVI sync." +
                            " Scheduler will retry.", farmId);
                    return true;
                }
            }

            // Step 2: first NDVI sync — failure must NOT block monitoring start
            try {
                syncNdviForFarm(farmId);
            } catch (Exception e) {
                log.warn("GS: initial NDVI sync failed for farm={}: {} — scheduler will retry",
                        farmId, e.getMessage());
            }

            log.info("GS: monitoring started for farm={}", farmId);
            return true;

        } catch (Exception e) {
            log.error("GS: startMonitoring failed for farm={}: {}", farmId, e.getMessage());
            return false;
        }
    }

    // ── GetLatestNdvi ─────────────────────────────────────────────────────────
    @Override
    public NdviReading getLatestNdvi(UUID farmId) {
        log.info("GS: getLatestNdvi farm={}", farmId);

        Optional<String> cached = redis.get("ndvi:latest:" + farmId);
        if (cached.isPresent()) {
            try {
                Double.parseDouble(cached.get()); // validate it is a number
                return ndviRepository.findLatestByFarmId(farmId)
                        .orElseThrow(() -> new FarmNotFoundException(farmId.toString()));
            } catch (NumberFormatException ignored) {}
        }

        return ndviRepository.findLatestByFarmId(farmId)
                .orElseThrow(() -> new FarmNotFoundException(farmId.toString()));
    }

    // ── GetNdviTimeSeries ─────────────────────────────────────────────────────
    @Override
    public List<NdviReading> getNdviTimeSeries(UUID farmId, int limitDays) {
        log.info("GS: getNdviTimeSeries farm={} days={}", farmId, limitDays);
        LocalDate since = LocalDate.now().minusDays(limitDays);
        return ndviRepository.findByFarmIdSince(farmId, since);
    }

    // ── PredictHarvestReadiness ───────────────────────────────────────────────
    @Override
    public HarvestReadinessResult predictHarvestReadiness(UUID farmId) {
        log.info("GS: predictHarvestReadiness farm={}", farmId);

        List<NdviReading> recent = ndviRepository
                .findByFarmIdOrderByDateDesc(farmId, harvestReadinessDays);

        if (recent.size() < 2) {
            return new HarvestReadinessResult(false, null, null, 0, 0,
                    "INSUFFICIENT_DATA");
        }

        NdviReading latest = recent.get(0);
        double currentNdvi = latest.getNdviValue();

        double peakNdvi = recent.stream()
                .mapToDouble(NdviReading::getNdviValue)
                .max().orElse(currentNdvi);

        boolean ready = NdviReading.isHarvestReady(peakNdvi, currentNdvi,
                harvestNdviDropThreshold);

        if (ready) {
            String dateFrom = LocalDate.now().plusWeeks(1).toString();
            String dateTo   = LocalDate.now().plusWeeks(3).toString();
            return new HarvestReadinessResult(true, dateFrom, dateTo,
                    currentNdvi, peakNdvi, "NDVI_DECLINING");
        }

        return new HarvestReadinessResult(false, null, null,
                currentNdvi, peakNdvi, "STILL_GROWING");
    }

    // ── GetFarmContext ────────────────────────────────────────────────────────
    @Override
    public FarmContext getFarmContext(UUID farmId) {
        log.info("GS: getFarmContext farm={}", farmId);

        FarmServicePort.FarmInfo farm = farmService.getFarmContext(farmId);

        double currentNdvi = 0.0;
        String ndviHealth  = "UNKNOWN";
        try {
            NdviReading ndvi = getLatestNdvi(farmId);
            currentNdvi = ndvi.getNdviValue();
            ndviHealth  = ndvi.getHealthStatus();
        } catch (Exception e) {
            log.warn("GS: no NDVI data for farm={}", farmId);
        }

        double yieldMean = 0.0;
        int yieldConf    = 0;
        try {
            Optional<YieldPrediction> yp = yieldRepository.findLatestByFarmId(farmId);
            if (yp.isPresent()) {
                yieldMean = yp.get().getTotalYieldMeanQuintals();
                yieldConf = yp.get().getConfidencePct();
            }
        } catch (Exception e) {
            log.warn("GS: no yield prediction for farm={}", farmId);
        }

        return new FarmContext(
                farm.farmId(), farm.farmerId(), farm.cropType(),
                farm.areaHectares(), farm.region(), farm.kebeleCode(),
                farm.gpsCentroidLat(), farm.gpsCentroidLng(),
                farm.agriScore(), farm.cropCycleId(),
                farm.seasonName(), farm.cropCycleStatus(),
                currentNdvi, ndviHealth, yieldMean, yieldConf
        );
    }

    // ── VerifyFarmBoundary ────────────────────────────────────────────────────
    @Override
    public BoundaryVerificationResult verifyFarmBoundary(UUID farmId,
                                                         double photoLat,
                                                         double photoLng) {
        log.info("GS: verifyFarmBoundary farm={} lat={} lng={}", farmId, photoLat, photoLng);

        Optional<FarmBoundary> boundary = boundaryRepository.findByFarmId(farmId);
        if (boundary.isEmpty()) {
            try {
                FarmServicePort.FarmInfo farm = farmService.getFarmById(farmId);
                double distKm = FarmBoundary.distanceKm(
                        photoLat, photoLng,
                        farm.gpsCentroidLat(), farm.gpsCentroidLng());
                double distM = distKm * 1000;
                return new BoundaryVerificationResult(distM <= 500, distM, "");
            } catch (Exception e) {
                return new BoundaryVerificationResult(false, -1, "");
            }
        }

        FarmBoundary fb = boundary.get();
        try {
            List<double[]> ring = GeoJsonPolygonUtils.parseExteriorRing(fb.getGeoJsonPolygon());
            boolean within = GeoJsonPolygonUtils.isPointInPolygon(photoLat, photoLng, ring);
            double distKm = FarmBoundary.distanceKm(
                    photoLat, photoLng, fb.getCentroidLat(), fb.getCentroidLng());
            return new BoundaryVerificationResult(within, distKm * 1000, fb.getGeoJsonPolygon());
        } catch (Exception e) {
            double distKm = FarmBoundary.distanceKm(
                    photoLat, photoLng, fb.getCentroidLat(), fb.getCentroidLng());
            double distM = distKm * 1000;
            return new BoundaryVerificationResult(distM <= 500, distM, fb.getGeoJsonPolygon());
        }
    }

    // ── SyncNdviForFarm ───────────────────────────────────────────────────────
    // FIX: uses MongoDB boundary for GPS instead of gRPC call to farm-service.
    // gRPC is only used as a last resort when boundary is not yet in MongoDB.
    @Override
    public NdviReading syncNdviForFarm(UUID farmId) {
        log.info("GS: syncNdviForFarm farm={}", farmId);

        Optional<FarmBoundary> boundary = boundaryRepository.findByFarmId(farmId);

        double lat, lng;
        String geoJson;

        if (boundary.isPresent()) {
            lat     = boundary.get().getCentroidLat();
            lng     = boundary.get().getCentroidLng();
            geoJson = boundary.get().getGeoJsonPolygon();
            log.debug("GS: using MongoDB boundary GPS for farm={}", farmId);
        } else {
            // Boundary not stored yet — fall back to gRPC
            log.warn("GS: no boundary in MongoDB for farm={}, falling back to gRPC", farmId);
            FarmServicePort.FarmInfo farm = farmService.getFarmById(farmId);
            lat     = farm.gpsCentroidLat();
            lng     = farm.gpsCentroidLng();
            geoJson = null;
        }

        NdviReading reading = copernicusClient.fetchNdvi(farmId, lat, lng, geoJson);

        if (reading == null) {
            log.warn("GS: no Copernicus data for farm={} (cloud cover or no scene)", farmId);
            return null;
        }

        reading.setFarmId(farmId);
        reading.setId(UUID.randomUUID());
        reading.setCreatedAt(LocalDateTime.now());

        NdviReading saved = ndviRepository.save(reading);

        // Cache latest NDVI value — TTL 5 days (SRS §3.6.1)
        redis.set("ndvi:latest:" + farmId,
                String.valueOf(saved.getNdviValue()),
                Duration.ofDays(ndviCacheTtlDays));

        // Calculate change from previous reading for the event payload
        double changeFromPrev = 0.0;
        Optional<NdviReading> prev = ndviRepository.findLatestByFarmId(farmId);
        if (prev.isPresent() && !prev.get().getId().equals(saved.getId())) {
            changeFromPrev = saved.getNdviValue() - prev.get().getNdviValue();
        }

        eventPublisher.publishNdviUpdated(saved, changeFromPrev);

        // SRS §5.2: publish farm.satellite.verified so farm-service updates status → VERIFIED
        // NDVI > 0.05 confirms this is real land (not water or dense urban area)
        try {
            String verificationStatus = saved.getNdviValue() > 0.05 ? "VERIFIED" : "REJECTED";
            double areaHa = 0.0;
            Optional<FarmBoundary> boundaryOpt = boundaryRepository.findByFarmId(farmId);
            if (boundaryOpt.isPresent()) {
                areaHa = boundaryOpt.get().getAreaSqKm() * 100.0;
            }
            eventPublisher.publishSatelliteVerified(
                    farmId, areaHa, saved.getNdviValue(), verificationStatus);
            log.info("GS: satellite verification published farm={} status={} area={}ha",
                    farmId, verificationStatus, areaHa);
        } catch (Exception e) {
            log.error("GS: failed to publish satellite verification for farm={}: {}",
                    farmId, e.getMessage());
        }

        log.info("GS: NDVI synced for farm={} value={}", farmId, saved.getNdviValue());
        return saved;
    }

    // ── SRS §3.6.3: Scheduled — sync all active farms every 5 days ───────────
    @Override
    public void syncNdviAllActiveFarms() {
        log.info("GS: SCHEDULER → syncNdviAllActiveFarms");
        List<UUID> farmIds = ndviRepository.findAllActiveFarmIds();
        int success = 0, skipped = 0;
        for (UUID farmId : farmIds) {
            try {
                NdviReading r = syncNdviForFarm(farmId);
                if (r != null) success++; else skipped++;
            } catch (Exception e) {
                log.error("GS: NDVI sync failed for farm={}: {}", farmId, e.getMessage());
            }
        }
        log.info("GS: NDVI sync complete → success={} skipped={}", success, skipped);
    }

    // ── SRS §3.6.3: Weekly yield predictions ─────────────────────────────────
    @Override
    public void runWeeklyYieldPredictions() {
        log.info("GS: SCHEDULER → runWeeklyYieldPredictions");
        List<UUID> farmIds = ndviRepository.findAllActiveFarmIds();
        for (UUID farmId : farmIds) {
            try {
                runYieldPredictionForFarm(farmId);
            } catch (Exception e) {
                log.error("GS: yield prediction failed for farm={}: {}", farmId, e.getMessage());
            }
        }
    }

    // ── SRS §3.6.3: Harvest readiness detection every 2 days ─────────────────
    @Override
    public void runHarvestReadinessDetection() {
        log.info("GS: SCHEDULER → runHarvestReadinessDetection");
        List<UUID> farmIds = ndviRepository.findAllActiveFarmIds();
        for (UUID farmId : farmIds) {
            try {
                HarvestReadinessResult result = predictHarvestReadiness(farmId);
                if (result.ready()) {
                    eventPublisher.publishHarvestPredicted(
                            farmId,
                            result.estimatedDateFrom(),
                            result.estimatedDateTo(),
                            result.currentNdvi()
                    );
                    log.info("GS: harvest ready signal for farm={}", farmId);
                }
            } catch (Exception e) {
                log.error("GS: harvest detection failed for farm={}: {}", farmId, e.getMessage());
            }
        }
    }

    // ── RegisterFarmPolygon ───────────────────────────────────────────────────
    @Override
    public RegisterPolygonResult registerFarmPolygon(UUID farmId, String geoJsonPolygon,
                                                     double centroidLat, double centroidLng,
                                                     Double areaHectares) {
        PolygonValidationResult validation = validatePolygon(geoJsonPolygon);
        if (!validation.valid()) {
            return new RegisterPolygonResult(false, validation.message(), 0);
        }

        double areaHa   = areaHectares != null && areaHectares > 0
                ? areaHectares : validation.areaHectares();
        double areaSqKm = areaHa / 100.0;

        FarmBoundary boundary = FarmBoundary.builder()
                .farmId(farmId)
                .geoJsonPolygon(geoJsonPolygon)
                .centroidLat(centroidLat > 0 ? centroidLat : validation.centroidLat())
                .centroidLng(centroidLng > 0 ? centroidLng : validation.centroidLng())
                .areaSqKm(areaSqKm)
                .satelliteVerified(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // FarmBoundaryRepositoryAdapter.save() now does upsert — no duplicates
        boundaryRepository.save(boundary);

        redis.set("farm:gps:" + farmId,
                boundary.getCentroidLat() + "," + boundary.getCentroidLng(),
                Duration.ofDays(365));

        return new RegisterPolygonResult(true, "Polygon stored", areaHa);
    }

    // ── ValidatePolygon ───────────────────────────────────────────────────────
    @Override
    public PolygonValidationResult validatePolygon(String geoJsonPolygon) {
        GeoJsonPolygonUtils.ValidationResult r = GeoJsonPolygonUtils.validate(geoJsonPolygon);
        if (!r.valid()) {
            return new PolygonValidationResult(false, r.message(), 0, 0, 0);
        }
        GeoJsonPolygonUtils.PolygonData data = r.polygonData();
        return new PolygonValidationResult(true, r.message(),
                data.areaHectares(), data.centroidLat(), data.centroidLng());
    }

    // ── CalculateFarmArea ─────────────────────────────────────────────────────
    @Override
    public FarmAreaResult calculateFarmArea(UUID farmId) {
        FarmBoundary boundary = boundaryRepository.findByFarmId(farmId)
                .orElseThrow(() -> new FarmNotFoundException(farmId.toString()));
        try {
            List<double[]> ring = GeoJsonPolygonUtils.parseExteriorRing(
                    boundary.getGeoJsonPolygon());
            double areaHa = GeoJsonPolygonUtils.calculateAreaHectares(ring);
            return new FarmAreaResult(areaHa, areaHa / 100.0);
        } catch (Exception e) {
            throw new BusinessException(
                    "Could not calculate area: " + e.getMessage(), "AREA_CALC_ERROR");
        }
    }

    // ── DetectSpatialOverlap ──────────────────────────────────────────────────
    @Override
    public SpatialOverlapResult detectSpatialOverlap(UUID farmId, String geoJsonPolygon,
                                                     double centroidLat, double centroidLng) {
        PolygonValidationResult validation = validatePolygon(geoJsonPolygon);
        if (!validation.valid()) {
            return new SpatialOverlapResult(false, 0, null, validation.message());
        }

        List<double[]> newRing;
        try {
            newRing = GeoJsonPolygonUtils.parseExteriorRing(geoJsonPolygon);
        } catch (Exception e) {
            return new SpatialOverlapResult(false, 0, null, e.getMessage());
        }

        final double overlapThresholdPct = 15.0;
        for (FarmBoundary existing : boundaryRepository.findAll()) {
            if (existing.getFarmId().equals(farmId)) continue;
            try {
                List<double[]> existingRing = GeoJsonPolygonUtils.parseExteriorRing(
                        existing.getGeoJsonPolygon());
                double overlapPct = estimateOverlapPercentage(newRing, existingRing);
                if (overlapPct >= overlapThresholdPct) {
                    return new SpatialOverlapResult(true, overlapPct,
                            existing.getFarmId(),
                            "Farm boundary overlaps existing farm by "
                                    + String.format("%.1f%%", overlapPct));
                }
                if (GeoJsonPolygonUtils.isPointInPolygon(centroidLat, centroidLng, existingRing)) {
                    return new SpatialOverlapResult(true, 100.0,
                            existing.getFarmId(),
                            "Farm centroid falls inside an existing registered farm");
                }
            } catch (Exception ignored) {
                double distKm = FarmBoundary.distanceKm(
                        centroidLat, centroidLng,
                        existing.getCentroidLat(), existing.getCentroidLng());
                if (distKm < 0.05) {
                    return new SpatialOverlapResult(true, 50.0,
                            existing.getFarmId(),
                            "Farm is too close to an existing farm (< 50m)");
                }
            }
        }
        return new SpatialOverlapResult(false, 0, null, "No overlap detected");
    }

    // ── GetFarmMap ────────────────────────────────────────────────────────────
    @Override
    public FarmMapData getFarmMap(UUID farmId) {
        FarmBoundary boundary = boundaryRepository.findByFarmId(farmId)
                .orElseThrow(() -> new FarmNotFoundException(farmId.toString()));

        NdviReading latest = null;
        try {
            latest = getLatestNdvi(farmId);
        } catch (Exception e) {
            log.debug("GS: no latest NDVI for farm map farm={}", farmId);
        }

        double areaHa = boundary.getAreaSqKm() * 100.0;
        if (areaHa <= 0) {
            try {
                areaHa = calculateFarmArea(farmId).areaHectares();
            } catch (Exception ignored) {}
        }

        return new FarmMapData(
                farmId,
                boundary.getGeoJsonPolygon(),
                boundary.getCentroidLat(),
                boundary.getCentroidLng(),
                areaHa,
                latest
        );
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private void runYieldPredictionForFarm(UUID farmId) {
        FarmServicePort.FarmInfo farm = farmService.getFarmById(farmId);
        WeatherServicePort.WeatherContext weather = weatherService.getWeatherContext(farmId);

        List<NdviReading> readings = ndviRepository.findByFarmIdOrderByDateDesc(farmId, 90);
        if (readings.isEmpty()) return;

        double currentNdvi = readings.get(0).getNdviValue();
        double peakNdvi = readings.stream()
                .mapToDouble(NdviReading::getNdviValue).max().orElse(currentNdvi);

        double growthRate = 0.0;
        List<NdviReading> first30 = readings.stream()
                .filter(r -> r.getRecordedDate().isAfter(LocalDate.now().minusDays(30)))
                .toList();
        if (first30.size() >= 2) {
            NdviReading oldest = first30.get(first30.size() - 1);
            NdviReading newest = first30.get(0);
            long days = java.time.temporal.ChronoUnit.DAYS.between(
                    oldest.getRecordedDate(), newest.getRecordedDate());
            if (days > 0) {
                growthRate = (newest.getNdviValue() - oldest.getNdviValue()) / days;
            }
        }

        int daysSincePlanting = 60;

        // Derived ML inputs
        double ndviSmoothness = readings.size() >= 3
                ? 1.0 - (readings.stream().mapToDouble(r -> Math.abs(r.getNdviValue() - currentNdvi)).average().orElse(0.0))
                : 0.8;
        int altitudeM             = 1800;  // Ethiopian highland default (metres)
        int cropVarietyEncoded    = 1;     // 1 = local variety (default)
        int inputQualityEncoded   = 1;     // 1 = basic inputs (default)
        double historicalZoneYield = 18.0; // Ethiopian avg ~18 quintals/ha

        AiServicePort.YieldPrediction aiPrediction = aiService.predictYield(
                new AiServicePort.YieldPredictionInput(
                        farmId.toString(),
                        farm.cropType(),
                        peakNdvi,
                        growthRate,
                        currentNdvi,
                        ndviSmoothness,
                        weather.totalRainfallMm(),
                        weather.avgTempC(),
                        altitudeM,
                        cropVarietyEncoded,
                        farm.areaHectares(),
                        inputQualityEncoded,
                        daysSincePlanting,
                        historicalZoneYield
                )
        );

        if (aiPrediction != null) {
            double meanPerHa  = aiPrediction.predictedYieldQuintalsPerHa();
            double lowerPerHa = aiPrediction.lowerBound();
            double upperPerHa = aiPrediction.upperBound();
            double areaHa     = farm.areaHectares();

            YieldPrediction prediction = YieldPrediction.builder()
                    .cropType(farm.cropType())
                    .predictedYieldMin(lowerPerHa)
                    .predictedYieldMax(upperPerHa)
                    .predictedYieldMean(meanPerHa)
                    .totalYieldMinQuintals(lowerPerHa * areaHa)
                    .totalYieldMaxQuintals(upperPerHa * areaHa)
                    .totalYieldMeanQuintals(meanPerHa * areaHa)
                    .confidencePct(aiPrediction.confidencePct())
                    .weeksToHarvest((int) Math.max(1, (90 - daysSincePlanting) / 7.0))
                    .modelVersion(aiPrediction.modelVersion())
                    .ndviPeak(peakNdvi)
                    .ndviGrowthRate(growthRate)
                    .totalRainfallMm(weather.totalRainfallMm())
                    .avgTempC(weather.avgTempC())
                    .altitudeM(altitudeM)
                    .inputQuality(inputQualityEncoded == 1 ? "BASIC" : "IMPROVED")
                    .predictedAt(LocalDateTime.now())
                    .build();

            prediction.setId(UUID.randomUUID());
            prediction.setFarmId(farmId);
            prediction.setCreatedAt(LocalDateTime.now());
            YieldPrediction saved = yieldRepository.save(prediction);
            eventPublisher.publishYieldPredicted(saved);
        }
    }

    private double estimateOverlapPercentage(List<double[]> a, List<double[]> b) {
        int inside = 0;
        int total  = a.size() - 1;
        for (int i = 0; i < total; i++) {
            double lat = a.get(i)[1];
            double lng = a.get(i)[0];
            if (GeoJsonPolygonUtils.isPointInPolygon(lat, lng, b)) inside++;
        }
        return total > 0 ? (inside * 100.0) / total : 0;
    }

    // ═══════════════════════════════════════════════════════════════════════════
// ADD THIS METHOD to GeospatialServiceImpl.java
//
// Place it after the getFarmMap() method, before the private helpers section.
// Also add these imports to the top of GeospatialServiceImpl.java:
//

//
// The method uses WeatherServicePort which is already injected as weatherService.
// ═══════════════════════════════════════════════════════════════════════════

    // ── GS-06: Digital Twin ───────────────────────────────────────────────────
    @Override
    public DigitalTwinData getDigitalTwin(UUID farmId) {
        log.info("GS: getDigitalTwin farm={}", farmId);

        // ── Layer 1: Farm context (from farm-service via gRPC) ────────────────
        FarmServicePort.FarmInfo farm;
        try {
            farm = farmService.getFarmContext(farmId);
        } catch (Exception e) {
            log.warn("GS: digital twin — could not load farm context for farm={}: {}",
                    farmId, e.getMessage());
            throw e;
        }

        // ── Layer 2: Spatial layer (from MongoDB farm_boundaries) ─────────────
        DigitalTwinSpatialLayer spatialLayer;
        try {
            FarmBoundary boundary = boundaryRepository.findByFarmId(farmId)
                    .orElse(null);
            if (boundary != null) {
                spatialLayer = new DigitalTwinSpatialLayer(
                        boundary.getGeoJsonPolygon(),
                        boundary.getCentroidLat(),
                        boundary.getCentroidLng(),
                        boundary.getAreaSqKm() * 100.0,
                        boundary.isSatelliteVerified()
                );
            } else {
                spatialLayer = new DigitalTwinSpatialLayer(
                        null,
                        farm.gpsCentroidLat(),
                        farm.gpsCentroidLng(),
                        farm.areaHectares(),
                        false
                );
            }
        } catch (Exception e) {
            log.warn("GS: digital twin — no spatial data for farm={}", farmId);
            spatialLayer = new DigitalTwinSpatialLayer(
                    null, farm.gpsCentroidLat(), farm.gpsCentroidLng(),
                    farm.areaHectares(), false);
        }

        // ── Layer 3: NDVI layer (from MongoDB ndvi_readings) ──────────────────
        DigitalTwinNdviLayer ndviLayer;
        try {
            List<NdviReading> series = ndviRepository
                    .findByFarmIdOrderByDateDesc(farmId, 30);

            double currentNdvi  = 0.0;
            String healthStatus = "UNKNOWN";
            double peakNdvi     = 0.0;
            double ndviTrend    = 0.0;

            if (!series.isEmpty()) {
                // series is newest-first — reverse for trend calculation
                NdviReading newest = series.get(0);
                currentNdvi  = newest.getNdviValue();
                healthStatus = newest.getHealthStatus();
                peakNdvi     = series.stream()
                        .mapToDouble(NdviReading::getNdviValue)
                        .max().orElse(currentNdvi);

                // Trend = change per day over the 30-day window
                if (series.size() >= 2) {
                    NdviReading oldest = series.get(series.size() - 1);
                    long days = java.time.temporal.ChronoUnit.DAYS.between(
                            oldest.getRecordedDate(), newest.getRecordedDate());
                    if (days > 0) {
                        ndviTrend = (newest.getNdviValue() - oldest.getNdviValue()) / days;
                        ndviTrend = Math.round(ndviTrend * 10000.0) / 10000.0;
                    }
                }
            }

            // Build time series oldest-first for chart rendering on frontend
            List<NdviPoint> timeSeries = series.stream()
                    .sorted(Comparator.comparing(NdviReading::getRecordedDate))
                    .map(r -> new NdviPoint(
                            r.getRecordedDate().toString(),
                            r.getNdviValue(),
                            r.getHealthStatus()))
                    .collect(java.util.stream.Collectors.toList());

            ndviLayer = new DigitalTwinNdviLayer(
                    currentNdvi, healthStatus, peakNdvi, ndviTrend, timeSeries);

        } catch (Exception e) {
            log.warn("GS: digital twin — no NDVI data for farm={}: {}", farmId, e.getMessage());
            ndviLayer = new DigitalTwinNdviLayer(0.0, "UNKNOWN", 0.0, 0.0,
                    java.util.Collections.emptyList());
        }

        // Layer 4: Weather layer (from weather-service via gRPC)
        DigitalTwinWeatherLayer weatherLayer;
        try {
            WeatherServicePort.WeatherContext weather =
                    weatherService.getWeatherContext(farmId);

            int riskScore = (int) Math.round(weather.weatherRiskScore());

            String riskLevel = riskScore >= 60
                    ? "HIGH"
                    : riskScore >= 30
                      ? "MEDIUM"
                      : "LOW";

            weatherLayer = new DigitalTwinWeatherLayer(
                    weather.totalRainfallMm(),
                    weather.avgTempC(),
                    riskScore,
                    riskLevel,
                    riskScore >= 60
            );

        } catch (Exception e) {
            log.warn(
                    "GS: digital twin - weather data unavailable for farm={}: {}",
                    farmId,
                    e.getMessage()
            );

            weatherLayer = new DigitalTwinWeatherLayer(
                    0.0,
                    0.0,
                    0,
                    "UNKNOWN",
                    false
            );
        }

        // ── Layer 5: Yield layer (from MongoDB yield_predictions) ─────────────
        DigitalTwinYieldLayer yieldLayer;
        try {
            yieldLayer = yieldRepository.findLatestByFarmId(farmId)
                    .map(y -> new DigitalTwinYieldLayer(
                            y.getTotalYieldMinQuintals(),
                            y.getTotalYieldMaxQuintals(),
                            y.getTotalYieldMeanQuintals(),
                            y.getConfidencePct(),
                            y.getWeeksToHarvest(),
                            y.getModelVersion()))
                    .orElse(new DigitalTwinYieldLayer(
                            0, 0, 0, 0, 0, "NOT_AVAILABLE"));
        } catch (Exception e) {
            log.warn("GS: digital twin — no yield prediction for farm={}", farmId);
            yieldLayer = new DigitalTwinYieldLayer(0, 0, 0, 0, 0, "NOT_AVAILABLE");
        }

        // ── Layer 6: Harvest readiness layer ──────────────────────────────────
        DigitalTwinHarvestLayer harvestLayer;
        try {
            HarvestReadinessResult readiness = predictHarvestReadiness(farmId);
            harvestLayer = new DigitalTwinHarvestLayer(
                    readiness.ready(),
                    readiness.estimatedDateFrom(),
                    readiness.estimatedDateTo(),
                    readiness.readinessSignal()
            );
        } catch (Exception e) {
            log.warn("GS: digital twin — harvest readiness unavailable for farm={}", farmId);
            harvestLayer = new DigitalTwinHarvestLayer(
                    false, null, null, "INSUFFICIENT_DATA");
        }

        // ── Assemble and return ───────────────────────────────────────────────
        return new DigitalTwinData(
                farmId,
                farm.cropType(),
                farm.region(),
                farm.seasonName(),
                farm.cropCycleStatus(),
                farm.agriScore(),
                spatialLayer,
                ndviLayer,
                weatherLayer,
                yieldLayer,
                harvestLayer,
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        );
    }

    // ── GetSatelliteImage ─────────────────────────────────────────────────────
    @Override
    public byte[] getSatelliteImage(UUID farmId, int widthPx, int heightPx) {
        log.info("GS: getSatelliteImage farm={} {}x{}px", farmId, widthPx, heightPx);

        // Get farm coordinates from MongoDB boundary (fastest — no gRPC)
        double lat, lng;
        String geoJson = null;

        Optional<FarmBoundary> boundary = boundaryRepository.findByFarmId(farmId);
        if (boundary.isPresent()) {
            lat    = boundary.get().getCentroidLat();
            lng    = boundary.get().getCentroidLng();
            geoJson = boundary.get().getGeoJsonPolygon();
        } else {
            // Fall back to gRPC if boundary not in MongoDB yet
            FarmServicePort.FarmInfo farm = farmService.getFarmById(farmId);
            lat = farm.gpsCentroidLat();
            lng = farm.gpsCentroidLng();
        }

        return copernicusClient.fetchSatelliteImage(farmId, lat, lng, geoJson,
                widthPx, heightPx);
    }

}