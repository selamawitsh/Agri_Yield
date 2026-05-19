package com.agriyield.userservice.core.port.outgoing;

import com.agriyield.userservice.core.domain.enums.UserRole;

import java.util.UUID;

public interface JwtTokenPort {
    String generateAccessToken(UUID userId, UserRole role, String faydaId);
    String generateRefreshToken(UUID userId);
    TokenValidationResult validateToken(String token);
    void blacklistToken(String token, long expiryInMillis);
    void storeRefreshToken(String refreshToken, UUID userId, long expiryInMillis);
    boolean isValidRefreshToken(String refreshToken);
    void removeRefreshToken(String refreshToken);

    class TokenValidationResult {
        private final boolean valid;
        private final UUID userId;
        private final UserRole role;
        private final String faydaId;
        private final String error;

        public TokenValidationResult(boolean valid, UUID userId, UserRole role, String faydaId, String error) {
            this.valid = valid;
            this.userId = userId;
            this.role = role;
            this.faydaId = faydaId;
            this.error = error;
        }

        public static TokenValidationResult success(UUID userId, UserRole role, String faydaId) {
            return new TokenValidationResult(true, userId, role, faydaId, null);
        }

        public static TokenValidationResult failure(String error) {
            return new TokenValidationResult(false, null, null, null, error);
        }

        // Getters
        public boolean isValid() { return valid; }
        public UUID getUserId() { return userId; }
        public UserRole getRole() { return role; }
        public String getFaydaId() { return faydaId; }
        public String getError() { return error; }
    }
}