package com.agriyield.offtakerservice.infrastructure.adapter.outgoing.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "farm_opportunities")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FarmOpportunityEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "farm_id", nullable = false, unique = true)
    private UUID farmId;

    @Column(name = "farmer_id")
    private String farmerId;

    @Column(name = "crop_type", length = 30)
    private String cropType;

    @Column(name = "area_hectares", precision = 8, scale = 4)
    private BigDecimal areaHectares;

    @Column(name = "region", length = 50)
    private String region;

    @Column(name = "kebele_code", length = 20)
    private String kebeleCode;

    @Column(name = "gps_centroid_lat", precision = 10, scale = 7)
    private BigDecimal gpsCentroidLat;

    @Column(name = "gps_centroid_lng", precision = 10, scale = 7)
    private BigDecimal gpsCentroidLng;

    @Column(name = "agri_score", nullable = false)
    private int agriScore;

    @Column(name = "crop_cycle_id")
    private String cropCycleId;

    @Column(name = "crop_cycle_status", length = 30)
    private String cropCycleStatus;

    @Column(name = "current_ndvi", precision = 5, scale = 4)
    private BigDecimal currentNdvi;

    @Column(name = "ndvi_health_status", length = 20)
    private String ndviHealthStatus;

    @Column(name = "predicted_yield_min_quintals", precision = 10, scale = 2)
    private BigDecimal predictedYieldMinQuintals;

    @Column(name = "predicted_yield_max_quintals", precision = 10, scale = 2)
    private BigDecimal predictedYieldMaxQuintals;

    @Column(name = "predicted_yield_mean_quintals", precision = 10, scale = 2)
    private BigDecimal predictedYieldMeanQuintals;

    @Column(name = "yield_confidence_pct")
    private Integer yieldConfidencePct;

    @Column(name = "harvest_ready", nullable = false)
    private boolean harvestReady;

    @Column(name = "estimated_harvest_date_from", length = 30)
    private String estimatedHarvestDateFrom;

    @Column(name = "estimated_harvest_date_to", length = 30)
    private String estimatedHarvestDateTo;

    @Column(name = "existing_bids_count", nullable = false)
    private int existingBidsCount;

    @UpdateTimestamp
    @Column(name = "last_updated", nullable = false)
    private OffsetDateTime lastUpdated;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;
}
