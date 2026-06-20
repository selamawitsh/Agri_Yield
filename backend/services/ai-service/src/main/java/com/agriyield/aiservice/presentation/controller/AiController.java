package com.agriyield.aiservice.presentation.controller;

import com.agriyield.aiservice.application.port.incoming.AiServicePort;
import com.agriyield.aiservice.domain.model.AdvisorySession;
import com.agriyield.aiservice.domain.model.CropDiagnosis;
import com.agriyield.aiservice.infrastructure.config.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiServicePort aiService;
    private final JwtUtils jwtUtils;

    // ── UC-AI-01: Voice advisory ──────────────────────────────────────────────
    // POST /api/v1/ai/advisory/voice
    // multipart: audio_file, farm_id, language
    @PostMapping(value = "/advisory/voice", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> voiceAdvisory(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam("audio_file") MultipartFile audioFile,
            @RequestParam("farm_id") String farmId,
            @RequestParam(value = "language", defaultValue = "am") String language) {

        UUID farmerId = jwtUtils.extractUserId(authHeader);
        log.info("POST /api/v1/ai/advisory/voice — farmer={} farm={} lang={}", farmerId, farmId, language);

        AdvisorySession session = aiService.processVoiceAdvisory(
                farmId, farmerId.toString(), audioFile, language);

        return ResponseEntity.ok(Map.of(
                "success",         true,
                "session_id",      session.getId(),
                "transcribed_text", session.getOriginalQuery() != null ? session.getOriginalQuery() : "",
                "response_text",   session.getAdvisoryText(),
                "audio_url",       session.getAudioResponseUrl() != null ? session.getAudioResponseUrl() : "",
                "language",        session.getLanguage()
        ));
    }

    // ── UC-AI-02: Text advisory ───────────────────────────────────────────────
    // POST /api/v1/ai/advisory/text
    // body: { farm_id, query, language }
    @PostMapping("/advisory/text")
    public ResponseEntity<Map<String, Object>> textAdvisory(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, String> body) {

        UUID farmerId = jwtUtils.extractUserId(authHeader);
        String farmId   = body.get("farm_id");
        String query    = body.get("query");
        String language = body.getOrDefault("language", "am");

        log.info("POST /api/v1/ai/advisory/text — farmer={} farm={}", farmerId, farmId);

        AdvisorySession session = aiService.processTextAdvisory(
                farmId, farmerId.toString(), query, language);

        return ResponseEntity.ok(Map.of(
                "success",       true,
                "session_id",    session.getId(),
                "query",         session.getOriginalQuery(),
                "response_text", session.getAdvisoryText(),
                "language",      session.getLanguage()
        ));
    }

    // ── UC-AI-03: Crop disease detection ─────────────────────────────────────
    // POST /api/v1/ai/diagnose/image
    // multipart: image, farm_id, crop_type, days_post_planting, current_ndvi
    @PostMapping(value = "/diagnose/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> diagnoseImage(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam("image") MultipartFile image,
            @RequestParam("farm_id") String farmId,
            @RequestParam(value = "crop_type", defaultValue = "WHEAT") String cropType,
            @RequestParam(value = "days_post_planting", defaultValue = "0") Integer daysPostPlanting,
            @RequestParam(value = "current_ndvi", defaultValue = "0.5") Double currentNdvi) {

        UUID farmerId = jwtUtils.extractUserId(authHeader);
        log.info("POST /api/v1/ai/diagnose/image — farmer={} farm={}", farmerId, farmId);

        String imageBase64;
        try {
            imageBase64 = Base64.getEncoder().encodeToString(image.getBytes());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Invalid image"));
        }

        // Generate a unique photo ID
        String photoId = UUID.randomUUID().toString();

        // Use farmer ID as who triggered the diagnosis
        String triggeredBy = farmerId.toString();

        // Now call with ALL 7 parameters in correct order
        CropDiagnosis diagnosis = aiService.diagnoseCropDisease(
                farmId,              // 1. farmId
                farmerId.toString(), // 2. farmerId
                imageBase64,         // 3. photoUrl (base64 encoded image)
                photoId,             // 4. photoId (new UUID)
                triggeredBy,         // 5. triggeredBy (who initiated this)
                daysPostPlanting,    // 6. daysPostPlanting
                currentNdvi          // 7. currentNdvi
        );

        return ResponseEntity.ok(Map.of(
                "success",                 true,
                "disease_name",            diagnosis.getDiseaseName(),
                "confidence_pct",          diagnosis.getConfidencePct(),
                "symptoms_observed",       diagnosis.getSymptomsObserved(),
                "recommended_treatment",   diagnosis.getRecommendedTreatment(),
                "severity",                diagnosis.getSeverity(),
                "escalate_to_agronomist",  diagnosis.getEscalateToAgronomist()
        ));
    }

    // ── Health check ──────────────────────────────────────────────────────────
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP", "service", "ai-service"));
    }
}