package com.agriyield.userservice.application.port.incoming;

import com.agriyield.userservice.domain.model.User;

import java.util.Map;
import java.util.UUID;

public interface AuthServicePort {

    // US-01 — Register new user
    User register(String phone, String faydaId, String password,
                  String role, String fullName);

    // US-02 — Verify OTP
    String verifyOtp(String phone, String otpCode, String purpose);

    // US-03 — Login
    Map<String, String> login(String phone, String password);

    // US-04 — Refresh JWT token
    String refreshToken(String refreshToken);

    // US-05 — Logout
    void logout(String accessToken, String refreshToken);

    // Password management
    void changePassword(UUID userId, String oldPassword, String newPassword);
    void requestPasswordReset(String phone);
    void resetPassword(String phone, String otpCode, String newPassword);
}
