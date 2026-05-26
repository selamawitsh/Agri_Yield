package com.agriyield.voucherservice.infrastructure.repository;

import com.agriyield.voucherservice.infrastructure.adapter.outgoing.persistence.entity.VoucherRedemptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JpaVoucherRedemptionRepository extends JpaRepository<VoucherRedemptionEntity, UUID> {

    List<VoucherRedemptionEntity> findByVoucherId(UUID voucherId);

    List<VoucherRedemptionEntity> findByMerchantIdOrderByRedeemedAtDesc(UUID merchantId);
}
