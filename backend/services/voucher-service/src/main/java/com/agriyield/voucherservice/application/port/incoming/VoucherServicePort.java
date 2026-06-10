package com.agriyield.voucherservice.application.port.incoming;

import com.agriyield.voucherservice.domain.model.Voucher;
import com.agriyield.voucherservice.domain.model.VoucherRedemption;

import java.util.List;
import java.util.UUID;

public interface VoucherServicePort {

    /** Generate all vouchers for a fully-funded investment.
     *  Called automatically when investment.fully.funded event arrives. */
    List<Voucher> generateForInvestment(UUID investmentId,
                                         UUID farmId,
                                         UUID farmerId,
                                         UUID inputNeedId,
                                         UUID cropCycleId);

    /** Farmer views their vouchers */
    List<Voucher> getMyVouchers(UUID farmerId);

    /** Get a single voucher by ID */
    Voucher getById(UUID voucherId);

    /** Get voucher by code (for merchant POS scan) */
    Voucher getByCode(String voucherCode);

    /** Merchant redeems a voucher at POS — triggers escrow release */
    VoucherRedemption redeem(String voucherCode,
                              UUID merchantId,
                              UUID redeemedBy,
                              String notes);

    /** Get redemption history for a voucher */
    List<VoucherRedemption> getRedemptions(UUID voucherId);

    /** Get all vouchers for a farm */
    List<Voucher> getByFarmId(UUID farmId);

    /** Returns all REDEEMED vouchers for a given merchant. */
    List<Voucher> getByMerchantId(UUID merchantId);

    /** Expire vouchers past their deadline — called by scheduler */
    void expireOverdueVouchers();

    /** Cancel all vouchers for an investment (e.g. investment cancelled) */
    void cancelForInvestment(UUID investmentId);
}
