package com.agriyield.geospatialservice.infrastructure.adapter.outgoing.copernicus;

import com.agriyield.geospatialservice.application.port.outgoing.CopernicusClientPort;
import com.agriyield.geospatialservice.domain.model.NdviReading;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.UUID;

@Slf4j
@Component
public class CopernicusClientAdapter implements CopernicusClientPort {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${app.copernicus.auth-url}")
    private String authUrl;

    @Value("${app.copernicus.process-api-url}")
    private String processApiUrl;

    @Value("${app.copernicus.client-id}")
    private String clientId;

    @Value("${app.copernicus.client-secret}")
    private String clientSecret;

    @Value("${app.ndvi.cloud-cover-threshold:30}")
    private int cloudCoverThreshold;

    public CopernicusClientAdapter(WebClient.Builder webClientBuilder,
                                    ObjectMapper objectMapper) {
        this.webClient    = webClientBuilder.build();
        this.objectMapper = objectMapper;
    }

    @Override
    public NdviReading fetchNdvi(UUID farmId,
                                  double lat,
                                  double lng,
                                  String geoJsonPolygon) {
        try {
            return fetchFromCopernicus(farmId, lat, lng, geoJsonPolygon);
        } catch (WebClientResponseException e) {
            // Log the full response body so we can see exactly what Copernicus rejected
            log.error("GS: Copernicus HTTP {} for farm={}: body={}",
                    e.getStatusCode(), farmId, e.getResponseBodyAsString(), e);
            return null;
        } catch (Exception e) {
            log.error("GS: Copernicus API failed for farm={}: {}", farmId, e.getMessage(), e);
            return null;
        }
    }

    // ── STEP 1: OAuth2 token ──────────────────────────────────────────────────
    private String getAccessToken() {
        log.debug("GS: fetching Copernicus OAuth2 token");

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type",    "client_credentials");
        formData.add("client_id",     clientId);
        formData.add("client_secret", clientSecret);

        String response = webClient.post()
                .uri(authUrl)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(String.class)
                .block();

        try {
            JsonNode json = objectMapper.readTree(response);
            String token  = json.get("access_token").asText();
            log.debug("GS: Copernicus token obtained successfully");
            return token;
        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to parse Copernicus OAuth2 token: " + e.getMessage(), e);
        }
    }

    // ── STEPS 2+3: Statistical API → mean Band 4 + Band 8 → NDVI ────────────
    private NdviReading fetchFromCopernicus(UUID farmId,
                                             double lat,
                                             double lng,
                                             String geoJsonPolygon)
            throws JsonProcessingException {

        log.info("GS: fetching real Sentinel-2 NDVI for farm={} lat={} lng={}",
                farmId, lat, lng);

        String token = getAccessToken();

        // Bounding box: centroid ± 0.005 degrees (~500m buffer)
        double lngMin = lng - 0.005;
        double latMin = lat - 0.005;
        double lngMax = lng + 0.005;
        double latMax = lat + 0.005;

        // Date range: last 15 days to maximise chance of a cloud-free scene
        String dateFrom = LocalDate.now().minusDays(15).toString();
        String dateTo   = LocalDate.now().toString();

        // SRS §3.6.1: evalscript returns Band 4 (Red) and Band 8 (NIR) per pixel
        // The Statistical API aggregates these into mean values over the bbox
        String evalscript = "//VERSION=3\n"
                + "function setup() {\n"
                + "  return { input: [\"B04\",\"B08\",\"dataMask\"],\n"
                + "           output: [\n"
                + "             { id:\"B04\", bands:1, sampleType:\"FLOAT32\" },\n"
                + "             { id:\"B08\", bands:1, sampleType:\"FLOAT32\" },\n"
                + "             { id:\"dataMask\", bands:1, sampleType:\"UINT8\" }\n"
                + "           ] };\n"
                + "}\n"
                + "function evaluatePixel(s) {\n"
                + "  return { B04:[s.B04], B08:[s.B08], dataMask:[s.dataMask] };\n"
                + "}";

        // Sentinel Hub Statistical API — correct request structure
        // Docs: https://docs.sentinel-hub.com/api/latest/api/statistical/
        String requestBody = buildStatisticsRequest(
                lngMin, latMin, lngMax, latMax,
                dateFrom, dateTo,
                evalscript);

        log.debug("GS: Statistical API request for farm={}: {}", farmId, requestBody);

        String response = webClient.post()
                .uri(processApiUrl + "/api/v1/statistics")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        log.debug("GS: Statistical API response for farm={}: {}", farmId, response);

        return parseStatisticsResponse(farmId, response);
    }

