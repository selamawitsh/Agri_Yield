package com.agriyield.aiservice.infrastructure.adapter.outgoing.tts;

import com.agriyield.aiservice.application.port.outgoing.TextToSpeechPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Base64;
import java.util.Map;

@Slf4j
@Component
public class GoogleTtsClient implements TextToSpeechPort {

    private final WebClient webClient;
    private final String apiKey;
    private final String baseUrl;
    private final ObjectMapper objectMapper;

    public GoogleTtsClient(
            @Value("${app.google-tts.api-key}") String apiKey,
            @Value("${app.google-tts.base-url}") String baseUrl,
            ObjectMapper objectMapper) {
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
        this.objectMapper = objectMapper;
        this.webClient = WebClient.builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Override
    public byte[] synthesizeSpeech(String text, String languageCode) {
        log.info("Google TTS: languageCode={} textLength={}", languageCode, text.length());
        try {
            String truncated = text.length() > 4500 ? text.substring(0, 4500) + "..." : text;
            Map<String, Object> requestBody = Map.of(
                    "input", Map.of("text", truncated),
                    "voice", Map.of("languageCode", languageCode, "ssmlGender", "NEUTRAL"),
                    "audioConfig", Map.of("audioEncoding", "MP3", "speakingRate", 0.9)
            );
            String jsonBody = objectMapper.writeValueAsString(requestBody);
            String response = webClient.post()
                    .uri(baseUrl + "?key=" + apiKey)
                    .bodyValue(jsonBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            var node = objectMapper.readTree(response);
            String base64Audio = node.path("audioContent").asText();
            return Base64.getDecoder().decode(base64Audio);
        } catch (Exception e) {
            log.error("Google TTS failed: {}", e.getMessage(), e);
            return new byte[0];
        }
    }
}
