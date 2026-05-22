package com.agriyield.escrowservice.presentation.dto.response;

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
public class EscrowAccountResponse {

    private UUID id;
    private UUID investmentId;
    private UUID farmerId;
    private UUID investorId;
    private BigDecimal totalAmountEtb;
    private BigDecimal lockedAmountEtb;
    private BigDecimal releasedAmountEtb;
    private BigDecimal remainingAmountEtb;
    private String status;
    private LocalDateTime lockExpiresAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
