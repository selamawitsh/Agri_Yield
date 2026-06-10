package com.agriyield.voucherservice.application.port.outgoing;

import com.agriyield.voucherservice.domain.model.Voucher;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VoucherRepositoryPort {

    Voucher save(Voucher voucher);

    Optional<Voucher> findById(UUID id);

    Optional<Voucher> findByVoucherCode(String voucherCode);

    List<Voucher> findByFarmerId(UUID farmerId);

    List<Voucher> findByFarmId(UUID farmId);

    List<Voucher> findByInvestmentId(UUID investmentId);

    List<Voucher> findExpiredActiveVouchers();
    List<Voucher> findByMerchantId(UUID merchantId);
}
