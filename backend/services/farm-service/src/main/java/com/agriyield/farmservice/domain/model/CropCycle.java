package com.agriyield.farmservice.domain.model;

import com.agriyield.farmservice.domain.enums.CropCycleStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CropCycle {

    private UUID id;
    private UUID farmId;

    // e.g. Kiremt_2026
    private String seasonName;

    private LocalDate plantingDate;
    private LocalDate expectedHarvestDate;
    private LocalDate actualHarvestDate;

    private CropCycleStatus status;

    private LocalDateTime createdAt;

    public void confirmPlanting(LocalDate plantingDate) {
        this.plantingDate = plantingDate;
        this.status = CropCycleStatus.PLANTED;
    }

    public void markFunded() {
        this.status = CropCycleStatus.FUNDED;
    }

    public void markGrowing() {
        this.status = CropCycleStatus.GROWING;
    }

    public void markHarvested(LocalDate actualHarvestDate) {
        this.actualHarvestDate = actualHarvestDate;
        this.status = CropCycleStatus.HARVESTED;
    }

    public void markFailed() {
        this.status = CropCycleStatus.FAILED;
    }
}
