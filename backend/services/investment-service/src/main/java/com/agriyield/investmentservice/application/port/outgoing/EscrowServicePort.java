package com.agriyield.investmentservice.application.port.outgoing;

import java.math.BigDecimal;
import java.util.UUID;

public interface EscrowServicePort {

    /** Calls escrow-service via gRPC to create + lock the escrow account. */
    void createAndLock(UUID investmentId,
                       UUID farmerId,
                       UUID investorId,
                       BigDecimal amountEtb);

    /** Cancels the escrow (returns funds to investor). */
    void cancel(UUID investmentId);
}
