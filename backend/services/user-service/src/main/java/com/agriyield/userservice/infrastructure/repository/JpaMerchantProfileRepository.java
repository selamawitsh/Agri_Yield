package com.agriyield.userservice.infrastructure.repository;

import com.agriyield.userservice.infrastructure.adapter.outgoing.persistence.entity.MerchantProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaMerchantProfileRepository extends JpaRepository<MerchantProfileEntity, UUID> {
    Optional<MerchantProfileEntity> findByUserId(UUID userId);
}
