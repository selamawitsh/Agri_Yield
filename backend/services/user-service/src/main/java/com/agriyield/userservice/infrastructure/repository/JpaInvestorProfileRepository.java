package com.agriyield.userservice.infrastructure.repository;

import com.agriyield.userservice.infrastructure.adapter.outgoing.persistence.entity.InvestorProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaInvestorProfileRepository extends JpaRepository<InvestorProfileEntity, UUID> {
    Optional<InvestorProfileEntity> findByUserId(UUID userId);
}