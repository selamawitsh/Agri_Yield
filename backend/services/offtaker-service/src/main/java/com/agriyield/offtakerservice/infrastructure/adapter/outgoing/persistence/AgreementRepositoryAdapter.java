package com.agriyield.offtakerservice.infrastructure.adapter.outgoing.persistence;

import com.agriyield.offtakerservice.application.port.outgoing.AgreementRepositoryPort;
import com.agriyield.offtakerservice.domain.model.PurchaseAgreement;
import com.agriyield.offtakerservice.infrastructure.adapter.outgoing.persistence.mapper.OfftakerEntityMapper;
import com.agriyield.offtakerservice.infrastructure.repository.JpaPurchaseAgreementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AgreementRepositoryAdapter implements AgreementRepositoryPort {

    private final JpaPurchaseAgreementRepository jpaRepository;
    private final OfftakerEntityMapper mapper;

    @Override
    public PurchaseAgreement save(PurchaseAgreement agreement) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(agreement)));
    }

    @Override
    public Optional<PurchaseAgreement> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<PurchaseAgreement> findByBidId(UUID bidId) {
        return jpaRepository.findByBidId(bidId).map(mapper::toDomain);
    }
}
