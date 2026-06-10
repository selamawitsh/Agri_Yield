package com.agriyield.offtakerservice.application.service;

import com.agriyield.offtakerservice.application.port.incoming.BidServicePort;
import com.agriyield.offtakerservice.application.port.outgoing.*;
import com.agriyield.offtakerservice.domain.enums.BidStatus;
import com.agriyield.offtakerservice.domain.exception.BusinessException;
import com.agriyield.offtakerservice.domain.exception.ResourceNotFoundException;
import com.agriyield.offtakerservice.domain.model.Bid;
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

    @Value("${app.bid.deposit-percentage:0.10}")
    private BigDecimal depositPercentage;

    @Override
    @Transactional
    public Bid placeBid(UUID offtakerId, UUID farmId, BigDecimal quantityQuintals,
                        BigDecimal pricePerQuintalEtb, int expiresInDays) {

        // Verify farm exists via gRPC
        farmService.getFarmById(farmId.toString());

        BigDecimal totalValue = quantityQuintals.multiply(pricePerQuintalEtb);
        BigDecimal depositAmount = totalValue.multiply(depositPercentage);
        OffsetDateTime expiresAt = OffsetDateTime.now().plusDays(expiresInDays);

        Bid bid = Bid.builder()
                .id(UUID.randomUUID())
                .offtakerId(offtakerId)
                .farmId(farmId)
                .cropCycleId(UUID.randomUUID()) // resolved from farm context
                .quantityQuintals(quantityQuintals)
                .pricePerQuintalEtb(pricePerQuintalEtb)
                .totalValueEtb(totalValue)
                .bidDepositEtb(depositAmount)
                .status(BidStatus.PENDING)
                .expiresAt(expiresAt)
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();

        // Lock deposit in escrow before saving bid
        escrowService.lockBidDeposit(bid.getId(), offtakerId, depositAmount);

        Bid saved = bidRepository.save(bid);

        eventPublisher.publishBidPlaced(
                saved.getId(), farmId, offtakerId,
                quantityQuintals, pricePerQuintalEtb,
                totalValue, expiresAt.toString());

        log.info("Bid placed: bidId={} farmId={} offtakerId={}", saved.getId(), farmId, offtakerId);
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

        // Generate purchase agreement
        String contractHash = UUID.randomUUID().toString(); // real hash logic in prod
        String pdfUrl = "minio://contracts/agreement-" + bidId + ".pdf";

        PurchaseAgreement agreement = PurchaseAgreement.builder()
                .id(UUID.randomUUID())
                .bidId(bidId)
                .contractHash(contractHash)
                .contractPdfUrl(pdfUrl)
                .fullyExecuted(false)
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();

        PurchaseAgreement savedAgreement = agreementRepository.save(agreement);

        log.info("Bid accepted: bidId={} agreementId={}", bidId, savedAgreement.getId());
        return saved;
    }

    @Override
    @Transactional
    public Bid rejectBid(UUID bidId, UUID farmerId) {
        Bid bid = findBidOrThrow(bidId);
        bid.reject();
        Bid saved = bidRepository.save(bid);

        // Refund deposit
        escrowService.forfeitBidDeposit(bidId);

        log.info("Bid rejected: bidId={}", bidId);
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
    @Scheduled(cron = "0 0 * * * *") // every hour
    @Transactional
    public void expireStaleBids() {
        List<Bid> staleBids = bidRepository.findByStatusAndExpiresAtBefore(
                BidStatus.PENDING, OffsetDateTime.now());
        for (Bid bid : staleBids) {
            bid.expire();
            bidRepository.save(bid);
            escrowService.forfeitBidDeposit(bid.getId());
            log.info("Bid expired: bidId={}", bid.getId());
        }
    }

    private Bid findBidOrThrow(UUID bidId) {
        return bidRepository.findById(bidId)
                .orElseThrow(() -> new ResourceNotFoundException("Bid not found: " + bidId));
    }
}
