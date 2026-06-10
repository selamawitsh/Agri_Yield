package com.agriyield.offtakerservice.domain.model;

import com.agriyield.offtakerservice.domain.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseAgreement {
    private UUID id;
    private UUID bidId;
    private String contractHash;
    private String contractPdfUrl;
    private OffsetDateTime farmerSignedAt;
    private OffsetDateTime offtakerSignedAt;
    private boolean fullyExecuted;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public void signAsFarmer() {
        if (this.farmerSignedAt != null) {
            throw new BusinessException("Farmer has already signed", "ALREADY_SIGNED");
        }
        this.farmerSignedAt = OffsetDateTime.now();
        checkFullyExecuted();
        this.updatedAt = OffsetDateTime.now();
    }

    public void signAsOfftaker() {
        if (this.offtakerSignedAt != null) {
            throw new BusinessException("Off-taker has already signed", "ALREADY_SIGNED");
        }
        this.offtakerSignedAt = OffsetDateTime.now();
        checkFullyExecuted();
        this.updatedAt = OffsetDateTime.now();
    }

    private void checkFullyExecuted() {
        if (this.farmerSignedAt != null && this.offtakerSignedAt != null) {
            this.fullyExecuted = true;
        }
    }
}
