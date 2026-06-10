package com.agriyield.voucherservice.infrastructure.repository;

import com.agriyield.voucherservice.infrastructure.adapter.outgoing.persistence.entity.VoucherEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaVoucherRepository extends JpaRepository<VoucherEntity, UUID> {

    Optional<VoucherEntity> findByVoucherCode(String voucherCode);

    List<VoucherEntity> findByFarmerIdOrderByCreatedAtDesc(UUID farmerId);

    List<VoucherEntity> findByFarmId(UUID farmId);

    List<VoucherEntity> findByInvestmentId(UUID investmentId);
    List<VoucherEntity> findByMerchantIdOrderByRedeemedAtDesc(UUID merchantId);

    @Query("SELECT v FROM VoucherEntity v WHERE v.status IN ('GENERATED','ISSUED') AND v.expiresAt < :now")
    List<VoucherEntity> findExpiredActiveVouchers(LocalDateTime now);
}
