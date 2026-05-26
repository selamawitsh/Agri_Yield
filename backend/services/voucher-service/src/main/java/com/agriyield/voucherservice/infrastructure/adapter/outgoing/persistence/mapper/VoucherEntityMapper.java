package com.agriyield.voucherservice.infrastructure.adapter.outgoing.persistence.mapper;

import com.agriyield.voucherservice.domain.enums.ProductCategory;
import com.agriyield.voucherservice.domain.enums.VoucherStatus;
import com.agriyield.voucherservice.domain.model.Voucher;
import com.agriyield.voucherservice.domain.model.VoucherRedemption;
import com.agriyield.voucherservice.infrastructure.adapter.outgoing.persistence.entity.VoucherEntity;
import com.agriyield.voucherservice.infrastructure.adapter.outgoing.persistence.entity.VoucherRedemptionEntity;
import org.springframework.stereotype.Component;

@Component
public class VoucherEntityMapper {

    // ─── Voucher ─────────────────────────────────────────────

    public Voucher toDomain(VoucherEntity entity) {
        if (entity == null) return null;
        return Voucher.builder()
            .id(entity.getId())
            .voucherCode(entity.getVoucherCode())
            .investmentId(entity.getInvestmentId())
            .farmId(entity.getFarmId())
            .farmerId(entity.getFarmerId())
            .merchantId(entity.getMerchantId())
            .inputNeedId(entity.getInputNeedId())
            .inputNeedItemId(entity.getInputNeedItemId())
            .cropCycleId(entity.getCropCycleId())
            .productName(entity.getProductName())
            .productCategory(ProductCategory.fromValue(entity.getProductCategory()))
            .amountEtb(entity.getAmountEtb())
            .status(VoucherStatus.fromValue(entity.getStatus()))
            .issuedAt(entity.getIssuedAt())
            .redeemedAt(entity.getRedeemedAt())
            .expiresAt(entity.getExpiresAt())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .build();
    }

    public VoucherEntity toEntity(Voucher domain) {
        if (domain == null) return null;
        return VoucherEntity.builder()
            .id(domain.getId())
            .voucherCode(domain.getVoucherCode())
            .investmentId(domain.getInvestmentId())
            .farmId(domain.getFarmId())
            .farmerId(domain.getFarmerId())
            .merchantId(domain.getMerchantId())
            .inputNeedId(domain.getInputNeedId())
            .inputNeedItemId(domain.getInputNeedItemId())
            .cropCycleId(domain.getCropCycleId())
            .productName(domain.getProductName())
            .productCategory(domain.getProductCategory().getValue())
            .amountEtb(domain.getAmountEtb())
            .status(domain.getStatus().getValue())
            .issuedAt(domain.getIssuedAt())
            .redeemedAt(domain.getRedeemedAt())
            .expiresAt(domain.getExpiresAt())
            .createdAt(domain.getCreatedAt())
            .updatedAt(domain.getUpdatedAt())
            .build();
    }

    // ─── VoucherRedemption ───────────────────────────────────

    public VoucherRedemption toDomain(VoucherRedemptionEntity entity) {
        if (entity == null) return null;
        return VoucherRedemption.builder()
            .id(entity.getId())
            .voucherId(entity.getVoucherId())
            .merchantId(entity.getMerchantId())
            .redeemedBy(entity.getRedeemedBy())
            .amountEtb(entity.getAmountEtb())
            .escrowReleased(entity.getEscrowReleased())
            .notes(entity.getNotes())
            .redeemedAt(entity.getRedeemedAt())
            .build();
    }

    public VoucherRedemptionEntity toEntity(VoucherRedemption domain) {
        if (domain == null) return null;
        return VoucherRedemptionEntity.builder()
            .id(domain.getId())
            .voucherId(domain.getVoucherId())
            .merchantId(domain.getMerchantId())
            .redeemedBy(domain.getRedeemedBy())
            .amountEtb(domain.getAmountEtb())
            .escrowReleased(domain.getEscrowReleased())
            .notes(domain.getNotes())
            .redeemedAt(domain.getRedeemedAt())
            .build();
    }
}
