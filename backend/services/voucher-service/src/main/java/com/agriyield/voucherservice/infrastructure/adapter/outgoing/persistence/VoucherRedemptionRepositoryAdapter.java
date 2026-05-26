package com.agriyield.voucherservice.infrastructure.adapter.outgoing.persistence;

import com.agriyield.voucherservice.application.port.outgoing.VoucherRedemptionRepositoryPort;
import com.agriyield.voucherservice.domain.model.VoucherRedemption;
import com.agriyield.voucherservice.infrastructure.adapter.outgoing.persistence.mapper.VoucherEntityMapper;
import com.agriyield.voucherservice.infrastructure.repository.JpaVoucherRedemptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class VoucherRedemptionRepositoryAdapter implements VoucherRedemptionRepositoryPort {

    private final JpaVoucherRedemptionRepository jpaRepository;
    private final VoucherEntityMapper mapper;

    @Override
    public VoucherRedemption save(VoucherRedemption redemption) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(redemption)));
    }

    @Override
    public List<VoucherRedemption> findByVoucherId(UUID voucherId) {
        return jpaRepository.findByVoucherId(voucherId)
            .stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<VoucherRedemption> findByMerchantId(UUID merchantId) {
        return jpaRepository.findByMerchantIdOrderByRedeemedAtDesc(merchantId)
            .stream().map(mapper::toDomain).collect(Collectors.toList());
    }
}
