package com.agriyield.farmservice.infrastructure.repository;

import com.agriyield.farmservice.infrastructure.adapter.outgoing.persistence.entity.InputNeedItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaInputNeedItemRepository extends JpaRepository<InputNeedItemEntity, UUID> {

    List<InputNeedItemEntity> findByInputNeedId(UUID inputNeedId);
}
