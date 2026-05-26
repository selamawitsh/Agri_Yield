package com.agriyield.voucherservice.domain.exception;

public class VoucherNotFoundException extends BusinessException {

    public VoucherNotFoundException(String id) {
        super("Voucher not found: " + id, "VOUCHER_NOT_FOUND");
    }
}
