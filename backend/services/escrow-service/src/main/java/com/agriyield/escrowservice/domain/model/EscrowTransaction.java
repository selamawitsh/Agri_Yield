package com.agriyield.escrowservice.domain.model;

import com.agriyield.escrowservice.domain.enums.TransactionType;
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
public class EscrowTransaction {

    private UUID id;
    private UUID escrowAccountId;
    private TransactionType transactionType;
    private BigDecimal amountEtb;
    private UUID referenceId;
    private String description;
    private LocalDateTime createdAt;
}