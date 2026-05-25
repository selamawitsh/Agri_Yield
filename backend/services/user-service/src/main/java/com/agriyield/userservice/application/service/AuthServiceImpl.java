package com.agriyield.userservice.application.service;

import com.agriyield.userservice.application.port.incoming.AuthServicePort;
import com.agriyield.userservice.application.port.outgoing.*;
import com.agriyield.userservice.domain.enums.*;
import com.agriyield.userservice.domain.exception.BusinessException;
import com.agriyield.userservice.domain.model.Otp;
import com.agriyield.userservice.domain.model.User;
import com.agriyield.userservice.infrastructure.adapter.incoming.messaging.UserEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
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

    // US-01
    @Override
    @Transactional
    public User register(String phone, String faydaId, String password,
                         String role, String fullName) {
        log.info("Registering user phone: {}, role: {}", phone, role);

        if (!phone.matches("^\\+251[0-9]{9}$")) {
            throw new BusinessException(
                "Invalid Ethiopian phone number. Use +251XXXXXXXXX",
                "INVALID_PHONE");
        }
        if (userRepository.existsByPhone(phone)) {
            throw new BusinessException(
                "Phone number already registered", "DUPLICATE_PHONE");
        }
        if (userRepository.existsByFaydaId(faydaId)) {
            throw new BusinessException(
                "Fayda ID already registered", "DUPLICATE_FAYDA_ID");
        }

        boolean faydaVerified = faydaVerificationPort.verifyFaydaId(
            faydaId, phone, fullName);
        if (!faydaVerified) {
            throw new BusinessException(
                "Fayda ID verification failed", "FAYDA_VERIFICATION_FAILED");
        }

        faydaVerificationPort.pullKycData(faydaId);

        User user = User.builder()
            .id(UUID.randomUUID())
            .phone(phone)
            .faydaId(faydaId)
            .passwordHash(passwordEncoder.encode(password))
            .role(UserRole.fromValue(role))
            .kycStatus(KycStatus.PENDING)
            .accountStatus(AccountStatus.PENDING_VERIFICATION)
            .preferredLanguage(PreferredLanguage.AM)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        User saved = userRepository.save(user);

        if (saved.getRole() == UserRole.INVESTOR) {
            investorProfileRepository.createDefaultProfile(saved.getId());
        } else if (saved.getRole() == UserRole.MERCHANT) {
            merchantProfileRepository.createDefaultProfile(saved.getId());
        }

        cachePort.set("user:fullname:" + saved.getId(),
            fullName, 30, TimeUnit.MINUTES);

        eventPublisher.publishUserPreRegistered(saved);

        String otpCode = generateOtp();
        Otp otp = Otp.builder()
            .id(UUID.randomUUID())
            .userId(saved.getId())
            .otpCode(otpCode)
            .purpose(OtpPurpose.REGISTRATION)
            .expiresAt(LocalDateTime.now().plusMinutes(otpExpiryMinutes))
            .createdAt(LocalDateTime.now())
            .build();
        otpRepository.save(otp);

        notificationPort.sendSms(phone, String.format(
            "Agri-Yield: Your OTP is %s. Valid for %d minutes.",
            otpCode, otpExpiryMinutes));

        log.info("User registered: {}", saved.getId());
        return saved;
    }

    // US-02
    @Override
    @Transactional
    public String verifyOtp(String phone, String otpCode, String purpose) {
        log.info("Verifying OTP for phone: {}", phone);

        String rateLimitKey = "otp:verify:" + phone;
        if (cachePort.exists(rateLimitKey) &&
            cachePort.getIncrement(rateLimitKey) > 5) {
            throw new BusinessException(
                "Too many OTP attempts. Try again later.",
                "RATE_LIMIT_EXCEEDED");
        }
        cachePort.increment(rateLimitKey);
        cachePort.set(rateLimitKey, 1, 10, TimeUnit.MINUTES);

        User user = userRepository.findByPhone(phone)
            .orElseThrow(() -> new BusinessException(
                "User not found", "USER_NOT_FOUND"));

        OtpPurpose otpPurpose = OtpPurpose.valueOf(purpose);
        Otp otp = otpRepository.findByUserIdAndOtpCodeAndPurpose(
                user.getId(), otpCode, otpPurpose)
            .orElseThrow(() -> new BusinessException(
                "Invalid OTP code", "INVALID_OTP"));

        if (!otp.isValid()) {
            throw new BusinessException(
                "OTP has expired or already used", "OTP_EXPIRED");
        }

        otp.markUsed();
        otpRepository.save(otp);

        if (otpPurpose == OtpPurpose.REGISTRATION) {
            user.activate();
            userRepository.save(user);

            String fullName = cachePort.get("user:fullname:" + user.getId())
                .map(Object::toString).orElse("");
            cachePort.delete("user:fullname:" + user.getId());
            eventPublisher.publishUserRegistered(user, fullName);

            log.info("User activated: {}", user.getId());
        }

        return "VERIFIED";
    }

    // US-03
    @Override
    public Map<String, String> login(String phone, String password) {
        log.info("Login attempt: {}", phone);

        String rateLimitKey = "login:attempts:" + phone;
        if (cachePort.exists(rateLimitKey) &&
            cachePort.getIncrement(rateLimitKey) > 10) {
            throw new BusinessException(
                "Too many login attempts. Try again later.",
                "ACCOUNT_LOCKED");
        }
        cachePort.increment(rateLimitKey);
        cachePort.set(rateLimitKey, 1, 1, TimeUnit.HOURS);

        User user = userRepository.findByPhone(phone)
            .orElseThrow(() -> new BusinessException(
                "Invalid credentials", "AUTH_FAILED"));

        if (user.getAccountStatus() == AccountStatus.SUSPENDED) {
            throw new BusinessException(
                "Account suspended. Contact support.", "ACCOUNT_SUSPENDED");
        }
        if (user.getAccountStatus() == AccountStatus.PENDING_VERIFICATION) {
            throw new BusinessException(
                "Please verify your OTP first.", "ACCOUNT_NOT_VERIFIED");
        }
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new BusinessException("Invalid credentials", "AUTH_FAILED");
        }

        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        String accessToken = jwtTokenPort.generateAccessToken(
            user.getId(), user.getRole(), user.getFaydaId());
        String refreshToken = jwtTokenPort.generateRefreshToken(user.getId());

        cachePort.delete(rateLimitKey);
        log.info("Login successful: {}", user.getId());

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);
        tokens.put("expiresIn", "86400000");
        return tokens;
    }

    // US-04
    @Override
    public String refreshToken(String refreshToken) {
        if (!jwtTokenPort.isValidRefreshToken(refreshToken)) {
            throw new BusinessException(
                "Invalid or expired refresh token",
                "INVALID_REFRESH_TOKEN");
        }
        JwtTokenPort.TokenValidationResult result =
            jwtTokenPort.validateToken(refreshToken);
        if (!result.isValid()) {
            throw new BusinessException(
                "Invalid refresh token", "INVALID_REFRESH_TOKEN");
        }
        User user = userRepository.findById(result.getUserId())
            .orElseThrow(() -> new BusinessException(
                "User not found", "USER_NOT_FOUND"));
        return jwtTokenPort.generateAccessToken(
            user.getId(), user.getRole(), user.getFaydaId());
    }

    // US-05
    @Override
    public void logout(String accessToken, String refreshToken) {
        JwtTokenPort.TokenValidationResult result =
            jwtTokenPort.validateToken(accessToken);
        if (result.isValid()) {
            jwtTokenPort.blacklistToken(accessToken, 86400000);
        }
        if (refreshToken != null) {
            jwtTokenPort.removeRefreshToken(refreshToken);
        }
    }

    @Override
    public void changePassword(UUID userId, String oldPassword,
                               String newPassword) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(
                "User not found", "USER_NOT_FOUND"));
        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            throw new BusinessException(
                "Current password is incorrect", "INVALID_PASSWORD");
        }
        validatePassword(newPassword);
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    @Override
    public void requestPasswordReset(String phone) {
        User user = userRepository.findByPhone(phone)
            .orElseThrow(() -> new BusinessException(
                "User not found", "USER_NOT_FOUND"));
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
        notificationPort.sendSms(phone, String.format(
            "Agri-Yield: Use OTP %s to reset your password. Valid %d minutes.",
            otpCode, otpExpiryMinutes));
    }

    @Override
    @Transactional
    public void resetPassword(String phone, String otpCode,
                              String newPassword) {
        User user = userRepository.findByPhone(phone)
            .orElseThrow(() -> new BusinessException(
                "User not found", "USER_NOT_FOUND"));
        Otp otp = otpRepository.findByUserIdAndOtpCodeAndPurpose(
                user.getId(), otpCode, OtpPurpose.PASSWORD_RESET)
            .orElseThrow(() -> new BusinessException(
                "Invalid OTP", "INVALID_OTP"));
        if (!otp.isValid()) {
            throw new BusinessException(
                "OTP expired or already used", "OTP_EXPIRED");
        }
        validatePassword(newPassword);
        otp.markUsed();
        otpRepository.save(otp);
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    private String generateOtp() {
        return String.format("%06d", random.nextInt(1000000));
    }

    private void validatePassword(String password) {
        if (password == null || password.length() < 8) {
            throw new BusinessException(
                "Password must be at least 8 characters", "WEAK_PASSWORD");
        }
        if (!password.matches(".*[A-Z].*")) {
            throw new BusinessException(
                "Password must contain an uppercase letter", "WEAK_PASSWORD");
        }
        if (!password.matches(".*[a-z].*")) {
            throw new BusinessException(
                "Password must contain a lowercase letter", "WEAK_PASSWORD");
        }
        if (!password.matches(".*\\d.*")) {
            throw new BusinessException(
                "Password must contain a digit", "WEAK_PASSWORD");
        }
    }
}
