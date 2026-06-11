package com.agriyield.merchantservice.infrastructure.adapter.outgoing.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "products", schema = "merchant_service")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "merchant_id", nullable = false)
    private UUID merchantId;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "product_category", nullable = false)
    private String productCategory;

    @Column(name = "unit", nullable = false)
    private String unit;

    @Column(name = "current_price_etb", nullable = false, precision = 12, scale = 2)
    private BigDecimal currentPriceEtb;

    @Column(name = "is_available", nullable = false)
    private boolean isAvailable;

    /** Stock quantity in the merchant's store. Deducted on voucher redemption. */
    @Column(name = "quantity_in_stock", nullable = false, precision = 10, scale = 2)
    private BigDecimal quantityInStock;

    /** Unit of measure (kg, litre, bag, piece). Same as unit — kept for clarity. */
    @Column(name = "unit_of_measure")
    private String unitOfMeasure;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
