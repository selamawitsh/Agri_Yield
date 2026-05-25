package com.agriyield.investmentservice.infrastructure.config;

import com.agriyield.investmentservice.domain.exception.BusinessException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Slf4j
@Component
public class JwtUtils {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public UUID extractUserId(String bearerToken) {
        if (bearerToken == null || bearerToken.isBlank()) {
            throw new BusinessException("Authorization token is required", "UNAUTHORIZED");
        }
        try {
            String token = bearerToken.startsWith("Bearer ")
                ? bearerToken.substring(7) : bearerToken;
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey()).build()
                .parseClaimsJws(token).getBody();
            return UUID.fromString(claims.getSubject());
        } catch (Exception e) {
            log.error("Failed to extract user ID from JWT: {}", e.getMessage());
            throw new BusinessException("Invalid or expired token", "UNAUTHORIZED");
        }
    }

    public String extractRole(String bearerToken) {
        if (bearerToken == null || bearerToken.isBlank()) {
            throw new BusinessException("Authorization token is required", "UNAUTHORIZED");
        }
        try {
            String token = bearerToken.startsWith("Bearer ")
                ? bearerToken.substring(7) : bearerToken;
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey()).build()
                .parseClaimsJws(token).getBody();
            return claims.get("role", String.class);
        } catch (Exception e) {
            log.error("Failed to extract role from JWT: {}", e.getMessage());
            throw new BusinessException("Invalid or expired token", "UNAUTHORIZED");
        }
    }
}
