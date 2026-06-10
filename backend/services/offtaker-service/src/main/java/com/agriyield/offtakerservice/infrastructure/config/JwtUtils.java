package com.agriyield.offtakerservice.infrastructure.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.UUID;

@Slf4j
@Component
public class JwtUtils {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    public UUID extractUserId(HttpServletRequest request) {
        String header = request.getHeader("X-User-Id");
        if (header != null && !header.isBlank()) {
            return UUID.fromString(header);
        }
        throw new IllegalStateException("X-User-Id header missing — request did not pass through gateway");
    }

    public String extractUserRole(HttpServletRequest request) {
        String role = request.getHeader("X-User-Role");
        if (role != null && !role.isBlank()) return role;
        throw new IllegalStateException("X-User-Role header missing");
    }

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
