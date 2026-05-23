package com.agriyield.investmentservice.presentation.dto.response;

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
public class PayoutRecordResponse {

    private UUID id;
    private UUID investmentId;
    private UUID farmId;
    private UUID listingId;
    private BigDecimal principalEtb;
    private BigDecimal returnEtb;
    private BigDecimal totalEtb;
    private BigDecimal actualApr;
    private String payoutReason;
    private LocalDateTime paidAt;
}
