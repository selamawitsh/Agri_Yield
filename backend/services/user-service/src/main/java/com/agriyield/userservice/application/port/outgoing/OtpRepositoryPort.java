package com.agriyield.userservice.application.port.outgoing;

import com.agriyield.userservice.domain.enums.OtpPurpose;
import com.agriyield.userservice.domain.model.Otp;

import java.util.Optional;
import java.util.UUID;

public interface OtpRepositoryPort {

    Otp save(Otp otp);

    Optional<Otp> findByUserIdAndOtpCodeAndPurpose(UUID userId,
                                                    String otpCode,
                                                    OtpPurpose purpose);

    void invalidateOldOtps(UUID userId, OtpPurpose purpose);
}