    // ── Build the correct Sentinel Hub Statistical API request body ───────────
    // Reference: https://docs.sentinel-hub.com/api/latest/api/statistical/
    private String buildStatisticsRequest(double lngMin, double latMin,
                                           double lngMax, double latMax,
                                           String dateFrom, String dateTo,
                                           String evalscript)
            throws JsonProcessingException {

        // Use Jackson to build the JSON properly — no string formatting issues
        com.fasterxml.jackson.databind.node.ObjectNode root =
                objectMapper.createObjectNode();

        // input
        com.fasterxml.jackson.databind.node.ObjectNode input =
                root.putObject("input");

        // input.bounds
        com.fasterxml.jackson.databind.node.ObjectNode bounds =
                input.putObject("bounds");
        com.fasterxml.jackson.databind.node.ArrayNode bbox =
                bounds.putArray("bbox");
        bbox.add(lngMin).add(latMin).add(lngMax).add(latMax);

        // input.data
        com.fasterxml.jackson.databind.node.ArrayNode dataArray =
                input.putArray("data");
        com.fasterxml.jackson.databind.node.ObjectNode dataItem =
                dataArray.addObject();
        dataItem.put("type", "sentinel-2-l2a");

        com.fasterxml.jackson.databind.node.ObjectNode dataFilter =
                dataItem.putObject("dataFilter");
        com.fasterxml.jackson.databind.node.ObjectNode timeRange =
                dataFilter.putObject("timeRange");
        timeRange.put("from", dateFrom + "T00:00:00Z");
        timeRange.put("to",   dateTo   + "T23:59:59Z");
        dataFilter.put("maxCloudCoverage", cloudCoverThreshold);
        dataFilter.put("mosaickingOrder", "leastCC"); // least cloud cover first

        // aggregation
        com.fasterxml.jackson.databind.node.ObjectNode aggregation =
                root.putObject("aggregation");

        com.fasterxml.jackson.databind.node.ObjectNode aggTimeRange =
                aggregation.putObject("timeRange");
        aggTimeRange.put("from", dateFrom + "T00:00:00Z");
        aggTimeRange.put("to",   dateTo   + "T23:59:59Z");

        com.fasterxml.jackson.databind.node.ObjectNode aggInterval =
                aggregation.putObject("aggregationInterval");
        aggInterval.put("of", "P15D"); // one result over the 15-day window

        aggregation.put("evalscript", evalscript);
        aggregation.put("resx", 10); // 10m resolution matching Sentinel-2
        aggregation.put("resy", 10);

        // calculations — request mean statistics for each output band
        com.fasterxml.jackson.databind.node.ObjectNode calculations =
                root.putObject("calculations");
        com.fasterxml.jackson.databind.node.ObjectNode defaultCalc =
                calculations.putObject("default");
        com.fasterxml.jackson.databind.node.ObjectNode statistics =
                defaultCalc.putObject("statistics");
        com.fasterxml.jackson.databind.node.ObjectNode defaultStat =
                statistics.putObject("default");
        // percentiles must be an OBJECT not an array
        // Correct format: { "percentiles": { "k": [25, 50, 75] } }
        com.fasterxml.jackson.databind.node.ObjectNode percentiles =
                defaultStat.putObject("percentiles");
        com.fasterxml.jackson.databind.node.ArrayNode kArray =
                percentiles.putArray("k");
        kArray.add(25).add(50).add(75);

        return objectMapper.writeValueAsString(root);
    }

