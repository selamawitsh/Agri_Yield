package com.agriyield.offtakerservice.domain.model;

import com.agriyield.offtakerservice.domain.enums.DispatchStatus;
import com.agriyield.offtakerservice.domain.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TruckDispatch {
    private UUID id;
    private UUID agreementId;
    private String driverFaydaId;
    private int truckCount;
    private LocalDate scheduledPickupDate;
    private LocalDate actualPickupDate;
    private BigDecimal driverPenaltyEscrowEtb;
    private DispatchStatus status;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public void confirmArrival(LocalDate arrivalDate) {
        if (this.status != DispatchStatus.SCHEDULED) {
            throw new BusinessException("Only SCHEDULED dispatches can be confirmed as arrived", "INVALID_DISPATCH_STATUS");
        }
        this.status = DispatchStatus.ARRIVED;
        this.actualPickupDate = arrivalDate;
        this.updatedAt = OffsetDateTime.now();
    }

    public void confirmLoading() {
        if (this.status != DispatchStatus.ARRIVED) {
            throw new BusinessException("Only ARRIVED dispatches can be marked as LOADED", "INVALID_DISPATCH_STATUS");
        }
        this.status = DispatchStatus.LOADED;
        this.updatedAt = OffsetDateTime.now();
    }

    public void confirmDelivery() {
        if (this.status != DispatchStatus.LOADED) {
            throw new BusinessException("Only LOADED dispatches can be marked as DELIVERED", "INVALID_DISPATCH_STATUS");
        }
        this.status = DispatchStatus.DELIVERED;
        this.updatedAt = OffsetDateTime.now();
    }

    public void markDriverDefaulted() {
        this.status = DispatchStatus.DRIVER_DEFAULTED;
        this.updatedAt = OffsetDateTime.now();
    }
}
