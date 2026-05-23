package com.agriyield.investmentservice.infrastructure.adapter.outgoing.persistence.mapper;

import com.agriyield.investmentservice.domain.enums.InvestmentStatus;
import com.agriyield.investmentservice.domain.enums.ListingStatus;
import com.agriyield.investmentservice.domain.model.FarmListing;
import com.agriyield.investmentservice.domain.model.Investment;
import com.agriyield.investmentservice.domain.model.PayoutRecord;
import com.agriyield.investmentservice.infrastructure.adapter.outgoing.persistence.entity.FarmListingEntity;
import com.agriyield.investmentservice.infrastructure.adapter.outgoing.persistence.entity.InvestmentEntity;
import com.agriyield.investmentservice.infrastructure.adapter.outgoing.persistence.entity.PayoutRecordEntity;
import org.springframework.stereotype.Component;

@Component
public class InvestmentEntityMapper {

    // ─── Investment ──────────────────────────────────────────

    public Investment toDomain(InvestmentEntity entity) {
        if (entity == null) return null;
        return Investment.builder()
            .id(entity.getId())
            .investorId(entity.getInvestorId())
            .farmId(entity.getFarmId())
            .farmerId(entity.getFarmerId())
            .inputNeedId(entity.getInputNeedId())
            .cropCycleId(entity.getCropCycleId())
            .amountEtb(entity.getAmountEtb())
            .status(InvestmentStatus.fromValue(entity.getStatus()))
            .cropType(entity.getCropType())
            .region(entity.getRegion())
            .seasonName(entity.getSeasonName())
            .expectedReturnPct(entity.getExpectedReturnPct())
            .actualReturnPct(entity.getActualReturnPct())
            .notes(entity.getNotes())
            .cancelledReason(entity.getCancelledReason())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .build();
    }

    public InvestmentEntity toEntity(Investment domain) {
        if (domain == null) return null;
        return InvestmentEntity.builder()
            .id(domain.getId())
            .investorId(domain.getInvestorId())
            .farmId(domain.getFarmId())
            .farmerId(domain.getFarmerId())
            .inputNeedId(domain.getInputNeedId())
            .cropCycleId(domain.getCropCycleId())
            .amountEtb(domain.getAmountEtb())
            .status(domain.getStatus().getValue())
            .cropType(domain.getCropType())
            .region(domain.getRegion())
            .seasonName(domain.getSeasonName())
            .expectedReturnPct(domain.getExpectedReturnPct())
            .actualReturnPct(domain.getActualReturnPct())
            .notes(domain.getNotes())
            .cancelledReason(domain.getCancelledReason())
            .createdAt(domain.getCreatedAt())
            .updatedAt(domain.getUpdatedAt())
            .build();
    }

    // ─── FarmListing ─────────────────────────────────────────

    public FarmListing toDomain(FarmListingEntity entity) {
        if (entity == null) return null;
        return FarmListing.builder()
            .id(entity.getId())
            .farmId(entity.getFarmId())
            .farmerId(entity.getFarmerId())
            .inputNeedId(entity.getInputNeedId())
            .cropCycleId(entity.getCropCycleId())
            .cropType(entity.getCropType())
            .region(entity.getRegion())
            .kebeleCode(entity.getKebeleCode())
            .seasonName(entity.getSeasonName())
            .totalAmountEtb(entity.getTotalAmountEtb())
            .fundedAmountEtb(entity.getFundedAmountEtb())
            .fundingPct(entity.getFundingPct())
            .baseApr(entity.getBaseApr())
            .currentApr(entity.getCurrentApr())
            .ndviBonus(entity.getNdviBonus())
            .weatherBonus(entity.getWeatherBonus())
            .ndviPenalty(entity.getNdviPenalty())
            .droughtRisk(entity.getDroughtRisk())
            .agriScore(entity.getAgriScore())
            .status(ListingStatus.fromValue(entity.getStatus()))
            .fundingDeadline(entity.getFundingDeadline())
            .fullyFundedAt(entity.getFullyFundedAt())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .build();
    }

    public FarmListingEntity toEntity(FarmListing domain) {
        if (domain == null) return null;
        return FarmListingEntity.builder()
            .id(domain.getId())
            .farmId(domain.getFarmId())
            .farmerId(domain.getFarmerId())
            .inputNeedId(domain.getInputNeedId())
            .cropCycleId(domain.getCropCycleId())
            .cropType(domain.getCropType())
            .region(domain.getRegion())
            .kebeleCode(domain.getKebeleCode())
            .seasonName(domain.getSeasonName())
            .totalAmountEtb(domain.getTotalAmountEtb())
            .fundedAmountEtb(domain.getFundedAmountEtb())
            .fundingPct(domain.getFundingPct())
            .baseApr(domain.getBaseApr())
            .currentApr(domain.getCurrentApr())
            .ndviBonus(domain.getNdviBonus())
            .weatherBonus(domain.getWeatherBonus())
            .ndviPenalty(domain.getNdviPenalty())
            .droughtRisk(domain.getDroughtRisk())
            .agriScore(domain.getAgriScore())
            .status(domain.getStatus().getValue())
            .fundingDeadline(domain.getFundingDeadline())
            .fullyFundedAt(domain.getFullyFundedAt())
            .createdAt(domain.getCreatedAt())
            .updatedAt(domain.getUpdatedAt())
            .build();
    }

    // ─── PayoutRecord ─────────────────────────────────────────

    public PayoutRecord toDomain(PayoutRecordEntity entity) {
        if (entity == null) return null;
        return PayoutRecord.builder()
            .id(entity.getId())
            .investmentId(entity.getInvestmentId())
            .investorId(entity.getInvestorId())
            .farmId(entity.getFarmId())
            .listingId(entity.getListingId())
            .principalEtb(entity.getPrincipalEtb())
            .returnEtb(entity.getReturnEtb())
            .totalEtb(entity.getTotalEtb())
            .actualApr(entity.getActualApr())
            .payoutReason(entity.getPayoutReason())
            .paidAt(entity.getPaidAt())
            .build();
    }

    public PayoutRecordEntity toEntity(PayoutRecord domain) {
        if (domain == null) return null;
        return PayoutRecordEntity.builder()
            .id(domain.getId())
            .investmentId(domain.getInvestmentId())
            .investorId(domain.getInvestorId())
            .farmId(domain.getFarmId())
            .listingId(domain.getListingId())
            .principalEtb(domain.getPrincipalEtb())
            .returnEtb(domain.getReturnEtb())
            .totalEtb(domain.getTotalEtb())
            .actualApr(domain.getActualApr())
            .payoutReason(domain.getPayoutReason())
            .paidAt(domain.getPaidAt())
            .build();
    }
}
