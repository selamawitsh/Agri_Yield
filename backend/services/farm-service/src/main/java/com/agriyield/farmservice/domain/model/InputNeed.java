package com.agriyield.farmservice.domain.model;

import com.agriyield.farmservice.domain.enums.InputNeedStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InputNeed {

    private UUID id;
    private UUID farmId;
    private UUID cropCycleId;

    private BigDecimal totalAmountEtb;
    private BigDecimal fundedAmountEtb;

    private InputNeedStatus status;
    private LocalDateTime createdAt;

    private List<InputNeedItem> items;

    public void updateFundedAmount(BigDecimal amount) {
        this.fundedAmountEtb = this.fundedAmountEtb.add(amount);
        if (this.fundedAmountEtb.compareTo(this.totalAmountEtb) >= 0) {
            this.status = InputNeedStatus.FULLY_FUNDED;
        } else if (this.fundedAmountEtb.compareTo(BigDecimal.ZERO) > 0) {
            this.status = InputNeedStatus.PARTIALLY_FUNDED;
        }
    }

    public void cancel() {
        this.status = InputNeedStatus.CANCELLED;
    }
}
