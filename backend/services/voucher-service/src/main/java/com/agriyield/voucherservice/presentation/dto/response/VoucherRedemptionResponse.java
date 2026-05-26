package com.agriyield.voucherservice.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoucherRedemptionResponse {

    private UUID id;
    private UUID voucherId;
    private UUID merchantId;
    private UUID redeemedBy;
    private BigDecimal amountEtb;
    private Boolean escrowReleased;
    private String notes;
    private LocalDateTime redeemedAt;
}
