package com.agriyield.voucherservice.application.port.outgoing;

import com.agriyield.voucherservice.domain.model.Voucher;
import com.agriyield.voucherservice.domain.model.VoucherRedemption;

import java.util.List;
import java.util.UUID;

public interface EventPublisherPort {
    void publishVouchersGenerated(List<Voucher> vouchers);
    void publishVoucherRedeemed(Voucher voucher, VoucherRedemption redemption);
    void publishVoucherExpired(Voucher voucher);
    void publishVoucherCancelled(Voucher voucher);
    void publishVoucherRejected(Voucher voucher, UUID merchantId, String reason);
}
