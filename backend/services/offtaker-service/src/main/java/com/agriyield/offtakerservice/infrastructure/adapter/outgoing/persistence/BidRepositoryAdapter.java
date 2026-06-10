package com.agriyield.offtakerservice.infrastructure.adapter.outgoing.persistence;

import com.agriyield.offtakerservice.application.port.outgoing.BidRepositoryPort;
import com.agriyield.offtakerservice.domain.enums.BidStatus;
import com.agriyield.offtakerservice.domain.model.Bid;
import com.agriyield.offtakerservice.infrastructure.adapter.outgoing.persistence.mapper.OfftakerEntityMapper;
import com.agriyield.offtakerservice.infrastructure.repository.JpaBidRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BidRepositoryAdapter implements BidRepositoryPort {

    private final JpaBidRepository jpaRepository;
    private final OfftakerEntityMapper mapper;

    @Override
    public Bid save(Bid bid) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(bid)));
    }

    @Override
    public Optional<Bid> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    // Convenience method used by AgreementServiceImpl
    public Bid findByBidId(UUID bidId) {
        return jpaRepository.findById(bidId)
                .map(mapper::toDomain)
                .orElseThrow();
    }

    @Override
    public List<Bid> findByOfftakerId(UUID offtakerId) {
        return jpaRepository.findByOfftakerId(offtakerId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Bid> findByFarmId(UUID farmId) {
        return jpaRepository.findByFarmId(farmId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Bid> findByStatusAndExpiresAtBefore(BidStatus status, OffsetDateTime dateTime) {
        return jpaRepository.findByStatusAndExpiresAtBefore(status, dateTime)
                .stream().map(mapper::toDomain).toList();
    }
}
