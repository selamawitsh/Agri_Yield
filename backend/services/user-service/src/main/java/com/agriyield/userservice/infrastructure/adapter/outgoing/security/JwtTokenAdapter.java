package com.agriyield.userservice.infrastructure.adapter.outgoing.security;

import com.agriyield.userservice.application.port.outgoing.CachePort;
import com.agriyield.userservice.application.port.outgoing.JwtTokenPort;
import com.agriyield.userservice.domain.enums.UserRole;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenAdapter implements JwtTokenPort {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration:86400000}")
    private long jwtExpiration;

    @Value("${app.jwt.refresh-expiration:604800000}")
    private long refreshExpiration;

    private final CachePort cachePort;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(
            jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String generateAccessToken(UUID userId, UserRole role,
                                      String faydaId) {
        Instant now = Instant.now();
        return Jwts.builder()
            .setSubject(userId.toString())
            .claim("role", role.getValue())
            .claim("fayda_id", faydaId)
            .claim("jti", UUID.randomUUID().toString())
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(now.plusMillis(jwtExpiration)))
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact();
    }

    @Override
    public String generateRefreshToken(UUID userId) {
        String refreshToken = UUID.randomUUID().toString();
        cachePort.set("refresh:" + refreshToken,
            userId.toString(), refreshExpiration, TimeUnit.MILLISECONDS);
        return refreshToken;
    }

    @Override
    public void storeRefreshToken(String refreshToken, UUID userId,
                                   long expiryInMillis) {
        cachePort.set("refresh:" + refreshToken,
            userId.toString(), expiryInMillis, TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean isValidRefreshToken(String refreshToken) {
        return cachePort.exists("refresh:" + refreshToken);
    }

    @Override
    public void removeRefreshToken(String refreshToken) {
        cachePort.delete("refresh:" + refreshToken);
    }

    @Override
    public TokenValidationResult validateToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey()).build()
                .parseClaimsJws(token).getBody();

            String jti = claims.get("jti", String.class);
            if (jti != null && cachePort.exists("jwt:blacklist:" + jti)) {
                return TokenValidationResult.failure("Token revoked");
            }

            return TokenValidationResult.success(
                UUID.fromString(claims.getSubject()),
                UserRole.fromValue(claims.get("role", String.class)),
                claims.get("fayda_id", String.class));

        } catch (ExpiredJwtException e) {
            return TokenValidationResult.failure("Token expired");
        } catch (JwtException | IllegalArgumentException e) {
            return TokenValidationResult.failure("Invalid token");
        }
    }

    @Override
    public void blacklistToken(String token, long expiryInMillis) {
        try {
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey()).build()
                .parseClaimsJws(token).getBody();
            String jti = claims.get("jti", String.class);
            if (jti != null) {
                long remaining = claims.getExpiration().getTime()
                    - System.currentTimeMillis();
                if (remaining > 0) {
                    cachePort.set("jwt:blacklist:" + jti,
                        "true", remaining, TimeUnit.MILLISECONDS);
                }
            }
        } catch (Exception e) {
            log.error("Failed to blacklist token: {}", e.getMessage());
        }
    }
}
