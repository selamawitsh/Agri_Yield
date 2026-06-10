package com.agriyield.offtakerservice.application.port.outgoing;

import com.agriyield.offtakerservice.domain.model.TruckDispatch;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DispatchRepositoryPort {
    TruckDispatch save(TruckDispatch dispatch);
    Optional<TruckDispatch> findById(UUID id);
    List<TruckDispatch> findByAgreementId(UUID agreementId);
}
