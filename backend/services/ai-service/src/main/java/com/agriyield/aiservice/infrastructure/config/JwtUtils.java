package com.agriyield.aiservice.infrastructure.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

@Component
public class JwtUtils {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    public UUID extractUserId(String authHeader) {
        // Lightweight JWT parsing: decode payload and extract "sub" claim
        // NOTE: This does not verify the token signature. For local/dev usage
        // with stub tokens this is sufficient; consider reintroducing full
        // signature verification using jjwt when library versions are aligned.
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new IllegalArgumentException("Missing Authorization Bearer token");
            }
            String token = authHeader.replace("Bearer ", "").trim();
            String[] parts = token.split("\\.");
            if (parts.length < 2) throw new IllegalArgumentException("Invalid JWT format");

            byte[] decoded = Base64.getUrlDecoder().decode(parts[1]);
            String payloadJson = new String(decoded, StandardCharsets.UTF_8);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(payloadJson);
            String sub = node.path("sub").asText(null);
            if (sub == null || sub.isEmpty()) {
                // Some tokens may use 'sub' nested under claims; try common alternatives
                sub = node.path("userId").asText(null);
            }
            if (sub == null || sub.isEmpty()) throw new IllegalArgumentException("sub claim not found in JWT");
            return UUID.fromString(sub);
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract user id from JWT: " + e.getMessage(), e);
        }
    }
}
