package com.agriyield.merchantservice.application.port.outgoing;

import com.agriyield.merchantservice.domain.model.PriceHistory;

import java.util.List;
import java.util.UUID;

public interface PriceHistoryRepositoryPort {
    PriceHistory save(PriceHistory priceHistory);
    List<PriceHistory> findByProductId(UUID productId);
}
