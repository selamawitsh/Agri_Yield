package com.agriyield.offtakerservice.presentation.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class DispatchResponse {
    private UUID id;
    private UUID agreementId;
    private String driverFaydaId;
    private int truckCount;
    private LocalDate scheduledPickupDate;
    private LocalDate actualPickupDate;
    private BigDecimal driverPenaltyEscrowEtb;
    private String status;
    private OffsetDateTime createdAt;
}
