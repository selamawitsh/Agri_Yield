package com.agriyield.investmentservice.infrastructure.repository;

import com.agriyield.investmentservice.infrastructure.adapter.outgoing.persistence.entity.PayoutRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JpaPayoutRecordRepository extends JpaRepository<PayoutRecordEntity, UUID> {

    List<PayoutRecordEntity> findByInvestorIdOrderByPaidAtDesc(UUID investorId);

    List<PayoutRecordEntity> findByInvestmentId(UUID investmentId);
}
