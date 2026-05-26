package com.agriyield.voucherservice.application.port.outgoing;

import java.math.BigDecimal;
import java.util.UUID;

public interface EscrowServicePort {

    /** Releases funds from escrow to merchant after voucher redemption */
    void releasePartial(UUID investmentId,
                        UUID voucherId,
                        BigDecimal amountEtb,
                        String releaseReason);
}
