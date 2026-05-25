package com.agriyield.merchantservice.infrastructure.repository;

import com.agriyield.merchantservice.infrastructure.adapter.outgoing.persistence.entity.PriceHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JpaPriceHistoryRepository extends JpaRepository<PriceHistoryEntity, UUID> {
    List<PriceHistoryEntity> findByProductId(UUID productId);
}
