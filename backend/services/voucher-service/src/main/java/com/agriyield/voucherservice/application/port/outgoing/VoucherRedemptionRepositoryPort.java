package com.agriyield.voucherservice.application.port.outgoing;

import com.agriyield.voucherservice.domain.model.VoucherRedemption;

import java.util.List;
import java.util.UUID;

public interface VoucherRedemptionRepositoryPort {

    VoucherRedemption save(VoucherRedemption redemption);

    List<VoucherRedemption> findByVoucherId(UUID voucherId);

    List<VoucherRedemption> findByMerchantId(UUID merchantId);
}
