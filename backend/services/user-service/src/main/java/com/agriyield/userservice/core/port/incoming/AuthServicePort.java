package com.agriyield.userservice.core.port.incoming;

import com.agriyield.userservice.core.domain.model.User;

import java.util.UUID;

public interface AuthServicePort {
    
    User register(String phone, String faydaId, String password, String role, String fullName);
    
    String verifyOtp(String phone, String otpCode, String purpose);
    
    String login(String phone, String password);
    
    String refreshToken(String refreshToken);
    
    void logout(String accessToken, String refreshToken);
    
    void changePassword(UUID userId, String oldPassword, String newPassword);
    
    void requestPasswordReset(String phone);
    
    void resetPassword(String phone, String otpCode, String newPassword);
}
