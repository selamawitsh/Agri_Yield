package com.agriyield.escrowservice.domain.model;

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
public class EscrowRelease {

    private UUID id;
    private UUID escrowAccountId;
    private UUID voucherId;
    private BigDecimal amountEtb;
    private String releaseReason;
    private LocalDateTime releasedAt;
}