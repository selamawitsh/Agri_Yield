package com.agriyield.farmservice.infrastructure.adapter.outgoing.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "input_need_items")
public class InputNeedItemEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "input_need_id", nullable = false)
    private UUID inputNeedId;

    @Column(name = "product_category", nullable = false, length = 30)
    private String productCategory;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "quantity", nullable = false, precision = 10, scale = 2)
    private BigDecimal quantity;

    @Column(name = "unit", nullable = false, length = 20)
    private String unit;

    @Column(name = "estimated_price_etb", nullable = false, precision = 12, scale = 2)
    private BigDecimal estimatedPriceEtb;

    // SRS Page 20 — enforces agronomic order
    @Column(name = "sequence_order", nullable = false)
    private Integer sequenceOrder;
}
