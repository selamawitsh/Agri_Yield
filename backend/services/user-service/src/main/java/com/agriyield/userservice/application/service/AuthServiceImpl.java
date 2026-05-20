package com.agriyield.userservice.application.service;

import com.agriyield.userservice.core.domain.enums.*;
import com.agriyield.userservice.core.domain.exceptions.BusinessException;
import com.agriyield.userservice.core.domain.model.Otp;
import com.agriyield.userservice.core.domain.model.User;
import com.agriyield.userservice.core.port.incoming.AuthServicePort;
import com.agriyield.userservice.core.port.outgoing.*;
import com.agriyield.userservice.infrastructure.adapter.incoming.messaging.UserEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthServicePort {
    
    private final UserRepositoryPort userRepository;
    private final OtpRepositoryPort otpRepository;
    private final FaydaVerificationPort faydaVerificationPort;
    private final NotificationPort notificationPort;
    private final JwtTokenPort jwtTokenPort;
    private final CachePort cachePort;
    private final PasswordEncoder passwordEncoder;
    private final UserEventPublisher eventPublisher;
    private final InvestorProfileRepositoryPort investorProfileRepository;
    private final MerchantProfileRepositoryPort merchantProfileRepository;
    
    @Value("${app.otp.expiry-minutes:10}")
    private int otpExpiryMinutes;
    
    private final Random random = new Random();
    
    @Override
    @Transactional
    public User register(String phone, String faydaId, String password, String role, String fullName) {
        log.info("Registering new user with phone: {}, faydaId: {}, role: {}", phone, faydaId, role);
        
        // Step 1: Validate phone format (Ethiopian: +251XXXXXXXXX)
        if (!phone.matches("^\\+251[0-9]{9}$")) {
            throw new BusinessException("Invalid Ethiopian phone number format. Use +251XXXXXXXXX", "INVALID_PHONE");
        }
        
        // Step 2: Check if user already exists
        if (userRepository.existsByPhone(phone)) {
            throw new BusinessException("User already exists with this phone number", "DUPLICATE_PHONE");
        }
        
        if (userRepository.existsByFaydaId(faydaId)) {
            throw new BusinessException("Fayda ID already registered", "DUPLICATE_FAYDA_ID");
        }
        
        // Step 3: Verify Fayda ID via gRPC (SRS Page 15)
        boolean faydaVerified = faydaVerificationPort.verifyFaydaId(faydaId, phone, fullName);
        if (!faydaVerified) {
            throw new BusinessException("Fayda ID verification failed. Please check your National ID.", "FAYDA_VERIFICATION_FAILED");
        }
        
        // Step 4: Pull KYC data from Fayda (SRS requirement)
        FaydaVerificationPort.KycData kycData = faydaVerificationPort.pullKycData(faydaId);
        
        // Step 5: Create user with PENDING status (SRS Page 12)
        User user = User.builder()
            .id(UUID.randomUUID())
            .phone(phone)
            .faydaId(faydaId)
            .passwordHash(passwordEncoder.encode(password))
            .role(UserRole.fromValue(role))
            .kycStatus(KycStatus.PENDING)
            .accountStatus(AccountStatus.PENDING_VERIFICATION) // Changed from ACTIVE to PENDING_VERIFICATION
            .preferredLanguage(PreferredLanguage.AM)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
        
        User savedUser = userRepository.save(user);
        log.info("User registered successfully with ID: {}", savedUser.getId());
        
        // Step 6: Create role-specific profile (SRS Pages 13-14)
        if (savedUser.getRole() == UserRole.INVESTOR) {
            investorProfileRepository.createDefaultProfile(savedUser.getId());
            log.info("Created investor profile for user: {}", savedUser.getId());
        } else if (savedUser.getRole() == UserRole.MERCHANT) {
            merchantProfileRepository.createDefaultProfile(savedUser.getId());
            log.info("Created merchant profile for user: {}", savedUser.getId());
        }
        
        // Step 7: Publish user.pre_registered event (SRS Page 15)
        eventPublisher.publishUserPreRegistered(savedUser);
        
        // Step 8: Generate and send OTP
        String otpCode = generateOtp();
        Otp otp = Otp.builder()
            .id(UUID.randomUUID())
            .userId(savedUser.getId())
            .otpCode(otpCode)
            .purpose(OtpPurpose.REGISTRATION)
            .expiresAt(LocalDateTime.now().plusMinutes(otpExpiryMinutes))
            .createdAt(LocalDateTime.now())
            .build();
        otpRepository.save(otp);
        
        // Step 9: Send OTP via SMS (SRS Page 15)
        String smsMessage = String.format("Agri-Yield: Your OTP for registration is %s. Valid for %d minutes.", otpCode, otpExpiryMinutes);
        notificationPort.sendSms(phone, smsMessage);
        
        return savedUser;
    }
    
    @Override
    @Transactional
    public String verifyOtp(String phone, String otpCode, String purpose) {
        log.info("Verifying OTP for phone: {}, purpose: {}", phone, purpose);
        
        // Rate limiting check (SRS Page 65)
        String rateLimitKey = "otp:verify:" + phone;
        if (cachePort.exists(rateLimitKey)) {
            long attempts = cachePort.getIncrement(rateLimitKey);
            if (attempts > 5) {
                throw new BusinessException("Too many OTP verification attempts. Try again later.", "RATE_LIMIT_EXCEEDED");
            }
        }
        cachePort.increment(rateLimitKey);
        cachePort.set(rateLimitKey, 1, 1, TimeUnit.MINUTES);
        
        // Find user
        User user = userRepository.findByPhone(phone)
            .orElseThrow(() -> new BusinessException("User not found with phone: " + phone, "USER_NOT_FOUND"));
        
        // Validate OTP
        OtpPurpose otpPurpose = OtpPurpose.valueOf(purpose);
        Otp otp = otpRepository.findByUserIdAndOtpCodeAndPurpose(user.getId(), otpCode, otpPurpose)
            .orElseThrow(() -> new BusinessException("Invalid OTP code", "INVALID_OTP"));
        
        if (!otp.isValid()) {
            throw new BusinessException("OTP has expired or already used", "OTP_EXPIRED");
        }
        
        // Mark OTP as used
        otp.markUsed();
        otpRepository.save(otp);
        
        // For registration, activate the account
        if (otpPurpose == OtpPurpose.REGISTRATION) {
            user.setAccountStatus(AccountStatus.ACTIVE);
            userRepository.save(user);
            
            // Publish user.registered event (SRS Page 16)
            eventPublisher.publishUserRegistered(user);
            
            log.info("User activated successfully: {}", user.getId());
        }
        
        return "VERIFIED";
    }
    
    @Override
    public Map<String, String> login(String phone, String password) {
        log.info("Login attempt for phone: {}", phone);
        
        // Rate limiting (SRS Page 65)
        String rateLimitKey = "login:attempts:" + phone;
        if (cachePort.exists(rateLimitKey)) {
            long attempts = cachePort.getIncrement(rateLimitKey);
            if (attempts > 10) {
                throw new BusinessException("Too many login attempts. Try again later.", "ACCOUNT_LOCKED");
            }
        }
        cachePort.increment(rateLimitKey);
        cachePort.set(rateLimitKey, 1, 1, TimeUnit.HOURS);
        
        // Find user
        User user = userRepository.findByPhone(phone)
            .orElseThrow(() -> new BusinessException("Invalid credentials", "AUTH_FAILED"));
        
        // Check account status
        if (user.getAccountStatus() == AccountStatus.SUSPENDED) {
            throw new BusinessException("Account is suspended. Contact support.", "ACCOUNT_SUSPENDED");
        }
        
        if (user.getAccountStatus() == AccountStatus.PENDING_VERIFICATION) {
            throw new BusinessException("Please verify your OTP first.", "ACCOUNT_NOT_VERIFIED");
        }
        
        // Verify password
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new BusinessException("Invalid credentials", "AUTH_FAILED");
        }
        
        // Update last login time
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);
        
        // Generate tokens (SRS Page 8)
        String accessToken = jwtTokenPort.generateAccessToken(user.getId(), user.getRole(), user.getFaydaId());
        String refreshToken = jwtTokenPort.generateRefreshToken(user.getId());
        
        // Clear rate limit on success
        cachePort.delete(rateLimitKey);
        
        log.info("User logged in successfully: {}", user.getId());
        
        Map<String, String> tokens = new HashMap<>();
        tokens.put("access_token", accessToken);
        tokens.put("refresh_token", refreshToken);
        tokens.put("expires_in", "86400000");
        
        return tokens;
    }
    
    @Override
    public String refreshToken(String refreshToken) {
        log.info("Refreshing token");
        
        if (!jwtTokenPort.isValidRefreshToken(refreshToken)) {
            throw new BusinessException("Invalid or expired refresh token", "INVALID_REFRESH_TOKEN");
        }
        
        JwtTokenPort.TokenValidationResult result = jwtTokenPort.validateToken(refreshToken);
        if (!result.isValid()) {
            throw new BusinessException("Invalid refresh token", "INVALID_REFRESH_TOKEN");
        }
        
        User user = userRepository.findById(result.getUserId())
            .orElseThrow(() -> new BusinessException("User not found", "USER_NOT_FOUND"));
        
        return jwtTokenPort.generateAccessToken(user.getId(), user.getRole(), user.getFaydaId());
    }
    
    @Override
    public void logout(String accessToken, String refreshToken) {
        log.info("Logging out user");
        
        JwtTokenPort.TokenValidationResult result = jwtTokenPort.validateToken(accessToken);
        if (result.isValid()) {
            jwtTokenPort.blacklistToken(accessToken, 86400000);
        }
        
        if (refreshToken != null) {
            jwtTokenPort.removeRefreshToken(refreshToken);
        }
    }
    
    @Override
    public void changePassword(UUID userId, String oldPassword, String newPassword) {
        log.info("Changing password for user: {}", userId);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException("User not found", "USER_NOT_FOUND"));
        
        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            throw new BusinessException("Current password is incorrect", "INVALID_PASSWORD");
        }
        
        validatePassword(newPassword);
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        
        log.info("Password changed successfully for user: {}", userId);
    }
    
    @Override
    public void requestPasswordReset(String phone) {
        log.info("Password reset requested for phone: {}", phone);
        
        User user = userRepository.findByPhone(phone)
            .orElseThrow(() -> new BusinessException("User not found", "USER_NOT_FOUND"));
        
        String otpCode = generateOtp();
        otpRepository.invalidateOldOtps(user.getId(), OtpPurpose.PASSWORD_RESET);
        
        Otp otp = Otp.builder()
            .id(UUID.randomUUID())
            .userId(user.getId())
            .otpCode(otpCode)
            .purpose(OtpPurpose.PASSWORD_RESET)
            .expiresAt(LocalDateTime.now().plusMinutes(otpExpiryMinutes))
            .createdAt(LocalDateTime.now())
            .build();
        otpRepository.save(otp);
        
        notificationPort.sendSms(phone, String.format("Agri-Yield: Use OTP %s to reset your password. Valid for %d minutes.", otpCode, otpExpiryMinutes));
    }
    
    @Override
    @Transactional
    public void resetPassword(String phone, String otpCode, String newPassword) {
        log.info("Resetting password for phone: {}", phone);
        
        User user = userRepository.findByPhone(phone)
            .orElseThrow(() -> new BusinessException("User not found", "USER_NOT_FOUND"));
        
        Otp otp = otpRepository.findByUserIdAndOtpCodeAndPurpose(user.getId(), otpCode, OtpPurpose.PASSWORD_RESET)
            .orElseThrow(() -> new BusinessException("Invalid OTP code", "INVALID_OTP"));
        
        if (!otp.isValid()) {
            throw new BusinessException("OTP has expired or already used", "OTP_EXPIRED");
        }
        
        validatePassword(newPassword);
        otp.markUsed();
        otpRepository.save(otp);
        
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        
        log.info("Password reset successfully for user: {}", user.getId());
    }
    
    private String generateOtp() {
        return String.format("%06d", random.nextInt(1000000));
    }
    
    private void validatePassword(String password) {
        if (password == null || password.length() < 8) {
            throw new BusinessException("Password must be at least 8 characters", "WEAK_PASSWORD");
        }
        if (!password.matches(".*[A-Z].*")) {
            throw new BusinessException("Password must contain at least one uppercase letter", "WEAK_PASSWORD");
        }
        if (!password.matches(".*[a-z].*")) {
            throw new BusinessException("Password must contain at least one lowercase letter", "WEAK_PASSWORD");
        }
        if (!password.matches(".*\\d.*")) {
            throw new BusinessException("Password must contain at least one digit", "WEAK_PASSWORD");
        }
    }
}
