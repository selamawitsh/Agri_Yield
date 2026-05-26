package com.agriyield.voucherservice.infrastructure.adapter.outgoing.persistence;

import com.agriyield.voucherservice.application.port.outgoing.VoucherRepositoryPort;
import com.agriyield.voucherservice.domain.model.Voucher;
import com.agriyield.voucherservice.infrastructure.adapter.outgoing.persistence.mapper.VoucherEntityMapper;
import com.agriyield.voucherservice.infrastructure.repository.JpaVoucherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class VoucherRepositoryAdapter implements VoucherRepositoryPort {

    private final JpaVoucherRepository jpaRepository;
    private final VoucherEntityMapper mapper;

    @Override
    public Voucher save(Voucher voucher) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(voucher)));
    }

    @Override
    public Optional<Voucher> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Voucher> findByVoucherCode(String voucherCode) {
        return jpaRepository.findByVoucherCode(voucherCode).map(mapper::toDomain);
    }

    @Override
    public List<Voucher> findByFarmerId(UUID farmerId) {
        return jpaRepository.findByFarmerIdOrderByCreatedAtDesc(farmerId)
            .stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Voucher> findByFarmId(UUID farmId) {
        return jpaRepository.findByFarmId(farmId)
            .stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Voucher> findByInvestmentId(UUID investmentId) {
        return jpaRepository.findByInvestmentId(investmentId)
            .stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Voucher> findExpiredActiveVouchers() {
        return jpaRepository.findExpiredActiveVouchers(LocalDateTime.now())
            .stream().map(mapper::toDomain).collect(Collectors.toList());
    }
}
