package com.agriyield.userservice.core.port.outgoing;

import com.agriyield.userservice.core.domain.model.Otp;
import com.agriyield.userservice.core.domain.enums.OtpPurpose;

import java.util.Optional;
import java.util.UUID;

public interface OtpRepositoryPort {
    Otp save(Otp otp);
    Optional<Otp> findByUserIdAndOtpCodeAndPurpose(UUID userId, String otpCode, OtpPurpose purpose);
    void invalidateOldOtps(UUID userId, OtpPurpose purpose);
    void deleteById(UUID id);
}