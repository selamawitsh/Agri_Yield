package com.agriyield.voucherservice.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VoucherResponse {

    private UUID id;
    private String voucherCode;
    private UUID investmentId;
    private UUID farmId;
    private UUID farmerId;
    private UUID merchantId;
    private UUID inputNeedId;
    private UUID cropCycleId;
    private String productName;
    private String productCategory;
    private BigDecimal amountEtb;
    private String status;
    private LocalDateTime issuedAt;
    private LocalDateTime redeemedAt;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
}
