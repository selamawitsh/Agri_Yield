package com.agriyield.investmentservice.domain.model;

import com.agriyield.investmentservice.domain.enums.ListingStatus;
import com.agriyield.investmentservice.domain.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FarmListing {

    private UUID id;
    private UUID farmId;
    private UUID farmerId;
    private UUID inputNeedId;
    private UUID cropCycleId;
    private String cropType;
    private String region;
    private String kebeleCode;
    private String seasonName;
    private BigDecimal totalAmountEtb;
    private BigDecimal fundedAmountEtb;
    private BigDecimal fundingPct;
    private BigDecimal baseApr;
    private BigDecimal currentApr;
    private BigDecimal ndviBonus;
    private BigDecimal weatherBonus;
    private BigDecimal ndviPenalty;
    private BigDecimal droughtRisk;
    private int agriScore;
    private ListingStatus status;
    // FIX: was missing — needed for the satellite verified filter and badge.
    // Defaults to false; should be set from farm.satellite.verified event
    // or fetched from farm-service when the listing is created/updated.
    @Builder.Default
    private boolean satelliteVerified = false;
    private LocalDateTime fundingDeadline;
    private LocalDateTime fullyFundedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void addFunding(BigDecimal amount) {
        if (this.status != ListingStatus.OPEN && this.status != ListingStatus.PARTIALLY_FUNDED) {
            throw new BusinessException(
                    "Listing is not accepting investments. Status: " + this.status.getValue(),
                    "LISTING_NOT_OPEN");
        }
        this.fundedAmountEtb = this.fundedAmountEtb.add(amount);
        this.fundingPct = this.fundedAmountEtb
                .divide(this.totalAmountEtb, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);

        if (this.fundedAmountEtb.compareTo(this.totalAmountEtb) >= 0) {
            this.status = ListingStatus.FULLY_FUNDED;
            this.fullyFundedAt = LocalDateTime.now();
        } else {
            this.status = ListingStatus.PARTIALLY_FUNDED;
        }
        this.updatedAt = LocalDateTime.now();
    }

    public void recalculateApr(BigDecimal ndviBonus,
                               BigDecimal weatherBonus,
                               BigDecimal ndviPenalty,
                               BigDecimal droughtRisk) {
        this.ndviBonus = ndviBonus;
        this.weatherBonus = weatherBonus;
        this.ndviPenalty = ndviPenalty;
        this.droughtRisk = droughtRisk;
        this.currentApr = this.baseApr
                .add(ndviBonus)
                .add(weatherBonus)
                .subtract(ndviPenalty)
                .subtract(droughtRisk)
                .max(BigDecimal.valueOf(1.0));
        this.updatedAt = LocalDateTime.now();
    }

    public void markFundingFailed() {
        if (this.status == ListingStatus.FULLY_FUNDED
                || this.status == ListingStatus.COMPLETED) {
            throw new BusinessException(
                    "Cannot fail a funded or completed listing", "INVALID_LISTING_STATE");
        }
        this.status = ListingStatus.FUNDING_FAILED;
        this.updatedAt = LocalDateTime.now();
    }

    public void activate() {
        this.status = ListingStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    public void complete() {
        this.status = ListingStatus.COMPLETED;
        this.updatedAt = LocalDateTime.now();
    }

    // FIX: mark satellite verified — call this when farm.satellite.verified event arrives
    public void markSatelliteVerified() {
        this.satelliteVerified = true;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isFullyFunded() {
        return this.status == ListingStatus.FULLY_FUNDED
                || this.fundedAmountEtb.compareTo(this.totalAmountEtb) >= 0;
    }
}