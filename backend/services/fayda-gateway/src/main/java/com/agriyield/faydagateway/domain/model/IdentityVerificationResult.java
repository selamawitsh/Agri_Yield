package com.agriyield.faydagateway.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdentityVerificationResult {
    private boolean verified;
    private String message;
    private String faydaId;
}
