package com.agriyield.merchantservice.infrastructure.repository;

import com.agriyield.merchantservice.infrastructure.adapter.outgoing.persistence.entity.MerchantProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface JpaMerchantProfileRepository extends JpaRepository<MerchantProfileEntity, UUID> {
    Optional<MerchantProfileEntity> findByUserId(UUID userId);
}
