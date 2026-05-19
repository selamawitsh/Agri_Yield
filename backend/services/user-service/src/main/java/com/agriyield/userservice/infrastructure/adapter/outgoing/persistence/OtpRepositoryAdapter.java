package com.agriyield.userservice.infrastructure.adapter.outgoing.persistence;

import com.agriyield.userservice.core.domain.enums.OtpPurpose;
import com.agriyield.userservice.core.domain.model.Otp;
import com.agriyield.userservice.core.port.outgoing.OtpRepositoryPort;
import com.agriyield.userservice.infrastructure.adapter.outgoing.persistence.mapper.EntityDomainMapper;
import com.agriyield.userservice.infrastructure.repository.JpaOtpRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OtpRepositoryAdapter implements OtpRepositoryPort {
    
    private final JpaOtpRepository jpaOtpRepository;
    private final EntityDomainMapper mapper;
    
    @Override
    public Otp save(Otp otp) {
        var entity = mapper.toEntity(otp);
        var savedEntity = jpaOtpRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }
    
    @Override
    public Optional<Otp> findByUserIdAndOtpCodeAndPurpose(UUID userId, String otpCode, OtpPurpose purpose) {
        return jpaOtpRepository.findByUserIdAndOtpCodeAndPurposeAndUsedAtIsNull(userId, otpCode, purpose.getValue())
            .map(mapper::toDomain);
    }
    
    @Override
    @Transactional
    public void invalidateOldOtps(UUID userId, OtpPurpose purpose) {
        jpaOtpRepository.invalidateOldOtps(userId, purpose.getValue());
    }
    
    @Override
    public void deleteById(UUID id) {
        jpaOtpRepository.deleteById(id);
    }
}
