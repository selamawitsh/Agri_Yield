package com.agriyield.merchantservice.infrastructure.adapter.outgoing.persistence;

import com.agriyield.merchantservice.application.port.outgoing.PriceAnomalyRepositoryPort;
import com.agriyield.merchantservice.domain.model.PriceAnomaly;
import com.agriyield.merchantservice.infrastructure.adapter.outgoing.persistence.mapper.MerchantEntityMapper;
import com.agriyield.merchantservice.infrastructure.repository.JpaPriceAnomalyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PriceAnomalyRepositoryAdapter implements PriceAnomalyRepositoryPort {

    private final JpaPriceAnomalyRepository jpaRepository;
    private final MerchantEntityMapper mapper;

    @Override
    public PriceAnomaly save(PriceAnomaly priceAnomaly) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(priceAnomaly)));
    }

    @Override
    public List<PriceAnomaly> findByMerchantId(UUID merchantId) {
        return jpaRepository.findByMerchantId(merchantId).stream()
                .map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<PriceAnomaly> findUnresolvedByMerchantId(UUID merchantId) {
        return jpaRepository.findByMerchantIdAndResolvedAtIsNull(merchantId).stream()
                .map(mapper::toDomain).collect(Collectors.toList());
    }
}
