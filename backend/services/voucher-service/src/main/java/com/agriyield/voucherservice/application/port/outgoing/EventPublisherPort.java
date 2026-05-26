package com.agriyield.voucherservice.application.port.outgoing;

import com.agriyield.voucherservice.domain.model.Voucher;
import com.agriyield.voucherservice.domain.model.VoucherRedemption;

import java.util.List;

public interface EventPublisherPort {

    void publishVouchersGenerated(List<Voucher> vouchers);

    void publishVoucherRedeemed(Voucher voucher, VoucherRedemption redemption);

    void publishVoucherExpired(Voucher voucher);

    void publishVoucherCancelled(Voucher voucher);
}
