package com.agriyield.offtakerservice.application.service;

import com.agriyield.offtakerservice.application.port.incoming.AgreementServicePort;
import com.agriyield.offtakerservice.application.port.outgoing.AgreementRepositoryPort;
import com.agriyield.offtakerservice.application.port.outgoing.BidRepositoryPort;
import com.agriyield.offtakerservice.application.port.outgoing.EventPublisherPort;
import com.agriyield.offtakerservice.domain.exception.BusinessException;
import com.agriyield.offtakerservice.domain.exception.ResourceNotFoundException;
import com.agriyield.offtakerservice.domain.model.Bid;
import com.agriyield.offtakerservice.domain.model.PurchaseAgreement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AgreementServiceImpl implements AgreementServicePort {

    private final AgreementRepositoryPort agreementRepository;
    private final BidRepositoryPort bidRepository;
    private final EventPublisherPort eventPublisher;

    @Override
    public PurchaseAgreement getById(UUID agreementId) {
        return agreementRepository.findById(agreementId)
                .orElseThrow(() -> new ResourceNotFoundException("Agreement not found: " + agreementId));
    }

    @Override
    @Transactional
    public PurchaseAgreement signAgreement(UUID agreementId, UUID signingUserId, String role) {
        PurchaseAgreement agreement = agreementRepository.findById(agreementId)
                .orElseThrow(() -> new ResourceNotFoundException("Agreement not found: " + agreementId));

        if (agreement.isFullyExecuted()) {
            throw new BusinessException("Agreement is already fully executed", "ALREADY_EXECUTED");
        }

        switch (role.toUpperCase()) {
            case "FARMER"    -> agreement.signAsFarmer();
            case "OFF_TAKER" -> agreement.signAsOfftaker();
            default          -> throw new BusinessException("Invalid signing role: " + role, "INVALID_ROLE");
        }

        PurchaseAgreement saved = agreementRepository.save(agreement);

        if (saved.isFullyExecuted()) {
            Bid bid = bidRepository.findByBidId(agreement.getBidId());
            bid.markContractSigned();
            bidRepository.save(bid);

            eventPublisher.publishBidAccepted(
                    bid.getId(), bid.getFarmId(),
                    signingUserId, // farmerID approximation — real impl pulls from bid context
                    bid.getOfftakerId(), agreementId);

            log.info("Agreement fully executed: agreementId={} bidId={}", agreementId, bid.getId());
        }

        return saved;
    }
}
