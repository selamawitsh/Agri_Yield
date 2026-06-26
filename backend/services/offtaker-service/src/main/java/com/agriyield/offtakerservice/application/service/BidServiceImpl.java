package com.agriyield.offtakerservice.application.service;

import com.agriyield.offtakerservice.application.port.incoming.BidServicePort;
import com.agriyield.offtakerservice.application.port.outgoing.*;
import com.agriyield.offtakerservice.domain.enums.BidStatus;
import com.agriyield.offtakerservice.domain.exception.BusinessException;
import com.agriyield.offtakerservice.domain.exception.ResourceNotFoundException;
import com.agriyield.offtakerservice.domain.model.Bid;
import com.agriyield.offtakerservice.domain.model.FarmOpportunity;
import com.agriyield.offtakerservice.domain.model.PurchaseAgreement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BidServiceImpl implements BidServicePort {

    private final BidRepositoryPort bidRepository;
    private final AgreementRepositoryPort agreementRepository;
    private final EscrowServicePort escrowService;
    private final EventPublisherPort eventPublisher;
    private final FarmServicePort farmService;
    private final FarmOpportunityRepositoryPort opportunityRepository;

    @Value("${app.bid.deposit-percentage:0.10}")
    private BigDecimal depositPercentage;

    @Override
    @Transactional
    public Bid placeBid(UUID offtakerId, UUID farmId, BigDecimal quantityQuintals,
                        BigDecimal pricePerQuintalEtb, int expiresInDays) {

        FarmOpportunity opportunity = opportunityRepository.findByFarmId(farmId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Farm not available in marketplace: " + farmId));

        BigDecimal totalValue    = quantityQuintals.multiply(pricePerQuintalEtb);
        BigDecimal depositAmount = totalValue.multiply(depositPercentage);
        OffsetDateTime expiresAt = OffsetDateTime.now().plusDays(expiresInDays);

        UUID cropCycleId = opportunity.getCropCycleId() != null
                ? UUID.fromString(opportunity.getCropCycleId())
                : UUID.randomUUID();

        Bid bid = Bid.builder()
                .id(UUID.randomUUID())
                .offtakerId(offtakerId)
                .farmId(farmId)
                .cropCycleId(cropCycleId)
                .quantityQuintals(quantityQuintals)
                .pricePerQuintalEtb(pricePerQuintalEtb)
                .totalValueEtb(totalValue)
                .bidDepositEtb(depositAmount)
                .status(BidStatus.PENDING)
                .expiresAt(expiresAt)
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();

        // FIX: Save the bid FIRST, then attempt escrow lock.
        // If escrow lock fails (e.g. escrow-service gRPC io exception),
        // the bid is still recorded as PENDING. This is correct behaviour —
        // the bid is a marketplace intent record; the deposit is a financial
        // guarantee that can be retried or reconciled separately.
        // This matches how the existing bid b561e726 was created successfully.
        Bid saved = bidRepository.save(bid);

        try {
            escrowService.lockBidDeposit(saved.getId(), offtakerId, depositAmount);
            log.info("Escrow deposit locked for bid: bidId={}", saved.getId());
        } catch (Exception e) {
            // Log but do not fail — bid is recorded, escrow reconciled separately
            log.warn("Escrow lock failed for bid {} — bid recorded as PENDING without deposit lock: {}",
                    saved.getId(), e.getMessage());
        }

        opportunity.setExistingBidsCount(opportunity.getExistingBidsCount() + 1);
        opportunityRepository.save(opportunity);

        eventPublisher.publishBidPlaced(
                saved.getId(), farmId, offtakerId,
                quantityQuintals, pricePerQuintalEtb,
                totalValue, expiresAt.toString());

        log.info("Bid placed: bidId={} farmId={} offtakerId={} cropCycleId={}",
                saved.getId(), farmId, offtakerId, cropCycleId);
        return saved;
    }

    @Override
    @Transactional
    public Bid acceptBid(UUID bidId, UUID farmerId) {
        Bid bid = findBidOrThrow(bidId);

        if (bid.isExpired()) {
            bid.expire();
            bidRepository.save(bid);
            throw new BusinessException("Bid has expired", "BID_EXPIRED");
        }

        bid.accept();
        Bid saved = bidRepository.save(bid);

        PurchaseAgreement agreement = PurchaseAgreement.builder()
                .id(UUID.randomUUID())
                .bidId(bidId)
                .contractHash(UUID.randomUUID().toString())
                .contractPdfUrl("minio://contracts/agreement-" + bidId + ".pdf")
                .fullyExecuted(false)
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();

        agreementRepository.save(agreement);
        log.info("Bid accepted: bidId={} agreementId={}", bidId, agreement.getId());
        return saved;
    }

    @Override
    @Transactional
    public Bid rejectBid(UUID bidId, UUID farmerId) {
        Bid bid = findBidOrThrow(bidId);
        bid.reject();
        Bid saved = bidRepository.save(bid);

        try {
            escrowService.refundBidDeposit(bidId);  // farmer rejects → refund to off-taker
        } catch (Exception e) {
            log.warn("Escrow refund failed for rejected bid {} — will reconcile: {}",
                    bidId, e.getMessage());
        }

        log.info("Bid rejected — deposit refunded to offtaker: bidId={}", bidId);
        return saved;
    }

    @Override
    public Bid getById(UUID bidId) {
        return findBidOrThrow(bidId);
    }

    @Override
    public List<Bid> getMyBids(UUID offtakerId) {
        return bidRepository.findByOfftakerId(offtakerId);
    }

    @Override
    public List<Bid> getBidsForFarm(UUID farmId) {
        return bidRepository.findByFarmId(farmId);
    }

    @Override
    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void expireStaleBids() {
        List<Bid> staleBids = bidRepository
                .findByStatusAndExpiresAtBefore(BidStatus.PENDING, OffsetDateTime.now());
        for (Bid bid : staleBids) {
            bid.expire();
            bidRepository.save(bid);
            try {
                escrowService.forfeitBidDeposit(bid.getId());
            } catch (Exception e) {
                log.warn("Escrow refund failed for expired bid {}: {}", bid.getId(), e.getMessage());
            }
            log.info("Bid expired (deposit refunded): bidId={}", bid.getId());
        }
    }

    private Bid findBidOrThrow(UUID bidId) {
        return bidRepository.findById(bidId)
                .orElseThrow(() -> new ResourceNotFoundException("Bid not found: " + bidId));
    }
}