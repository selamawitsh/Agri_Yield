package com.agriyield.offtakerservice.presentation.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class BidResponse {
    private UUID id;
    private UUID offtakerId;
    private UUID farmId;
    private UUID cropCycleId;
    private BigDecimal quantityQuintals;
    private BigDecimal pricePerQuintalEtb;
    private BigDecimal totalValueEtb;
    private BigDecimal bidDepositEtb;
    private String status;
    private OffsetDateTime expiresAt;
    private OffsetDateTime acceptedAt;
    private OffsetDateTime createdAt;

    /**
     * FIX: agreementId is populated once the farmer accepts the bid and an agreement
     * is created. Null for PENDING/REJECTED/EXPIRED bids.
     * Without this field the entire frontend agreement + logistics flow is broken
     * because the agreement UUID differs from the bid UUID.
     */
    private UUID agreementId;
}