    // ── STEP 4: Parse response and compute NDVI ───────────────────────────────
    // SRS §3.6.1: NDVI = (B08 - B04) / (B08 + B04)
    private NdviReading parseStatisticsResponse(UUID farmId, String response) {
        try {
            JsonNode root = objectMapper.readTree(response);

            // Handle Copernicus error response
            if (root.has("error") || root.has("errors")) {
                log.warn("GS: Copernicus returned error for farm={}: {}", farmId, response);
                return null;
            }

            JsonNode data = root.path("data");
            if (data.isMissingNode() || !data.isArray() || data.isEmpty()) {
                log.warn("GS: No satellite data returned for farm={}" +
                        " — no cloud-free scene in date range", farmId);
                return null;
            }

            // Use the most recent interval with valid data
            JsonNode interval  = null;
            for (int i = data.size() - 1; i >= 0; i--) {
                JsonNode candidate = data.get(i);
                String status = candidate.path("status").asText("");
                if (!"NO_DATA".equals(status)) {
                    interval = candidate;
                    break;
                }
            }

            if (interval == null) {
                log.warn("GS: All intervals returned NO_DATA for farm={}", farmId);
                return null;
            }

            JsonNode outputs = interval.path("outputs");

            // Extract mean B04 (Red) and B08 (NIR)
            double meanRed = outputs.path("B04")
                    .path("bands").path("B0")
                    .path("stats").path("mean").asDouble(-1);

            double meanNir = outputs.path("B08")
                    .path("bands").path("B0")
                    .path("stats").path("mean").asDouble(-1);

            if (meanRed < 0 || meanNir < 0) {
                log.warn("GS: Band statistics missing for farm={} outputs={}",
                        farmId, outputs);
                return null;
            }

            // SRS §3.6.1: NDVI = (B08 - B04) / (B08 + B04)
            double ndvi = NdviReading.calculate(meanNir, meanRed);
            ndvi = Math.max(-1.0, Math.min(1.0, ndvi));
            ndvi = Math.round(ndvi * 10000.0) / 10000.0;

            // Cloud coverage from dataMask (0=no data, 1=valid pixel)
            // mean of 1.0 = 0% cloud, mean of 0.7 = 30% cloud
            double maskMean = outputs.path("dataMask")
                    .path("bands").path("B0")
                    .path("stats").path("mean").asDouble(1.0);
            double cloudCoverage = Math.round((1.0 - maskMean) * 1000.0) / 10.0;

            String intervalFrom = interval.path("interval")
                    .path("from").asText("UNKNOWN");
            String sceneId = "S2-" + intervalFrom.substring(0, 10)
                    + "-" + farmId.toString().substring(0, 8).toUpperCase();

            log.info("GS: NDVI calculated — farm={} B04(red)={} B08(nir)={} NDVI={} cloud={}%",
                    farmId, meanRed, meanNir, ndvi, cloudCoverage);

            return NdviReading.builder()
                    .farmId(farmId)
                    .ndviValue(ndvi)
                    .cloudCoverage(cloudCoverage)
                    .sentinelSceneId(sceneId)
                    .recordedDate(LocalDate.now())
                    .createdAt(LocalDateTime.now())
                    .build();

        } catch (Exception e) {
            log.error("GS: Failed to parse Copernicus response for farm={}: {}",
                    farmId, e.getMessage(), e);
            return null;
        }
    }

