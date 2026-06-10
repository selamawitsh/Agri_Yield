package com.agriyield.offtakerservice.application.service;

import com.agriyield.offtakerservice.application.port.incoming.DispatchServicePort;
import com.agriyield.offtakerservice.application.port.outgoing.*;
import com.agriyield.offtakerservice.domain.enums.DispatchStatus;
import com.agriyield.offtakerservice.domain.exception.BusinessException;
import com.agriyield.offtakerservice.domain.exception.ResourceNotFoundException;
import com.agriyield.offtakerservice.domain.model.PurchaseAgreement;
import com.agriyield.offtakerservice.domain.model.TruckDispatch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DispatchServiceImpl implements DispatchServicePort {

    private final DispatchRepositoryPort dispatchRepository;
    private final AgreementRepositoryPort agreementRepository;
    private final BidRepositoryPort bidRepository;
    private final EscrowServicePort escrowService;
    private final EventPublisherPort eventPublisher;

    @Override
    @Transactional
    public TruckDispatch scheduleDispatch(UUID offtakerId, UUID agreementId,
                                          String driverFaydaId, int truckCount,
                                          LocalDate scheduledPickupDate) {

        PurchaseAgreement agreement = agreementRepository.findById(agreementId)
                .orElseThrow(() -> new ResourceNotFoundException("Agreement not found: " + agreementId));

        if (!agreement.isFullyExecuted()) {
            throw new BusinessException("Agreement must be fully signed before dispatching trucks", "AGREEMENT_NOT_EXECUTED");
        }

        TruckDispatch dispatch = TruckDispatch.builder()
                .id(UUID.randomUUID())
                .agreementId(agreementId)
                .driverFaydaId(driverFaydaId)
                .truckCount(truckCount)
                .scheduledPickupDate(scheduledPickupDate)
                .driverPenaltyEscrowEtb(BigDecimal.valueOf(500.00))
                .status(DispatchStatus.SCHEDULED)
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();

        TruckDispatch saved = dispatchRepository.save(dispatch);
        log.info("Dispatch scheduled: dispatchId={} agreementId={}", saved.getId(), agreementId);
        return saved;
    }

    @Override
    @Transactional
    public TruckDispatch confirmArrival(UUID dispatchId, UUID farmerId) {
        TruckDispatch dispatch = findDispatchOrThrow(dispatchId);
        dispatch.confirmArrival(LocalDate.now());
        TruckDispatch saved = dispatchRepository.save(dispatch);
        log.info("Dispatch arrival confirmed: dispatchId={}", dispatchId);
        return saved;
    }

    @Override
    @Transactional
    public TruckDispatch confirmLoading(UUID dispatchId, UUID farmerId) {
        TruckDispatch dispatch = findDispatchOrThrow(dispatchId);
        dispatch.confirmLoading();
        TruckDispatch saved = dispatchRepository.save(dispatch);
        log.info("Dispatch loading confirmed: dispatchId={}", dispatchId);
        return saved;
    }

    @Override
    @Transactional
    public TruckDispatch confirmDelivery(UUID agreementId, UUID offtakerId,
                                         BigDecimal actualQuantityQuintals, String qualityGrade) {

        List<TruckDispatch> dispatches = dispatchRepository.findByAgreementId(agreementId);
        TruckDispatch activeDispatch = dispatches.stream()
                .filter(d -> d.getStatus() == DispatchStatus.LOADED)
                .findFirst()
                .orElseThrow(() -> new BusinessException("No LOADED dispatch found for agreement", "NO_LOADED_DISPATCH"));

        activeDispatch.confirmDelivery();
        dispatchRepository.save(activeDispatch);

        // Calculate payment and trigger settlement
        PurchaseAgreement agreement = agreementRepository.findById(agreementId)
                .orElseThrow(() -> new ResourceNotFoundException("Agreement not found: " + agreementId));

        var bid = bidRepository.findById(agreement.getBidId())
                .orElseThrow(() -> new ResourceNotFoundException("Bid not found"));

        BigDecimal totalPayment = actualQuantityQuintals.multiply(bid.getPricePerQuintalEtb());

        escrowService.processHarvestPayment(bid.getFarmId(), agreementId, totalPayment);

        eventPublisher.publishHarvestConfirmed(
                bid.getFarmId(), agreementId,
                actualQuantityQuintals, qualityGrade, totalPayment);

        bid.complete();
        bidRepository.save(bid);

        log.info("Delivery confirmed: agreementId={} totalPayment={}", agreementId, totalPayment);
        return activeDispatch;
    }

    @Override
    public List<TruckDispatch> getDispatchesForAgreement(UUID agreementId) {
        return dispatchRepository.findByAgreementId(agreementId);
    }

    private TruckDispatch findDispatchOrThrow(UUID dispatchId) {
        return dispatchRepository.findById(dispatchId)
                .orElseThrow(() -> new ResourceNotFoundException("Dispatch not found: " + dispatchId));
    }
}
