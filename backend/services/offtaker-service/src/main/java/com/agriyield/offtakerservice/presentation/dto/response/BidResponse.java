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
    private BigDecimal quantityQuintals;
    private BigDecimal pricePerQuintalEtb;
    private BigDecimal totalValueEtb;
    private BigDecimal bidDepositEtb;
    private String status;
    private OffsetDateTime expiresAt;
    private OffsetDateTime acceptedAt;
    private OffsetDateTime createdAt;
}
