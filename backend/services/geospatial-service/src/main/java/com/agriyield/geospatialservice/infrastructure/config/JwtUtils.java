package com.agriyield.geospatialservice.infrastructure.config;

import com.agriyield.geospatialservice.domain.exception.BusinessException;
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

    /**
     * Generate signing key from JWT secret
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(
                jwtSecret.getBytes(StandardCharsets.UTF_8)
        );
    }

    /**
     * Extract user ID from JWT token
     */
    public UUID extractUserId(String bearerToken) {
        try {
            String token = bearerToken.startsWith("Bearer ")
                    ? bearerToken.substring(7)
                    : bearerToken;

            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return UUID.fromString(claims.getSubject());

        } catch (Exception e) {
            log.error("Failed to extract user ID from token", e);
            throw new BusinessException(
                    "Invalid or expired token",
                    "UNAUTHORIZED"
            );
        }
    }

    /**
     * Extract user role from JWT token
     */
    public String extractRole(String bearerToken) {
        try {
            String token = bearerToken.startsWith("Bearer ")
                    ? bearerToken.substring(7)
                    : bearerToken;

            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return claims.get("role", String.class);

        } catch (Exception e) {
            log.error("Failed to extract role from token", e);
            throw new BusinessException(
                    "Invalid or expired token",
                    "UNAUTHORIZED"
            );
        }
    }
}