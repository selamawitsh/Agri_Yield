package com.agriyield.aiservice.infrastructure.adapter.outgoing.gemini;

import com.agriyield.aiservice.application.port.outgoing.GeminiPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class GeminiClient implements GeminiPort {

    private final WebClient webClient;
    private final String apiKey;
    private final String baseUrl;
    private final ObjectMapper objectMapper;

    public GeminiClient(
            @Value("${app.gemini.api-key}") String apiKey,
            @Value("${app.gemini.base-url}") String baseUrl,
            ObjectMapper objectMapper) {
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
        this.objectMapper = objectMapper;
        this.webClient = WebClient.builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Override
    public String generateAdvisory(String systemPrompt, String userPrompt) {
        log.info("Calling Gemini API for advisory");
        try {
            Map<String, Object> requestBody = Map.of(
                    "system_instruction", Map.of(
                            "parts", List.of(Map.of("text", systemPrompt))
                    ),
                    "contents", List.of(
                            Map.of("parts", List.of(Map.of("text", userPrompt)))
                    ),
                    "generationConfig", Map.of(
                            "temperature", 0.7,
                            "maxOutputTokens", 1024
                    )
            );

            String jsonBody = objectMapper.writeValueAsString(requestBody);

            String response = webClient.post()
                    .uri(baseUrl + "?key=" + apiKey)
                    .bodyValue(jsonBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return extractTextFromGeminiResponse(response);

        } catch (Exception e) {
            log.error("Gemini advisory API call failed: {}", e.getMessage(), e);
            return "Advisory service temporarily unavailable. Please consult your local extension officer.";
        }
    }

    @Override
    public String diagnoseCropImage(String photoUrlOrBase64, String cropType,
                                    Double currentNdvi, Integer daysPostPlanting) {
        log.info("Calling Gemini Vision API for crop diagnosis cropType={}", cropType);
        try {
            String prompt = buildDiagnosisPrompt(cropType, currentNdvi, daysPostPlanting);

            // Build vision request - using URL reference for Gemini
            Map<String, Object> imagePart = Map.of(
                    "file_data", Map.of(
                            "file_uri", photoUrlOrBase64,
                            "mime_type", "image/jpeg"
                    )
            );

            Map<String, Object> textPart = Map.of("text", prompt);

            Map<String, Object> requestBody = Map.of(
                    "contents", List.of(
                            Map.of("parts", List.of(imagePart, textPart))
                    ),
                    "generationConfig", Map.of(
                            "temperature", 0.1,
                            "maxOutputTokens", 512,
                            "response_mime_type", "application/json"
                    )
            );

            String jsonBody = objectMapper.writeValueAsString(requestBody);

            String response = webClient.post()
                    .uri(baseUrl + "?key=" + apiKey)
                    .bodyValue(jsonBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return extractTextFromGeminiResponse(response);

        } catch (Exception e) {
            log.error("Gemini vision API call failed: {}", e.getMessage(), e);
            // Return safe fallback JSON
            return """
                    {
                      "disease_name": "Analysis unavailable",
                      "confidence_pct": 0,
                      "symptoms_observed": "Image analysis service temporarily unavailable",
                      "recommended_treatment": "Please consult a local agronomist",
                      "severity": "MEDIUM",
                      "escalate_to_agronomist": true
                    }
                    """;
        }
    }

    private String buildDiagnosisPrompt(String cropType, Double ndvi, Integer days) {
        return String.format(
                "You are an Ethiopian crop disease specialist. Analyze this photo of a %s plant. " +
                        "The farm's current NDVI is %.2f. The crop is %d days post-planting. " +
                        "Identify any disease, deficiency, or pest damage. " +
                        "Respond ONLY with a valid JSON object containing these exact fields: " +
                        "disease_name (string), confidence_pct (integer 0-100), " +
                        "symptoms_observed (string), recommended_treatment (string), " +
                        "severity (string: LOW or MEDIUM or HIGH), " +
                        "escalate_to_agronomist (boolean). " +
                        "Return ONLY the JSON object, no markdown, no explanation.",
                cropType != null ? cropType : "crop",
                ndvi != null ? ndvi : 0.0,
                days != null ? days : 0
        );
    }

    private String extractTextFromGeminiResponse(String rawResponse) {
        try {
            var node = objectMapper.readTree(rawResponse);
            return node.path("candidates")
                    .path(0)
                    .path("content")
                    .path("parts")
                    .path(0)
                    .path("text")
                    .asText("");
        } catch (Exception e) {
            log.error("Failed to parse Gemini response: {}", e.getMessage());
            return rawResponse;
        }
    }
}