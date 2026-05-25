package com.agriyield.merchantservice.application.port.outgoing;

import com.agriyield.merchantservice.domain.model.PriceAnomaly;

import java.util.List;
import java.util.UUID;

public interface PriceAnomalyRepositoryPort {
    PriceAnomaly save(PriceAnomaly priceAnomaly);
    List<PriceAnomaly> findByMerchantId(UUID merchantId);
    List<PriceAnomaly> findUnresolvedByMerchantId(UUID merchantId);
}
