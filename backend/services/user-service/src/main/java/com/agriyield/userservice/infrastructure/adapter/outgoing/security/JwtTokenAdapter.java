package com.agriyield.userservice.infrastructure.adapter.outgoing.security;

import com.agriyield.userservice.core.domain.enums.UserRole;
import com.agriyield.userservice.core.port.outgoing.CachePort;
import com.agriyield.userservice.core.port.outgoing.JwtTokenPort;
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
    
    @Value("${spring.security.jwt.secret}")
    private String jwtSecret;
    
    @Value("${spring.security.jwt.expiration:86400000}")
    private long jwtExpiration;
    
    @Value("${spring.security.jwt.refresh-expiration:604800000}")
    private long refreshExpiration;
    
    private final CachePort cachePort;
    
    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    
    @Override
    public String generateAccessToken(UUID userId, UserRole role, String faydaId) {
        Instant now = Instant.now();
        Date issuedAt = Date.from(now);
        Date expiresAt = Date.from(now.plusMillis(jwtExpiration));
        
        String token = Jwts.builder()
            .setSubject(userId.toString())
            .claim("role", role.getValue())
            .claim("fayda_id", faydaId)
            .claim("jti", UUID.randomUUID().toString())
            .setIssuedAt(issuedAt)
            .setExpiration(expiresAt)
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact();
        
        log.debug("Generated access token for user: {}", userId);
        return token;
    }
    
    @Override
    public String generateRefreshToken(UUID userId) {
        String refreshToken = UUID.randomUUID().toString();
        String key = "refresh:" + refreshToken;
        cachePort.set(key, userId.toString(), refreshExpiration, TimeUnit.MILLISECONDS);
        log.debug("Generated refresh token for user: {}", userId);
        return refreshToken;
    }
    
    @Override
    public void storeRefreshToken(String refreshToken, UUID userId, long expiryInMillis) {
        String key = "refresh:" + refreshToken;
        cachePort.set(key, userId.toString(), expiryInMillis, TimeUnit.MILLISECONDS);
    }
    
    @Override
    public boolean isValidRefreshToken(String refreshToken) {
        String key = "refresh:" + refreshToken;
        return cachePort.exists(key);
    }
    
    @Override
    public void removeRefreshToken(String refreshToken) {
        String key = "refresh:" + refreshToken;
        cachePort.delete(key);
    }
    
    @Override
    public TokenValidationResult validateToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
            
            String jti = claims.get("jti", String.class);
            if (jti != null) {
                String blacklistKey = "jwt:blacklist:" + jti;
                if (cachePort.exists(blacklistKey)) {
                    return TokenValidationResult.failure("Token has been revoked");
                }
            }
            
            UUID userId = UUID.fromString(claims.getSubject());
            UserRole role = UserRole.fromValue(claims.get("role", String.class));
            String faydaId = claims.get("fayda_id", String.class);
            
            return TokenValidationResult.success(userId, role, faydaId);
            
        } catch (ExpiredJwtException e) {
            log.warn("Token expired: {}", e.getMessage());
            return TokenValidationResult.failure("Token expired");
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Invalid token: {}", e.getMessage());
            return TokenValidationResult.failure("Invalid token");
        }
    }
    
    @Override
    public void blacklistToken(String token, long expiryInMillis) {
        try {
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
            
            String jti = claims.get("jti", String.class);
            if (jti != null) {
                Date expiration = claims.getExpiration();
                long remainingTtl = expiration.getTime() - System.currentTimeMillis();
                long ttl = Math.min(remainingTtl, expiryInMillis);
                
                if (ttl > 0) {
                    cachePort.set("jwt:blacklist:" + jti, "true", ttl, TimeUnit.MILLISECONDS);
                    log.debug("Token blacklisted: {}", jti);
                }
            }
        } catch (Exception e) {
            log.error("Failed to blacklist token: {}", e.getMessage());
        }
    }
}
