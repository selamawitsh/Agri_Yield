package com.agriyield.merchantservice.infrastructure.repository;

import com.agriyield.merchantservice.infrastructure.adapter.outgoing.persistence.entity.PriceAnomalyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JpaPriceAnomalyRepository extends JpaRepository<PriceAnomalyEntity, UUID> {
    List<PriceAnomalyEntity> findByMerchantId(UUID merchantId);
    List<PriceAnomalyEntity> findByMerchantIdAndResolvedAtIsNull(UUID merchantId);
}
