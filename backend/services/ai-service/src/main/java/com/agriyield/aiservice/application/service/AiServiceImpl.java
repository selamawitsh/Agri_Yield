package com.agriyield.aiservice.application.service;

import com.agriyield.aiservice.application.port.incoming.AiServicePort;
import com.agriyield.aiservice.application.port.outgoing.*;
import com.agriyield.aiservice.domain.model.AdvisorySession;
import com.agriyield.aiservice.domain.model.CropDiagnosis;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiServiceImpl implements AiServicePort {

    private final WhisperPort whisperPort;
    private final GeminiPort geminiPort;
    private final TextToSpeechPort textToSpeechPort;
    private final AdvisorySessionRepositoryPort advisorySessionRepository;
    private final CropDiagnosisRepositoryPort cropDiagnosisRepository;
    private final GeospatialServicePort geospatialServicePort;
    private final WeatherServicePort weatherServicePort;
    private final FarmServicePort farmServicePort;
    private final AudioStoragePort audioStoragePort;
    private final ObjectMapper objectMapper;

    @Override
    public AdvisorySession processVoiceAdvisory(String farmId, String farmerId,
                                                 MultipartFile audioFile, String language) {
        log.info("Voice advisory: farm={} farmer={} lang={}", farmId, farmerId, language);
        String audioInputUrl = audioStoragePort.storeAudioInput(audioFile, farmId);
        String transcribedText = whisperPort.transcribeAudio(audioFile, language);
        String contextJson = buildFarmContext(farmId);
        FarmServicePort.FarmData farmData = safeFetchFarm(farmId);
        String advisoryText = geminiPort.generateAdvisory(
                buildAdvisorySystemPrompt(language),
                buildAdvisoryUserPrompt(transcribedText, contextJson));
        String audioResponseUrl = null;
        try {
            byte[] audioBytes = textToSpeechPort.synthesizeSpeech(
                    advisoryText, toTtsLanguageCode(language));
            if (audioBytes != null && audioBytes.length > 0) {
                audioResponseUrl = audioStoragePort.storeAudioResponse(
                        audioBytes, farmId, UUID.randomUUID().toString());
            }
        } catch (Exception e) {
            log.warn("TTS failed, text-only response: {}", e.getMessage());
        }
        AdvisorySession session = AdvisorySession.builder()
                .farmId(farmId).farmerId(farmerId).queryType("VOICE").language(language)
                .originalQuery(transcribedText).audioInputUrl(audioInputUrl)
                .advisoryText(advisoryText).audioResponseUrl(audioResponseUrl)
                .cropType(farmData != null ? farmData.cropType() : null)
                .currentNdvi(safeGetNdvi(farmId)).createdAt(LocalDateTime.now())
                .build();
        return advisorySessionRepository.save(session);
    }

    @Override
    public AdvisorySession processTextAdvisory(String farmId, String farmerId,
                                                String query, String language) {
        log.info("Text advisory: farm={} lang={}", farmId, language);
        String contextJson = buildFarmContext(farmId);
        FarmServicePort.FarmData farmData = safeFetchFarm(farmId);
        String advisoryText = geminiPort.generateAdvisory(
                buildAdvisorySystemPrompt(language),
                buildAdvisoryUserPrompt(query, contextJson));
        AdvisorySession session = AdvisorySession.builder()
                .farmId(farmId).farmerId(farmerId).queryType("TEXT").language(language)
                .originalQuery(query).advisoryText(advisoryText)
                .cropType(farmData != null ? farmData.cropType() : null)
                .currentNdvi(safeGetNdvi(farmId)).createdAt(LocalDateTime.now())
                .build();
        return advisorySessionRepository.save(session);
    }

    @Override
    public CropDiagnosis diagnoseCropDisease(String farmId, String farmerId,
                                              String photoUrl, String photoId,
                                              String triggeredBy,
                                              Integer daysPostPlanting,
                                              Double currentNdvi) {
        log.info("Crop diagnosis: farm={} photo={} trigger={}", farmId, photoId, triggeredBy);
        FarmServicePort.FarmData farmData = safeFetchFarm(farmId);
        String cropType = farmData != null ? farmData.cropType() : "UNKNOWN";
        Double ndvi = currentNdvi;
        Integer days = daysPostPlanting;
        if (ndvi == null || days == null) {
            try {
                GeospatialServicePort.FarmContext ctx = geospatialServicePort.getFarmContext(farmId);
                if (ndvi == null)  ndvi = ctx.ndviValue();
                if (days == null)  days = ctx.daysSincePlanting();
            } catch (Exception e) {
                log.warn("Geospatial context unavailable: {}", e.getMessage());
            }
        }
        String diagnosisJson = geminiPort.diagnoseCropImage(photoUrl, cropType, ndvi, days);
        CropDiagnosis diagnosis = parseDiagnosisJson(
                diagnosisJson, farmId, farmerId, photoId, photoUrl, cropType, ndvi, days, triggeredBy);
        CropDiagnosis saved = cropDiagnosisRepository.save(diagnosis);
        if (Boolean.TRUE.equals(saved.getEscalateToAgronomist())) {
            log.warn("Agronomist escalation needed for farm={}", farmId);
        }
        return saved;
    }

    @Override
    public YieldPredictionResult predictYield(YieldPredictionInput in) {
        log.info("Yield prediction: farm={} crop={}", in.farmId(), in.cropType());
        double base = in.historicalZoneYield();
        double ndviFactor = in.ndviPeak() > 0.70 ? 1.25 : in.ndviPeak() > 0.60 ? 1.15 :
                            in.ndviPeak() > 0.50 ? 1.05 : in.ndviPeak() < 0.30 ? 0.70 :
                            in.ndviPeak() < 0.40 ? 0.85 : 1.00;
        double rainFactor = in.totalRainfallMm() > 400 ? 1.10 :
                            in.totalRainfallMm() < 150 ? 0.75 : 1.00;
        double inputFactor = switch (in.inputQualityEncoded()) {
            case 3 -> 1.30; case 2 -> 1.15; case 1 -> 1.00; default -> 0.80;
        };
        double mean  = Math.round(base * ndviFactor * rainFactor * inputFactor * 100.0) / 100.0;
        double lower = Math.round(mean * 0.82 * 100.0) / 100.0;
        double upper = Math.round(mean * 1.18 * 100.0) / 100.0;
        int confidence = Math.min(75 + (in.ndviSmoothness() < 0.05 ? 10 : 0)
                                     + (in.daysSincePlanting() > 60 ? 5 : 0), 95);
        return new YieldPredictionResult(mean, lower, upper, confidence, "rule-based-v1.0");
    }

    @Override
    public FraudRiskResult scoreFraudRisk(String entityId, String entityType,
                                           String eventType, String eventPayloadJson) {
        log.info("Fraud risk: entity={} event={}", entityId, eventType);
        double p = 0.05;
        if ("DUPLICATE_SCAN".equals(eventType))    p += 0.80;
        if ("MERCHANT_TOO_FAR".equals(eventType))  p += 0.40;
        if ("CATEGORY_MISMATCH".equals(eventType)) p += 0.30;
        if ("NIGHT_TRANSACTION".equals(eventType)) p += 0.20;
        p = Math.min(p, 0.99);
        String level = p >= 0.70 ? "HIGH" : p >= 0.40 ? "MEDIUM" : "LOW";
        return new FraudRiskResult(p, "rule-based-fraud-v1.0", level);
    }

    // --- helpers ---
    private FarmServicePort.FarmData safeFetchFarm(String farmId) {
        try { return farmServicePort.getFarmById(farmId); }
        catch (Exception e) { log.warn("Farm fetch failed {}: {}", farmId, e.getMessage()); return null; }
    }

    private Double safeGetNdvi(String farmId) {
        try { return geospatialServicePort.getLatestNdvi(farmId).ndviValue(); }
        catch (Exception e) { log.warn("NDVI fetch failed {}: {}", farmId, e.getMessage()); return null; }
    }

    private String buildFarmContext(String farmId) {
        StringBuilder ctx = new StringBuilder("{");
        try {
            FarmServicePort.FarmData f = farmServicePort.getFarmById(farmId);
            ctx.append("\"cropType\":\"").append(f.cropType()).append("\",");
            ctx.append("\"region\":\"").append(f.region()).append("\",");
            ctx.append("\"areaHectares\":").append(f.areaHectares()).append(",");
        } catch (Exception e) { log.warn("Farm ctx unavailable: {}", e.getMessage()); }
        try {
            GeospatialServicePort.FarmContext g = geospatialServicePort.getFarmContext(farmId);
            ctx.append("\"currentNdvi\":").append(g.ndviValue()).append(",");
            ctx.append("\"daysSincePlanting\":").append(g.daysSincePlanting()).append(",");
        } catch (Exception e) { log.warn("Geo ctx unavailable: {}", e.getMessage()); }
        try {
            WeatherServicePort.WeatherData w = weatherServicePort.getCurrentWeather(farmId);
            ctx.append("\"temperature\":").append(w.temperatureC()).append(",");
            ctx.append("\"rainfall\":").append(w.rainfallMm()).append(",");
            ctx.append("\"weatherSummary\":\"").append(w.forecastSummary()).append("\"");
        } catch (Exception e) { ctx.append("\"weatherSummary\":\"unavailable\""); }
        ctx.append("}");
        return ctx.toString();
    }

    private String buildAdvisorySystemPrompt(String language) {
        String lang = switch (language) {
            case "am" -> "Respond ONLY in Amharic (አማርኛ).";
            case "om" -> "Respond ONLY in Oromiffa (Afaan Oromoo).";
            case "ti" -> "Respond ONLY in Tigrinya (ትግርኛ).";
            default   -> "Respond in English.";
        };
        return "You are an expert Ethiopian agricultural advisor. Provide practical, actionable " +
               "advice to smallholder farmers. " + lang + " Keep responses concise.";
    }

    private String buildAdvisoryUserPrompt(String query, String contextJson) {
        return "Farmer question: " + query + "\n\nFarm data: " + contextJson +
               "\n\nProvide specific, actionable advice for this Ethiopian farmer.";
    }

    private String toTtsLanguageCode(String language) {
        return switch (language) {
            case "am" -> "am-ET"; case "om" -> "om-ET"; case "ti" -> "ti-ET"; default -> "en-US";
        };
    }

    private CropDiagnosis parseDiagnosisJson(String json, String farmId, String farmerId,
            String photoId, String photoUrl, String cropType,
            Double ndvi, Integer days, String triggeredBy) {
        CropDiagnosis.CropDiagnosisBuilder b = CropDiagnosis.builder()
                .farmId(farmId).farmerId(farmerId).photoId(photoId).photoUrl(photoUrl)
                .cropType(cropType).currentNdvi(ndvi).daysPostPlanting(days)
                .triggeredBy(triggeredBy).diagnosedAt(LocalDateTime.now());
        try {
            JsonNode node = objectMapper.readTree(json);
            b.diseaseName(node.path("disease_name").asText("Unknown"));
            b.confidencePct(node.path("confidence_pct").asInt(50));
            b.symptomsObserved(node.path("symptoms_observed").asText(""));
            b.recommendedTreatment(node.path("recommended_treatment").asText(""));
            b.severity(node.path("severity").asText("MEDIUM"));
            b.escalateToAgronomist(node.path("escalate_to_agronomist").asBoolean(false));
        } catch (Exception e) {
            log.error("Failed to parse diagnosis JSON: {}", e.getMessage());
            b.diseaseName("Analysis Error").confidencePct(0)
             .symptomsObserved("Image analysis failed")
             .recommendedTreatment("Please consult an agronomist")
             .severity("MEDIUM").escalateToAgronomist(true);
        }
        return b.build();
    }
}
