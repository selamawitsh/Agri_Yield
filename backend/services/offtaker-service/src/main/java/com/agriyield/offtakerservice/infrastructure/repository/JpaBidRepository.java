package com.agriyield.offtakerservice.infrastructure.repository;

import com.agriyield.offtakerservice.domain.enums.BidStatus;
import com.agriyield.offtakerservice.infrastructure.adapter.outgoing.persistence.entity.BidEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface JpaBidRepository extends JpaRepository<BidEntity, UUID> {
    List<BidEntity> findByOfftakerId(UUID offtakerId);
    List<BidEntity> findByFarmId(UUID farmId);
    List<BidEntity> findByStatusAndExpiresAtBefore(BidStatus status, OffsetDateTime dateTime);
}
