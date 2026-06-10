package com.agriyield.offtakerservice.infrastructure.repository;

import com.agriyield.offtakerservice.infrastructure.adapter.outgoing.persistence.entity.TruckDispatchEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JpaTruckDispatchRepository extends JpaRepository<TruckDispatchEntity, UUID> {
    List<TruckDispatchEntity> findByAgreementId(UUID agreementId);
}
