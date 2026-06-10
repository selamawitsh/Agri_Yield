package com.agriyield.offtakerservice.infrastructure.adapter.outgoing.persistence.mapper;

import com.agriyield.offtakerservice.domain.model.Bid;
import com.agriyield.offtakerservice.domain.model.PurchaseAgreement;
import com.agriyield.offtakerservice.domain.model.TruckDispatch;
import com.agriyield.offtakerservice.infrastructure.adapter.outgoing.persistence.entity.BidEntity;
import com.agriyield.offtakerservice.infrastructure.adapter.outgoing.persistence.entity.PurchaseAgreementEntity;
import com.agriyield.offtakerservice.infrastructure.adapter.outgoing.persistence.entity.TruckDispatchEntity;
import org.springframework.stereotype.Component;

@Component
public class OfftakerEntityMapper {

    public Bid toDomain(BidEntity e) {
        return Bid.builder()
                .id(e.getId())
                .offtakerId(e.getOfftakerId())
                .farmId(e.getFarmId())
                .cropCycleId(e.getCropCycleId())
                .quantityQuintals(e.getQuantityQuintals())
                .pricePerQuintalEtb(e.getPricePerQuintalEtb())
                .totalValueEtb(e.getTotalValueEtb())
                .bidDepositEtb(e.getBidDepositEtb())
                .status(e.getStatus())
                .expiresAt(e.getExpiresAt())
                .acceptedAt(e.getAcceptedAt())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }

    public BidEntity toEntity(Bid b) {
        return BidEntity.builder()
                .id(b.getId())
                .offtakerId(b.getOfftakerId())
                .farmId(b.getFarmId())
                .cropCycleId(b.getCropCycleId())
                .quantityQuintals(b.getQuantityQuintals())
                .pricePerQuintalEtb(b.getPricePerQuintalEtb())
                .status(b.getStatus())
                .expiresAt(b.getExpiresAt())
                .acceptedAt(b.getAcceptedAt())
                .build();
    }

    public PurchaseAgreement toDomain(PurchaseAgreementEntity e) {
        return PurchaseAgreement.builder()
                .id(e.getId())
                .bidId(e.getBidId())
                .contractHash(e.getContractHash())
                .contractPdfUrl(e.getContractPdfUrl())
                .farmerSignedAt(e.getFarmerSignedAt())
                .offtakerSignedAt(e.getOfftakerSignedAt())
                .fullyExecuted(e.isFullyExecuted())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }

    public PurchaseAgreementEntity toEntity(PurchaseAgreement a) {
        return PurchaseAgreementEntity.builder()
                .id(a.getId())
                .bidId(a.getBidId())
                .contractHash(a.getContractHash())
                .contractPdfUrl(a.getContractPdfUrl())
                .farmerSignedAt(a.getFarmerSignedAt())
                .offtakerSignedAt(a.getOfftakerSignedAt())
                .fullyExecuted(a.isFullyExecuted())
                .build();
    }

    public TruckDispatch toDomain(TruckDispatchEntity e) {
        return TruckDispatch.builder()
                .id(e.getId())
                .agreementId(e.getAgreementId())
                .driverFaydaId(e.getDriverFaydaId())
                .truckCount(e.getTruckCount())
                .scheduledPickupDate(e.getScheduledPickupDate())
                .actualPickupDate(e.getActualPickupDate())
                .driverPenaltyEscrowEtb(e.getDriverPenaltyEscrowEtb())
                .status(e.getStatus())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }

    public TruckDispatchEntity toEntity(TruckDispatch d) {
        return TruckDispatchEntity.builder()
                .id(d.getId())
                .agreementId(d.getAgreementId())
                .driverFaydaId(d.getDriverFaydaId())
                .truckCount(d.getTruckCount())
                .scheduledPickupDate(d.getScheduledPickupDate())
                .actualPickupDate(d.getActualPickupDate())
                .driverPenaltyEscrowEtb(d.getDriverPenaltyEscrowEtb())
                .status(d.getStatus())
                .build();
    }
}
