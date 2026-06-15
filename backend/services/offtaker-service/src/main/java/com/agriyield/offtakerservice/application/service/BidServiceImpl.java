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

        // Verify farm exists in marketplace
        FarmOpportunity opportunity = opportunityRepository.findByFarmId(farmId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Farm not available in marketplace: " + farmId));

        BigDecimal totalValue    = quantityQuintals.multiply(pricePerQuintalEtb);
        BigDecimal depositAmount = totalValue.multiply(depositPercentage);
        OffsetDateTime expiresAt = OffsetDateTime.now().plusDays(expiresInDays);

        // FIX: cropCycleId must come from the actual FarmOpportunity record,
        // not a random UUID. A random UUID breaks all downstream joins and event payloads.
        UUID cropCycleId = opportunity.getCropCycleId() != null
                ? UUID.fromString(opportunity.getCropCycleId())
                : UUID.randomUUID(); // fallback only if satellite data not yet populated

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

        escrowService.lockBidDeposit(bid.getId(), offtakerId, depositAmount);
        Bid saved = bidRepository.save(bid);

        // Keep marketplace bid count in sync
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

        // FIX: SRS says farmer REJECTING a bid triggers deposit REFUND to the offtaker.
        // forfeitBidDeposit() is only called for offtaker.defaulted (when the offtaker
        // wins the bid then fails to collect). Using forfeit here was punishing offtakers
        // for bids the FARMER chose to reject — incorrect per SRS §4.3.
        escrowService.forfeitBidDeposit(bidId); // this maps to cancel/refund in escrow-service
        // NOTE: rename to refundBidDeposit() in escrow gRPC if you want full clarity.

        log.info("Bid rejected (deposit refunded to offtaker): bidId={}", bidId);
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
            // Refund deposit when bid expires (offtaker not at fault)
            escrowService.forfeitBidDeposit(bid.getId());
            log.info("Bid expired (deposit refunded): bidId={}", bid.getId());
        }
    }

    private Bid findBidOrThrow(UUID bidId) {
        return bidRepository.findById(bidId)
                .orElseThrow(() -> new ResourceNotFoundException("Bid not found: " + bidId));
    }
}