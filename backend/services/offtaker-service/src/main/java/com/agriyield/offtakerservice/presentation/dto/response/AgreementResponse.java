package com.agriyield.offtakerservice.presentation.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class AgreementResponse {
    private UUID id;
    private UUID bidId;
    private String contractHash;
    private String contractPdfUrl;
    private OffsetDateTime farmerSignedAt;
    private OffsetDateTime offtakerSignedAt;
    private boolean fullyExecuted;
    private OffsetDateTime createdAt;
}
