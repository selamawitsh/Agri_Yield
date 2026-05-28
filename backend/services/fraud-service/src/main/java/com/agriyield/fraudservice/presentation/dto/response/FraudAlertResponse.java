package com.agriyield.fraudservice.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FraudAlertResponse {

    private UUID id;
    private String alertType;
    private String entityType;
    private UUID entityId;
    private String severity;
    private String description;
    private String evidence;
    private boolean resolved;
    private UUID resolvedByAdminId;
    private String resolutionNotes;
    private LocalDateTime resolvedAt;
    private LocalDateTime createdAt;
}
