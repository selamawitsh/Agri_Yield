package com.agriyield.escrowservice.application.port.incoming;

import com.agriyield.escrowservice.domain.model.EscrowAccount;
import com.agriyield.escrowservice.domain.model.EscrowRelease;
import com.agriyield.escrowservice.domain.model.EscrowTransaction;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface EscrowServicePort {

    /** Called by Investment Service after investment is confirmed.
     *  Creates the escrow account and immediately locks the full amount. */
    EscrowAccount createAndLock(UUID investmentId,
                                UUID farmerId,
                                UUID investorId,
                                BigDecimal amountEtb);

    /** Releases a partial amount to the farmer when a voucher batch is redeemed. */
    EscrowRelease releasePartial(UUID investmentId,
                                 UUID voucherId,
                                 BigDecimal amountEtb,
                                 String releaseReason);

    /** Cancels the escrow (e.g. investment cancelled before farm started). */
    EscrowAccount cancel(UUID investmentId);

    /** Returns the current escrow account for an investment. */
    EscrowAccount getByInvestmentId(UUID investmentId);

    /** Returns the full transaction ledger for an escrow account. */
    List<EscrowTransaction> getTransactions(UUID investmentId);
}