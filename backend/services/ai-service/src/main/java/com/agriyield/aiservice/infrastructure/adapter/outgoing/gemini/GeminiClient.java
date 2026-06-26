package com.agriyield.aiservice.infrastructure.adapter.outgoing.gemini;

import com.agriyield.aiservice.application.port.outgoing.GeminiPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class GeminiClient implements GeminiPort {

    private final WebClient webClient;
    private final String apiKey;
    private final ObjectMapper objectMapper;

    private static final List<String> MODELS = List.of(
            "gemini-2.5-flash",
            "gemini-2.0-flash",
            "gemini-2.0-flash-lite",
            "gemini-flash-latest"
    );

    private static final String BASE =
            "https://generativelanguage.googleapis.com/v1beta/models/";

    public GeminiClient(
            @Value("${app.gemini.api-key}") String apiKey,
            ObjectMapper objectMapper) {
        this.apiKey = apiKey;
        this.objectMapper = objectMapper;
        this.webClient = WebClient.builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .codecs(c -> c.defaultCodecs().maxInMemorySize(4 * 1024 * 1024))
                .build();
    }

    @Override
    public String generateAdvisory(String systemPrompt, String userPrompt) {
        log.info("Gemini advisory request");
        Map<String, Object> body = Map.of(
                "contents", List.of(Map.of(
                        "role", "user",
                        "parts", List.of(Map.of("text", systemPrompt + "\n\n" + userPrompt))
                )),
                "generationConfig", Map.of("temperature", 0.7, "maxOutputTokens", 1024)
        );
        return callWithFallback(body,
                "Advisory service temporarily unavailable. " +
                "Please consult your local agricultural extension officer.");
    }

    @Override
    public String diagnoseCropImage(String photoUrlOrPath, String cropType,
                                     Double currentNdvi, Integer daysPostPlanting) {
        log.info("Gemini crop diagnosis: cropType={}", cropType);
        String prompt = String.format(
                "You are an Ethiopian crop disease specialist. " +
                "The crop type is %s. Current NDVI is %.2f. " +
                "The crop is %d days post-planting. " +
                "Based on these field conditions typical in Ethiopia, " +
                "provide a realistic disease or stress diagnosis. " +
                "Respond ONLY with valid JSON containing exactly these fields: " +
                "disease_name (string), confidence_pct (integer 0-100), " +
                "symptoms_observed (string), recommended_treatment (string), " +
                "severity (LOW or MEDIUM or HIGH), " +
                "escalate_to_agronomist (boolean). " +
                "Return ONLY the JSON object, no markdown fences.",
                cropType != null ? cropType : "unknown crop",
                currentNdvi != null ? currentNdvi : 0.0,
                daysPostPlanting != null ? daysPostPlanting : 0
        );
        Map<String, Object> body = Map.of(
                "contents", List.of(Map.of(
                        "parts", List.of(Map.of("text", prompt))
                )),
                "generationConfig", Map.of("temperature", 0.1, "maxOutputTokens", 512)
        );
        return callWithFallback(body, """
                {"disease_name":"Unable to analyze","confidence_pct":0,
                 "symptoms_observed":"Analysis unavailable",
                 "recommended_treatment":"Please consult a local agronomist",
                 "severity":"MEDIUM","escalate_to_agronomist":true}
                """);
    }

    private String callWithFallback(Map<String, Object> body, String fallback) {
        String json;
        try { json = objectMapper.writeValueAsString(body); }
        catch (Exception e) { return fallback; }

        for (String model : MODELS) {
            try {
                String response = webClient.post()
                        .uri(BASE + model + ":generateContent?key=" + apiKey)
                        .bodyValue(json)
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();
                String text = extractText(response);
                if (text != null && !text.isBlank()) {
                    log.info("Gemini success: model={}", model);
                    return text;
                }
            } catch (WebClientResponseException e) {
                log.warn("Model {} -> HTTP {}", model, e.getStatusCode().value());
                if (e.getStatusCode().value() == 429) break;
            } catch (Exception e) {
                log.warn("Model {} -> {}", model, e.getMessage());
            }
        }
        return fallback;
    }

    private String extractText(String raw) {
        try {
            return objectMapper.readTree(raw)
                    .path("candidates").path(0)
                    .path("content").path("parts").path(0)
                    .path("text").asText("");
        } catch (Exception e) { return null; }
    }
}
