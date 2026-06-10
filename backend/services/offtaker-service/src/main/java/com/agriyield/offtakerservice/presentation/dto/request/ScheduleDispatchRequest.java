package com.agriyield.offtakerservice.presentation.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class ScheduleDispatchRequest {
    @NotNull
    private UUID agreementId;

    @NotBlank
    private String driverFaydaId;

    @Min(1)
    private int truckCount;

    @NotNull @Future
    private LocalDate scheduledPickupDate;
}