    // ── SATELLITE IMAGE: True-colour PNG from Sentinel-2 ─────────────────────
    // Band 4 (Red) + Band 3 (Green) + Band 2 (Blue) = natural colour photograph
    // Returns raw PNG bytes ready to serve as image/png HTTP response
    @Override
    public byte[] fetchSatelliteImage(UUID farmId, double lat, double lng,
                                       String geoJsonPolygon,
                                       int widthPx, int heightPx) {
        try {
            log.info("GS: fetching satellite image for farm={} {}x{}px", farmId, widthPx, heightPx);

            String token = getAccessToken();

            // Bounding box — slightly wider than NDVI so the farm is centred in frame
            double buffer = 0.015;
            double lngMin = lng - buffer;
            double latMin = lat - buffer;
            double lngMax = lng + buffer;
            double latMax = lat + buffer;

            String dateFrom = LocalDate.now().minusDays(30).toString();
            String dateTo   = LocalDate.now().toString();

            // TrueColor evalscript — gamma correction makes it look like a real photo
            String evalscript = "//VERSION=3\n"
                    + "function setup() {\n"
                    + "  return { input: [{bands:['B02','B03','B04']}],\n"
                    + "           output: { bands: 3, sampleType: 'AUTO' } };\n"
                    + "}\n"
                    + "function evaluatePixel(s) {\n"
                    + "  return [2.5*s.B04, 2.5*s.B03, 2.5*s.B02];\n"
                    + "}";

            // Build Process API request for image output
            com.fasterxml.jackson.databind.node.ObjectNode root =
                    objectMapper.createObjectNode();

            // input
            com.fasterxml.jackson.databind.node.ObjectNode input = root.putObject("input");
            com.fasterxml.jackson.databind.node.ObjectNode bounds = input.putObject("bounds");
            bounds.putArray("bbox").add(lngMin).add(latMin).add(lngMax).add(latMax);

            com.fasterxml.jackson.databind.node.ArrayNode dataArr = input.putArray("data");
            com.fasterxml.jackson.databind.node.ObjectNode dataItem = dataArr.addObject();
            dataItem.put("type", "sentinel-2-l2a");

            com.fasterxml.jackson.databind.node.ObjectNode dataFilter =
                    dataItem.putObject("dataFilter");
            com.fasterxml.jackson.databind.node.ObjectNode timeRange =
                    dataFilter.putObject("timeRange");
            timeRange.put("from", dateFrom + "T00:00:00Z");
            timeRange.put("to",   dateTo   + "T23:59:59Z");
            dataFilter.put("maxCloudCoverage", 80);
            dataFilter.put("mosaickingOrder", "leastCC");

            // output — PNG image
            com.fasterxml.jackson.databind.node.ObjectNode output = root.putObject("output");
            output.put("width",  widthPx);
            output.put("height", heightPx);
            // Sentinel Hub Process API requires 'responses' as an ARRAY
            com.fasterxml.jackson.databind.node.ArrayNode responses =
                    output.putArray("responses");
            com.fasterxml.jackson.databind.node.ObjectNode defaultResp =
                    responses.addObject();
            defaultResp.put("identifier", "default");
            com.fasterxml.jackson.databind.node.ObjectNode formatNode =
                    defaultResp.putObject("format");
            formatNode.put("type", "image/png");

            root.put("evalscript", evalscript);

            String requestBody = objectMapper.writeValueAsString(root);
            log.debug("GS: satellite image request for farm={}: {}", farmId, requestBody);

            byte[] imageBytes = webClient.post()
                    .uri(processApiUrl + "/api/v1/process")
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(byte[].class)
                    .block();

            if (imageBytes == null || imageBytes.length == 0) {
                log.warn("GS: satellite image returned empty for farm={}", farmId);
                return null;
            }

            log.info("GS: satellite image fetched for farm={} size={}KB",
                    farmId, imageBytes.length / 1024);
            return imageBytes;

        } catch (org.springframework.web.reactive.function.client.WebClientResponseException e) {
            log.error("GS: satellite image HTTP {} for farm={}: {}",
                    e.getStatusCode(), farmId, e.getResponseBodyAsString());
            return null;
        } catch (Exception e) {
            log.error("GS: satellite image failed for farm={}: {}", farmId, e.getMessage(), e);
            return null;
        }
    }

}