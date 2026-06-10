package com.agriyield.offtakerservice.presentation.controller;

import com.agriyield.offtakerservice.application.port.incoming.DispatchServicePort;
import com.agriyield.offtakerservice.domain.model.TruckDispatch;
import com.agriyield.offtakerservice.infrastructure.config.JwtUtils;
import com.agriyield.offtakerservice.presentation.dto.request.ConfirmDeliveryRequest;
import com.agriyield.offtakerservice.presentation.dto.request.ScheduleDispatchRequest;
import com.agriyield.offtakerservice.presentation.dto.response.ApiResponse;
import com.agriyield.offtakerservice.presentation.dto.response.DispatchResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class DispatchController {

    private final DispatchServicePort dispatchService;
    private final JwtUtils jwtUtils;

    // UC-OFF-07: Dispatch Harvest Collection Trucks  — OFF_TAKER
    @PostMapping("/api/v1/offtaker/dispatches")
    public ResponseEntity<ApiResponse<DispatchResponse>> scheduleDispatch(
            @Valid @RequestBody ScheduleDispatchRequest req,
            HttpServletRequest request) {
        UUID offtakerId = jwtUtils.extractUserId(request);
        TruckDispatch dispatch = dispatchService.scheduleDispatch(
                offtakerId, req.getAgreementId(),
                req.getDriverFaydaId(), req.getTruckCount(),
                req.getScheduledPickupDate());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Dispatch scheduled", toResponse(dispatch)));
    }

    // UC-OFF-08: Confirm Truck Arrival  — FARMER
    @PostMapping("/api/v1/farmer/dispatches/{dispatchId}/confirm-arrival")
    public ResponseEntity<ApiResponse<DispatchResponse>> confirmArrival(
            @PathVariable UUID dispatchId,
            HttpServletRequest request) {
        UUID farmerId = jwtUtils.extractUserId(request);
        TruckDispatch dispatch = dispatchService.confirmArrival(dispatchId, farmerId);
        return ResponseEntity.ok(ApiResponse.success("Arrival confirmed", toResponse(dispatch)));
    }

    // UC-OFF-09: Confirm Harvest Loading  — FARMER
    @PostMapping("/api/v1/farmer/dispatches/{dispatchId}/confirm-loading")
    public ResponseEntity<ApiResponse<DispatchResponse>> confirmLoading(
            @PathVariable UUID dispatchId,
            HttpServletRequest request) {
        UUID farmerId = jwtUtils.extractUserId(request);
        TruckDispatch dispatch = dispatchService.confirmLoading(dispatchId, farmerId);
        return ResponseEntity.ok(ApiResponse.success("Loading confirmed", toResponse(dispatch)));
    }

    // UC-OFF-10: Confirm Harvest Delivery  — OFF_TAKER
    @PostMapping("/api/v1/offtaker/deliveries/{agreementId}/confirm")
    public ResponseEntity<ApiResponse<DispatchResponse>> confirmDelivery(
            @PathVariable UUID agreementId,
            @Valid @RequestBody ConfirmDeliveryRequest req,
            HttpServletRequest request) {
        UUID offtakerId = jwtUtils.extractUserId(request);
        TruckDispatch dispatch = dispatchService.confirmDelivery(
                agreementId, offtakerId,
                req.getActualQuantityQuintals(), req.getQualityGrade());
        return ResponseEntity.ok(ApiResponse.success("Delivery confirmed — settlement initiated", toResponse(dispatch)));
    }

    @GetMapping("/api/v1/offtaker/dispatches/{agreementId}")
    public ResponseEntity<ApiResponse<List<DispatchResponse>>> getDispatches(
            @PathVariable UUID agreementId,
            HttpServletRequest request) {
        jwtUtils.extractUserId(request);
        List<DispatchResponse> dispatches = dispatchService.getDispatchesForAgreement(agreementId)
                .stream().map(this::toResponse).toList();
        return ResponseEntity.ok(ApiResponse.success(dispatches));
    }

    private DispatchResponse toResponse(TruckDispatch d) {
        return DispatchResponse.builder()
                .id(d.getId())
                .agreementId(d.getAgreementId())
                .driverFaydaId(d.getDriverFaydaId())
                .truckCount(d.getTruckCount())
                .scheduledPickupDate(d.getScheduledPickupDate())
                .actualPickupDate(d.getActualPickupDate())
                .driverPenaltyEscrowEtb(d.getDriverPenaltyEscrowEtb())
                .status(d.getStatus().name())
                .createdAt(d.getCreatedAt())
                .build();
    }
}
