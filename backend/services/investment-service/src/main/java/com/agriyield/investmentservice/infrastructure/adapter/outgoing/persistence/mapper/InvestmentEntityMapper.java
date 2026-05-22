package com.agriyield.investmentservice.infrastructure.adapter.outgoing.persistence.mapper;

import com.agriyield.investmentservice.domain.enums.InvestmentStatus;
import com.agriyield.investmentservice.domain.model.Investment;
import com.agriyield.investmentservice.infrastructure.adapter.outgoing.persistence.entity.InvestmentEntity;
import org.springframework.stereotype.Component;

@Component
public class InvestmentEntityMapper {

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
}
