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
        String auth = request.getHeader("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) {
            try {
                Claims claims = parseClaims(auth.substring(7));
                return UUID.fromString(claims.getSubject());
            } catch (Exception e) {
                log.warn("Cannot identify user from JWT: {}", e.getMessage());
            }
        }
        throw new IllegalStateException("Cannot identify user");
    }

    public String extractUserRole(HttpServletRequest request) {
        String role = request.getHeader("X-User-Role");
        if (role != null && !role.isBlank()) return role;
        String auth = request.getHeader("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) {
            try {
                Claims claims = parseClaims(auth.substring(7));
                return (String) claims.get("role");
            } catch (Exception e) {
                log.warn("Cannot identify role from JWT: {}", e.getMessage());
            }
        }
        throw new IllegalStateException("Cannot identify role");
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
