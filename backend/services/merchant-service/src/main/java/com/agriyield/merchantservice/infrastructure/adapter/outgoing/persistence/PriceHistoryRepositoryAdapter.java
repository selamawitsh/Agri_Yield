package com.agriyield.merchantservice.infrastructure.adapter.outgoing.persistence;

import com.agriyield.merchantservice.application.port.outgoing.PriceHistoryRepositoryPort;
import com.agriyield.merchantservice.domain.model.PriceHistory;
import com.agriyield.merchantservice.infrastructure.adapter.outgoing.persistence.mapper.MerchantEntityMapper;
import com.agriyield.merchantservice.infrastructure.repository.JpaPriceHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PriceHistoryRepositoryAdapter implements PriceHistoryRepositoryPort {

    private final JpaPriceHistoryRepository jpaRepository;
    private final MerchantEntityMapper mapper;

    @Override
    public PriceHistory save(PriceHistory priceHistory) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(priceHistory)));
    }

    @Override
    public List<PriceHistory> findByProductId(UUID productId) {
        return jpaRepository.findByProductId(productId).stream()
                .map(mapper::toDomain).collect(Collectors.toList());
    }
}
