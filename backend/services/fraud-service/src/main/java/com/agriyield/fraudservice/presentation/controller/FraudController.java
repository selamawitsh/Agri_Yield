package com.agriyield.fraudservice.presentation.controller;

import com.agriyield.fraudservice.application.port.incoming.FraudServicePort;
import com.agriyield.fraudservice.domain.model.FraudAlert;
import com.agriyield.fraudservice.domain.model.FraudRiskScore;
import com.agriyield.fraudservice.infrastructure.config.JwtUtils;
import com.agriyield.fraudservice.presentation.dto.request.ResolveAlertRequest;
import com.agriyield.fraudservice.presentation.dto.response.ApiResponse;
import com.agriyield.fraudservice.presentation.dto.response.FraudAlertResponse;
import com.agriyield.fraudservice.presentation.dto.response.FraudRiskScoreResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class FraudController {

    private final FraudServicePort fraudService;
    private final JwtUtils jwtUtils;

    // ── FR-08: Admin — get fraud audit log ────────────────────────────────────
    @GetMapping("/admin/fraud-audit")
    public ResponseEntity<ApiResponse<List<FraudAlertResponse>>> getAuditLog(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(required = false) String severity,
            @RequestParam(defaultValue = "false") boolean unresolvedOnly,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        String role = jwtUtils.extractRole(authHeader);
        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(403)
                .body(ApiResponse.<List<FraudAlertResponse>>builder()
                    .success(false).message("Admin access required").build());
        }

        log.info("GET /admin/fraud-audit severity={} unresolvedOnly={}", severity, unresolvedOnly);
        List<FraudAlertResponse> alerts = fraudService
            .getAlerts(severity, unresolvedOnly, page, size)
            .stream().map(this::toAlertResponse).collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(alerts));
    }

    // ── Get single alert ──────────────────────────────────────────────────────
    @GetMapping("/admin/fraud-audit/{alertId}")
    public ResponseEntity<ApiResponse<FraudAlertResponse>> getAlert(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable UUID alertId) {

        String role = jwtUtils.extractRole(authHeader);
        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(403)
                .body(ApiResponse.<FraudAlertResponse>builder()
                    .success(false).message("Admin access required").build());
        }

        FraudAlert alert = fraudService.getAlertById(alertId);
        return ResponseEntity.ok(ApiResponse.success(toAlertResponse(alert)));
    }

    // ── Resolve an alert ──────────────────────────────────────────────────────
    @PatchMapping("/admin/fraud-audit/{alertId}/resolve")
    public ResponseEntity<ApiResponse<FraudAlertResponse>> resolveAlert(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable UUID alertId,
            @Valid @RequestBody ResolveAlertRequest request) {

        UUID adminId = jwtUtils.extractUserId(authHeader);
        String role  = jwtUtils.extractRole(authHeader);
        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(403)
                .body(ApiResponse.<FraudAlertResponse>builder()
                    .success(false).message("Admin access required").build());
        }

        log.info("PATCH /admin/fraud-audit/{}/resolve by admin={}", alertId, adminId);
        FraudAlert resolved = fraudService.resolveAlert(alertId, adminId, request.getNotes());
        return ResponseEntity.ok(ApiResponse.success(
            "Alert resolved successfully", toAlertResponse(resolved)));
    }

    // ── Get alerts by entity ──────────────────────────────────────────────────
    @GetMapping("/admin/fraud-audit/entity/{entityId}")
    public ResponseEntity<ApiResponse<List<FraudAlertResponse>>> getAlertsByEntity(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable UUID entityId,
            @RequestParam String entityType) {

        String role = jwtUtils.extractRole(authHeader);
        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(403)
                .body(ApiResponse.<List<FraudAlertResponse>>builder()
                    .success(false).message("Admin access required").build());
        }

        List<FraudAlertResponse> alerts = fraudService
            .getAlertsByEntity(entityId, entityType)
            .stream().map(this::toAlertResponse).collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(alerts));
    }

    // ── Get fraud risk score for an entity ────────────────────────────────────
    @GetMapping("/admin/fraud-score/{entityId}")
    public ResponseEntity<ApiResponse<FraudRiskScoreResponse>> getFraudScore(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable UUID entityId,
            @RequestParam(defaultValue = "FARMER") String entityType) {

        String role = jwtUtils.extractRole(authHeader);
        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(403)
                .body(ApiResponse.<FraudRiskScoreResponse>builder()
                    .success(false).message("Admin access required").build());
        }

        FraudRiskScore score = fraudService.calculateFraudRiskScore(entityId, entityType);
        return ResponseEntity.ok(ApiResponse.success(toScoreResponse(score)));
    }

    // ── Mappers ───────────────────────────────────────────────────────────────

    private FraudAlertResponse toAlertResponse(FraudAlert a) {
        return FraudAlertResponse.builder()
            .id(a.getId())
            .alertType(a.getAlertType().getValue())
            .entityType(a.getEntityType().getValue())
            .entityId(a.getEntityId())
            .severity(a.getSeverity().getValue())
            .description(a.getDescription())
            .evidence(a.getEvidence())
            .resolved(a.isResolved())
            .resolvedByAdminId(a.getResolvedByAdminId())
            .resolutionNotes(a.getResolutionNotes())
            .resolvedAt(a.getResolvedAt())
            .createdAt(a.getCreatedAt())
            .build();
    }

    private FraudRiskScoreResponse toScoreResponse(FraudRiskScore s) {
        return FraudRiskScoreResponse.builder()
            .entityId(s.getEntityId())
            .entityType(s.getEntityType().getValue())
            .gpsAnomalyScore(s.getGpsAnomalyScore())
            .duplicateVoucherScore(s.getDuplicateVoucherScore())
            .exifMismatchScore(s.getExifMismatchScore())
            .suspiciousActivityScore(s.getSuspiciousActivityScore())
            .totalScore(s.getTotalScore())
            .severity(s.getSeverity().getValue())
            .calculatedAt(s.getCalculatedAt())
            .build();
    }
}
