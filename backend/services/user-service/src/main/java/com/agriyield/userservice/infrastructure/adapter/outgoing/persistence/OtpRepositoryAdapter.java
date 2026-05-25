package com.agriyield.userservice.infrastructure.adapter.outgoing.persistence;

import com.agriyield.userservice.application.port.outgoing.OtpRepositoryPort;
import com.agriyield.userservice.domain.enums.OtpPurpose;
import com.agriyield.userservice.domain.model.Otp;
import com.agriyield.userservice.infrastructure.adapter.outgoing.persistence.mapper.EntityDomainMapper;
import com.agriyield.userservice.infrastructure.repository.JpaOtpRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OtpRepositoryAdapter implements OtpRepositoryPort {

    private final JpaOtpRepository jpaOtpRepository;
    private final EntityDomainMapper mapper;

    @Override
    public Otp save(Otp otp) {
        return mapper.toDomain(
            jpaOtpRepository.save(mapper.toEntity(otp)));
    }

    @Override
    public Optional<Otp> findByUserIdAndOtpCodeAndPurpose(
            UUID userId, String otpCode, OtpPurpose purpose) {
        return jpaOtpRepository
            .findValidOtp(userId, otpCode, purpose.getValue())
            .map(mapper::toDomain);
    }

    @Override
    public void invalidateOldOtps(UUID userId, OtpPurpose purpose) {
        jpaOtpRepository.invalidateOldOtps(userId, purpose.getValue());
    }
}
