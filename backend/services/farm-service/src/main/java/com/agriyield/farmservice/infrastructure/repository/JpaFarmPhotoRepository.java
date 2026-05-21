package com.agriyield.farmservice.infrastructure.repository;

import com.agriyield.farmservice.infrastructure.adapter.outgoing.persistence.entity.FarmPhotoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaFarmPhotoRepository extends JpaRepository<FarmPhotoEntity, UUID> {

    List<FarmPhotoEntity> findByFarmId(UUID farmId);
}
