package com.agriyield.offtakerservice.presentation.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Combined farm detail payload for the off-taker detail view (UC-OFF-02).
 * Bundles the marketplace listing data with NDVI history, bid history,
 * and farmer identity context in a single response, so the frontend
 * doesn't need 4 separate round-trips.
 */
@Data
@Builder
public class FarmDetailResponse {
    private FarmMarketplaceResponse farm;
    private List<Map<String, Object>> ndviHistory;
    private List<BidResponse> bids;
    private FarmerIdentity farmer;

    @Data
    @Builder
    public static class FarmerIdentity {
        private String farmerId;
        private String phone;
        private String faydaId;
        private String kycStatus;
        private int agriScore;
        private int totalSeasonsCompleted;
    }
}
