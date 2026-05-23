package com.agriyield.investmentservice.infrastructure.adapter.outgoing.persistence;

import com.agriyield.investmentservice.application.port.outgoing.PayoutRecordRepositoryPort;
import com.agriyield.investmentservice.domain.model.PayoutRecord;
import com.agriyield.investmentservice.infrastructure.adapter.outgoing.persistence.mapper.InvestmentEntityMapper;
import com.agriyield.investmentservice.infrastructure.repository.JpaPayoutRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PayoutRecordRepositoryAdapter implements PayoutRecordRepositoryPort {

    private final JpaPayoutRecordRepository jpaRepository;
    private final InvestmentEntityMapper mapper;

    @Override
    public PayoutRecord save(PayoutRecord payoutRecord) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(payoutRecord)));
    }

    @Override
    public List<PayoutRecord> findByInvestorId(UUID investorId) {
        return jpaRepository.findByInvestorIdOrderByPaidAtDesc(investorId)
            .stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<PayoutRecord> findByInvestmentId(UUID investmentId) {
        return jpaRepository.findByInvestmentId(investmentId)
            .stream().map(mapper::toDomain).collect(Collectors.toList());
    }
}
