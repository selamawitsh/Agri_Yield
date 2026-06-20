package com.agriyield.aiservice.infrastructure.adapter.outgoing.whisper;

import com.agriyield.aiservice.application.port.outgoing.WhisperPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
public class WhisperClient implements WhisperPort {

    private final WebClient webClient;
    private final String apiKey;
    private final String whisperUrl;

    public WhisperClient(
            @Value("${app.openai.api-key}") String apiKey,
            @Value("${app.openai.whisper-url}") String whisperUrl) {
        this.apiKey = apiKey;
        this.whisperUrl = whisperUrl;
        this.webClient = WebClient.builder()
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .build();
    }

    @Override
    public String transcribeAudio(MultipartFile audioFile, String language) {
        log.info("Calling Whisper API for language={}", language);
        try {
            byte[] fileBytes = audioFile.getBytes();
            String filename = audioFile.getOriginalFilename() != null
                    ? audioFile.getOriginalFilename() : "audio.m4a";

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("model", "whisper-1");
            body.add("language", language);
            body.add("response_format", "text");
            body.add("file", new ByteArrayResource(fileBytes) {
                @Override
                public String getFilename() { return filename; }
            });

            String result = webClient.post()
                    .uri(whisperUrl)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData(body))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            log.info("Whisper transcription successful, length={}", result != null ? result.length() : 0);
            return result != null ? result.trim() : "";

        } catch (Exception e) {
            log.error("Whisper API call failed: {}", e.getMessage(), e);
            // Return empty string so the advisory can still proceed with fallback
            return "[Audio transcription unavailable]";
        }
    }
}