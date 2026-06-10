package com.agriyield.offtakerservice.infrastructure.adapter.outgoing.persistence;

import com.agriyield.offtakerservice.application.port.outgoing.DispatchRepositoryPort;
import com.agriyield.offtakerservice.domain.model.TruckDispatch;
import com.agriyield.offtakerservice.infrastructure.adapter.outgoing.persistence.mapper.OfftakerEntityMapper;
import com.agriyield.offtakerservice.infrastructure.repository.JpaTruckDispatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DispatchRepositoryAdapter implements DispatchRepositoryPort {

    private final JpaTruckDispatchRepository jpaRepository;
    private final OfftakerEntityMapper mapper;

    @Override
    public TruckDispatch save(TruckDispatch dispatch) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(dispatch)));
    }

    @Override
    public Optional<TruckDispatch> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<TruckDispatch> findByAgreementId(UUID agreementId) {
        return jpaRepository.findByAgreementId(agreementId).stream().map(mapper::toDomain).toList();
    }
}
