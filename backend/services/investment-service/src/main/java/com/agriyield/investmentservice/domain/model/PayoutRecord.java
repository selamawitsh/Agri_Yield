package com.agriyield.investmentservice.domain.model;

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
public class PayoutRecord {

    private UUID id;
    private UUID investmentId;
    private UUID investorId;
    private UUID farmId;
    private UUID listingId;
    private BigDecimal principalEtb;
    private BigDecimal returnEtb;
    private BigDecimal totalEtb;
    private BigDecimal actualApr;
    private String payoutReason;
    private LocalDateTime paidAt;
}
