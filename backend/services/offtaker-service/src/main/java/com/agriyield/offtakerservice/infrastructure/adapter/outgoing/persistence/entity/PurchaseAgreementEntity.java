package com.agriyield.offtakerservice.infrastructure.adapter.outgoing.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "purchase_agreements")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseAgreementEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "bid_id", nullable = false, unique = true)
    private UUID bidId;

    @Column(name = "contract_hash", nullable = false, length = 255)
    private String contractHash;

    @Column(name = "contract_pdf_url", nullable = false, length = 500)
    private String contractPdfUrl;

    @Column(name = "farmer_signed_at")
    private OffsetDateTime farmerSignedAt;

    @Column(name = "offtaker_signed_at")
    private OffsetDateTime offtakerSignedAt;

    @Column(name = "is_fully_executed", nullable = false)
    private boolean fullyExecuted;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
