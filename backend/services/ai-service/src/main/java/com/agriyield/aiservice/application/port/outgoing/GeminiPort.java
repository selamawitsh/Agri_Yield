package com.agriyield.aiservice.application.port.outgoing;

public interface GeminiPort {

    String generateAdvisory(String systemPrompt, String userPrompt);

    String diagnoseCropImage(
            String base64Image,
            String cropType,
            Double currentNdvi,
            Integer daysPostPlanting
    );
}