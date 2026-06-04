package com.agriyield.geospatialservice.infrastructure.adapter.outgoing.copernicus;

import com.agriyield.geospatialservice.application.port.outgoing.CopernicusClientPort;
import com.agriyield.geospatialservice.domain.model.NdviReading;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Real Copernicus Dataspace implementation.
 *
 * Flow:
 *   1. Authenticate → get OAuth2 access token from identity.dataspace.copernicus.eu
 *   2. STAC search  → find latest cloud-free Sentinel-2 L2A scene over the farm bbox
 *   3. Process API  → submit evalscript to Sentinel Hub Process API to compute
 *                     mean NDVI over the farm polygon (Band 8 NIR, Band 4 Red)
 *   4. Parse result → extract mean NDVI value and return NdviReading domain object
 *
 * Credentials come from environment variables:
 *   COPERNICUS_CLIENT_ID     → your Copernicus OAuth2 client id
 *   COPERNICUS_CLIENT_SECRET → your Copernicus OAuth2 client secret
 *
 * Set app.copernicus.mock-enabled=false in application.yml to activate.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CopernicusClientAdapter implements CopernicusClientPort {

    private final WebClient.Builder webClientBuilder;

    // ── Config values from application.yml ────────────────────────────────────

    @Value("${app.copernicus.base-url:https://catalogue.dataspace.copernicus.eu}")
    private String catalogBaseUrl;

    // Sentinel Hub Process API endpoint (different from the STAC catalog)
    @Value("${app.copernicus.process-api-url:https://sh.dataspace.copernicus.eu}")
    private String processApiBaseUrl;

    // OAuth2 token endpoint
    @Value("${app.copernicus.auth-url:https://identity.dataspace.copernicus.eu/auth/realms/CDSE/protocol/openid-connect/token}")
    private String authUrl;

    @Value("${app.copernicus.client-id}")
    private String clientId;

    @Value("${app.copernicus.client-secret}")
    private String clientSecret;

    @Value("${app.copernicus.mock-enabled:false}")
    private boolean mockEnabled;

    @Value("${app.ndvi.cloud-cover-threshold:30}")
    private int cloudCoverThreshold;

    // ── Main entry point ───────────────────────────────────────────────────────

    @Override
    public NdviReading fetchNdvi(UUID farmId,
                                 double lat,
                                 double lng,
                                 String geoJsonPolygon) {
        if (mockEnabled) {
            log.warn("GS: Copernicus mock-enabled=true — returning null. " +
                    "Set app.copernicus.mock-enabled=false to use real satellite data.");
            return null;
        }

        try {
            return fetchFromCopernicus(farmId, lat, lng, geoJsonPolygon);
        } catch (WebClientResponseException e) {
            log.error("GS: Copernicus HTTP {} for farm={}: {}",
                    e.getStatusCode(), farmId, e.getResponseBodyAsString());
            return null;
        } catch (Exception e) {
            log.error("GS: Copernicus fetch failed for farm={}: {}", farmId, e.getMessage());
            return null;
        }
    }

    // ── Step 1: OAuth2 token ───────────────────────────────────────────────────

    /**
     * Fetches a short-lived OAuth2 Bearer token from Copernicus identity service.
     * Uses client_credentials grant — no user login needed.
     */
    @SuppressWarnings("unchecked")
    private String getAccessToken() {
        WebClient authClient = webClientBuilder.baseUrl(authUrl).build();

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type",    "client_credentials");
        formData.add("client_id",     clientId);
        formData.add("client_secret", clientSecret);

        Map<String, Object> tokenResponse = authClient.post()
                .uri("")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (tokenResponse == null || !tokenResponse.containsKey("access_token")) {
            throw new IllegalStateException("Copernicus auth failed — no access_token in response");
        }

        String token = (String) tokenResponse.get("access_token");
        log.debug("GS: Copernicus OAuth2 token obtained successfully");
        return token;
    }

    // ── Step 2: STAC scene search ──────────────────────────────────────────────

    /**
     * Searches the Copernicus STAC catalog for the most recent cloud-free
     * Sentinel-2 L2A scene covering the farm location.
     *
     * Returns the scene ID and actual cloud cover percentage, or null if
     * no suitable scene is found in the last 10 days.
     */
    @SuppressWarnings("unchecked")
    private SceneInfo findLatestScene(String token, double lat, double lng) {
        WebClient catalogClient = webClientBuilder.baseUrl(catalogBaseUrl).build();

        // Search 10-day window — Sentinel-2 revisit is every 5 days
        String dateFrom = LocalDate.now().minusDays(10).toString();
        String dateTo   = LocalDate.now().toString();

        // Build a small bbox around the farm centroid (±0.05 degrees ≈ 5km)
        double bboxMinLng = lng - 0.05;
        double bboxMinLat = lat - 0.05;
        double bboxMaxLng = lng + 0.05;
        double bboxMaxLat = lat + 0.05;

        String bbox = bboxMinLng + "," + bboxMinLat + "," + bboxMaxLng + "," + bboxMaxLat;

        log.info("GS: STAC search bbox={} dates={}/{}", bbox, dateFrom, dateTo);

        Map<String, Object> stacResponse = catalogClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/stac/search")
                        .queryParam("collections", "SENTINEL-2")
                        .queryParam("bbox", bbox)
                        .queryParam("datetime", dateFrom + "T00:00:00Z/" + dateTo + "T23:59:59Z")
                        .queryParam("filter", "eo:cloud_cover<" + cloudCoverThreshold)
                        .queryParam("sortby", "-datetime")   // newest first
                        .queryParam("limit", "1")
                        .build())
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (stacResponse == null) {
            log.warn("GS: STAC search returned null response");
            return null;
        }

        List<Map<String, Object>> features =
                (List<Map<String, Object>>) stacResponse.get("features");

        if (features == null || features.isEmpty()) {
            log.warn("GS: No Sentinel-2 L2A scene found for bbox={} in last 10 days " +
                    "(cloud cover threshold={}%)", bbox, cloudCoverThreshold);
            return null;
        }

        Map<String, Object> scene      = features.get(0);
        String              sceneId    = (String) scene.get("id");
        Map<String, Object> properties = (Map<String, Object>) scene.get("properties");

        double cloudCover = 0.0;
        if (properties != null && properties.containsKey("eo:cloud_cover")) {
            cloudCover = ((Number) properties.get("eo:cloud_cover")).doubleValue();
        }

        log.info("GS: Found scene={} cloudCover={}%", sceneId, cloudCover);
        return new SceneInfo(sceneId, cloudCover);
    }

    // ── Step 3: Process API — compute mean NDVI over the polygon ──────────────

    /**
     * Calls the Sentinel Hub Process API with an evalscript that computes
     * mean NDVI = (B08 - B04) / (B08 + B04) over the farm polygon.
     *
     * The evalscript aggregates all valid pixels (not masked by cloud/shadow)
     * and returns the mean value as a single-band GeoTIFF.
     * We parse the mean from the statistics response instead of downloading
     * a full image, which is faster and cheaper.
     */
    @SuppressWarnings("unchecked")
    private double computeMeanNdvi(String token, double lat, double lng,
                                   String geoJsonPolygon) {
        WebClient processClient = webClientBuilder.baseUrl(processApiBaseUrl).build();

        // Use the Statistical API endpoint — returns aggregated stats, not a full image.
        // This is the correct approach for computing a single mean NDVI value over a polygon.
        // Docs: https://documentation.dataspace.copernicus.eu/APIs/SentinelHub/Statistical.html

        String dateFrom = LocalDate.now().minusDays(10).toString();
        String dateTo   = LocalDate.now().toString();

        // Build the geometry — use farm polygon if available, else 100m bbox around centroid
        String geometry = buildGeometry(lat, lng, geoJsonPolygon);

        // Evalscript: compute NDVI per pixel, return mean over valid (non-masked) pixels
        String evalscript = """
                //VERSION=3
                function setup() {
                  return {
                    input: [{ bands: ["B04", "B08", "SCL"], units: "DN" }],
                    output: [{ id: "ndvi", bands: 1, sampleType: "FLOAT32" }],
                    mosaicking: "ORBIT"
                  };
                }
                function evaluatePixel(samples) {
                  let sum = 0, count = 0;
                  for (let s of samples) {
                    // SCL classes 4 (vegetation) and 5 (not vegetation) are cloud-free
                    if (s.SCL === 4 || s.SCL === 5 || s.SCL === 6 || s.SCL === 11) {
                      let b4 = s.B04, b8 = s.B08;
                      if (b4 + b8 > 0) {
                        sum += (b8 - b4) / (b8 + b4);
                        count++;
                      }
                    }
                  }
                  return [count > 0 ? sum / count : -9999];
                }
                """;

        Map<String, Object> requestBody = Map.of(
                "input", Map.of(
                        "bounds", Map.of(
                                "geometry", geometry.isEmpty()
                                        ? buildBboxGeometry(lat, lng) : Map.of(
                                        "type", "Polygon",
                                        "coordinates", parseGeoJsonCoordinates(geoJsonPolygon)
                                ),
                                "properties", Map.of("crs", "http://www.opengis.net/def/crs/EPSG/0/4326")
                        ),
                        "data", List.of(Map.of(
                                "type", "sentinel-2-l2a",
                                "dataFilter", Map.of(
                                        "timeRange", Map.of(
                                                "from", dateFrom + "T00:00:00Z",
                                                "to",   dateTo   + "T23:59:59Z"
                                        ),
                                        "maxCloudCoverage", cloudCoverThreshold
                                )
                        ))
                ),
                "aggregation", Map.of(
                        "timeRange", Map.of(
                                "from", dateFrom + "T00:00:00Z",
                                "to",   dateTo   + "T23:59:59Z"
                        ),
                        "aggregationInterval", Map.of("of", "P10D"),
                        "evalscript", evalscript,
                        "resx", 10,
                        "resy", 10
                ),
                "calculations", Map.of(
                        "ndvi", Map.of("statistics", Map.of("default", Map.of(
                                "percentiles", Map.of("k", List.of(25, 50, 75))
                        )))
                )
        );

        Map<String, Object> statsResponse = processClient.post()
                .uri("/api/v1/statistics")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        return parseNdviFromStatsResponse(statsResponse);
    }

    // ── Step 4: Parse the statistics response ─────────────────────────────────

    @SuppressWarnings("unchecked")
    private double parseNdviFromStatsResponse(Map<String, Object> response) {
        if (response == null) {
            throw new IllegalStateException("Statistics API returned null response");
        }

        try {
            // Navigate: data[0].outputs.ndvi.bands.B0.stats.mean
            List<Map<String, Object>> data =
                    (List<Map<String, Object>>) response.get("data");

            if (data == null || data.isEmpty()) {
                throw new IllegalStateException("Statistics API returned empty data array");
            }

            Map<String, Object> outputs   = (Map<String, Object>) data.get(0).get("outputs");
            Map<String, Object> ndviOut   = (Map<String, Object>) outputs.get("ndvi");
            Map<String, Object> bands     = (Map<String, Object>) ndviOut.get("bands");
            Map<String, Object> b0        = (Map<String, Object>) bands.get("B0");
            Map<String, Object> stats     = (Map<String, Object>) b0.get("stats");

            double mean     = ((Number) stats.get("mean")).doubleValue();
            double noDataPct = stats.containsKey("noDataPixelsFraction")
                    ? ((Number) stats.get("noDataPixelsFraction")).doubleValue() * 100 : 0;

            if (mean == -9999 || noDataPct > 80) {
                throw new IllegalStateException(
                        "Too many masked pixels (noData=" + noDataPct + "%) — scene unusable");
            }

            // Clamp to valid NDVI range [-1, 1]
            double ndvi = Math.max(-1.0, Math.min(1.0, mean));
            ndvi = Math.round(ndvi * 10000.0) / 10000.0;

            log.info("GS: Parsed NDVI mean={} (noData={}%)", ndvi, noDataPct);
            return ndvi;

        } catch (ClassCastException | NullPointerException e) {
            throw new IllegalStateException(
                    "Could not parse NDVI from Statistics API response: " + e.getMessage(), e);
        }
    }

    // ── Orchestrator ───────────────────────────────────────────────────────────

    private NdviReading fetchFromCopernicus(UUID farmId,
                                            double lat,
                                            double lng,
                                            String geoJsonPolygon) {
        // Step 1: auth
        String token = getAccessToken();

        // Step 2: find latest cloud-free scene
        SceneInfo scene = findLatestScene(token, lat, lng);
        if (scene == null) {
            log.warn("GS: No suitable Sentinel-2 scene for farm={} (try widening date window " +
                    "or increasing cloud-cover threshold)", farmId);
            return null;
        }

        // Step 3: compute mean NDVI via Statistics API
        double ndvi = computeMeanNdvi(token, lat, lng, geoJsonPolygon);

        log.info("GS: Real NDVI computed for farm={} sceneId={} ndvi={}",
                farmId, scene.sceneId(), ndvi);

        return NdviReading.builder()
                .farmId(farmId)
                .ndviValue(ndvi)
                .cloudCoverage(scene.cloudCover())
                .sentinelSceneId(scene.sceneId())
                .recordedDate(LocalDate.now())
                .createdAt(LocalDateTime.now())
                .build();
    }

    // ── Geometry helpers ───────────────────────────────────────────────────────

    /**
     * Builds a small rectangular polygon around the centroid when no farm
     * boundary GeoJSON is available. Side ≈ 500m at Ethiopian latitudes.
     */
    private Map<String, Object> buildBboxGeometry(double lat, double lng) {
        double delta = 0.0025; // ~275m each side at equator
        return Map.of(
                "type", "Polygon",
                "coordinates", List.of(List.of(
                        List.of(lng - delta, lat - delta),
                        List.of(lng + delta, lat - delta),
                        List.of(lng + delta, lat + delta),
                        List.of(lng - delta, lat + delta),
                        List.of(lng - delta, lat - delta)
                ))
        );
    }

    private String buildGeometry(double lat, double lng, String geoJsonPolygon) {
        // Return empty string to signal "use polygon from GeoJSON directly"
        // Caller checks geoJsonPolygon != null before calling this
        return geoJsonPolygon != null ? geoJsonPolygon : "";
    }

    /**
     * Extracts the coordinates array from a GeoJSON Polygon string.
     * Expects: {"type":"Polygon","coordinates":[[[lng,lat],...]]}.
     * Uses simple string parsing to avoid adding a JSON library dependency.
     */
    @SuppressWarnings("unchecked")
    private Object parseGeoJsonCoordinates(String geoJsonPolygon) {
        // Use Jackson ObjectMapper if already on classpath (it is, via Spring Boot)
        try {
            com.fasterxml.jackson.databind.ObjectMapper om =
                    new com.fasterxml.jackson.databind.ObjectMapper();
            Map<String, Object> parsed = om.readValue(geoJsonPolygon, Map.class);
            return parsed.get("coordinates");
        } catch (Exception e) {
            log.warn("GS: Could not parse geoJsonPolygon, using centroid bbox. Error: {}",
                    e.getMessage());
            return null;
        }
    }

    // ── Private records ────────────────────────────────────────────────────────

    private record SceneInfo(String sceneId, double cloudCover) {}
}