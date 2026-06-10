package com.agriyield.offtakerservice.presentation.controller;

import com.agriyield.offtakerservice.application.port.incoming.BidServicePort;
import com.agriyield.offtakerservice.domain.model.Bid;
import com.agriyield.offtakerservice.infrastructure.config.JwtUtils;
import com.agriyield.offtakerservice.presentation.dto.request.PlaceBidRequest;
import com.agriyield.offtakerservice.presentation.dto.response.ApiResponse;
import com.agriyield.offtakerservice.presentation.dto.response.BidResponse;
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
public class BidController {

    private final BidServicePort bidService;
    private final JwtUtils jwtUtils;

    // UC-OFF-03: Place Purchase Bid  — OFF_TAKER
    @PostMapping("/api/v1/offtaker/bids")
    public ResponseEntity<ApiResponse<BidResponse>> placeBid(
            @Valid @RequestBody PlaceBidRequest req,
            HttpServletRequest request) {

        UUID offtakerId = jwtUtils.extractUserId(request);
        Bid bid = bidService.placeBid(
                offtakerId, req.getFarmId(),
                req.getQuantityQuintals(), req.getPricePerQuintalEtb(),
                req.getExpiresInDays());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Bid placed successfully", toResponse(bid)));
    }

    // UC-OFF-04: View My Bids  — OFF_TAKER
    @GetMapping("/api/v1/offtaker/bids")
    public ResponseEntity<ApiResponse<List<BidResponse>>> getMyBids(HttpServletRequest request) {
        UUID offtakerId = jwtUtils.extractUserId(request);
        List<BidResponse> bids = bidService.getMyBids(offtakerId)
                .stream().map(this::toResponse).toList();
        return ResponseEntity.ok(ApiResponse.success(bids));
    }

    @GetMapping("/api/v1/offtaker/bids/{bidId}")
    public ResponseEntity<ApiResponse<BidResponse>> getBidById(
            @PathVariable UUID bidId,
            HttpServletRequest request) {
        jwtUtils.extractUserId(request);
        return ResponseEntity.ok(ApiResponse.success(toResponse(bidService.getById(bidId))));
    }

    // UC-OFF-05: Accept Bid  — FARMER
    @PostMapping("/api/v1/farmer/bids/{bidId}/accept")
    public ResponseEntity<ApiResponse<BidResponse>> acceptBid(
            @PathVariable UUID bidId,
            HttpServletRequest request) {
        UUID farmerId = jwtUtils.extractUserId(request);
        Bid bid = bidService.acceptBid(bidId, farmerId);
        return ResponseEntity.ok(ApiResponse.success("Bid accepted", toResponse(bid)));
    }

    // Reject Bid  — FARMER
    @PostMapping("/api/v1/farmer/bids/{bidId}/reject")
    public ResponseEntity<ApiResponse<BidResponse>> rejectBid(
            @PathVariable UUID bidId,
            HttpServletRequest request) {
        UUID farmerId = jwtUtils.extractUserId(request);
        Bid bid = bidService.rejectBid(bidId, farmerId);
        return ResponseEntity.ok(ApiResponse.success("Bid rejected", toResponse(bid)));
    }

    // Get bids on a farmer's farm  — FARMER
    @GetMapping("/api/v1/farmer/bids")
    public ResponseEntity<ApiResponse<List<BidResponse>>> getBidsForMyFarm(
            @RequestParam UUID farmId,
            HttpServletRequest request) {
        jwtUtils.extractUserId(request);
        List<BidResponse> bids = bidService.getBidsForFarm(farmId)
                .stream().map(this::toResponse).toList();
        return ResponseEntity.ok(ApiResponse.success(bids));
    }

    private BidResponse toResponse(Bid b) {
        return BidResponse.builder()
                .id(b.getId())
                .offtakerId(b.getOfftakerId())
                .farmId(b.getFarmId())
                .quantityQuintals(b.getQuantityQuintals())
                .pricePerQuintalEtb(b.getPricePerQuintalEtb())
                .totalValueEtb(b.getTotalValueEtb())
                .bidDepositEtb(b.getBidDepositEtb())
                .status(b.getStatus().name())
                .expiresAt(b.getExpiresAt())
                .acceptedAt(b.getAcceptedAt())
                .createdAt(b.getCreatedAt())
                .build();
    }
}
