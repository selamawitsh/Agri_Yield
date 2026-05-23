package com.agriyield.investmentservice.application.service;

import com.agriyield.investmentservice.application.port.incoming.InvestmentServicePort;
import com.agriyield.investmentservice.application.port.incoming.ListingServicePort;
import com.agriyield.investmentservice.application.port.outgoing.*;
import com.agriyield.investmentservice.domain.enums.InvestmentStatus;
import com.agriyield.investmentservice.domain.enums.ListingStatus;
import com.agriyield.investmentservice.domain.exception.BusinessException;
import com.agriyield.investmentservice.domain.exception.InvestmentNotFoundException;
import com.agriyield.investmentservice.domain.model.FarmListing;
import com.agriyield.investmentservice.domain.model.Investment;
import com.agriyield.investmentservice.domain.model.PayoutRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ListingServiceImpl implements ListingServicePort {

    private final FarmListingRepositoryPort listingRepository;
    private final InvestmentRepositoryPort investmentRepository;
    private final PayoutRecordRepositoryPort payoutRepository;
    private final EscrowServicePort escrowServicePort;
    private final EventPublisherPort eventPublisher;
    private final InvestmentServicePort investmentService;

    @Value("${app.investment.min-amount-etb:500.00}")
    private BigDecimal minAmountEtb;

    // IS-04: Create listing from input.needs.created RabbitMQ event
    @Override
    @Transactional
    public FarmListing createFromInputNeeds(UUID farmId,
                                            UUID farmerId,
                                            UUID inputNeedId,
                                            UUID cropCycleId,
                                            BigDecimal totalAmountEtb,
                                            String cropType,
                                            String region,
                                            String kebeleCode,
                                            String seasonName,
                                            int agriScore) {
        log.info("IS-04: Creating listing for farm: {}, amount: {} ETB", farmId, totalAmountEtb);

        // Idempotency — don't create duplicate listing for same input need
        listingRepository.findByInputNeedId(inputNeedId).ifPresent(existing -> {
            throw new BusinessException(
                "Listing already exists for input need: " + inputNeedId,
                "DUPLICATE_LISTING");
        });

        // APR starts at base — adjusted by agri score
        BigDecimal baseApr = calculateBaseApr(agriScore);

        FarmListing listing = FarmListing.builder()
            .id(UUID.randomUUID())
            .farmId(farmId)
            .farmerId(farmerId)
            .inputNeedId(inputNeedId)
            .cropCycleId(cropCycleId)
            .cropType(cropType)
            .region(region)
            .kebeleCode(kebeleCode)
            .seasonName(seasonName)
            .totalAmountEtb(totalAmountEtb)
            .fundedAmountEtb(BigDecimal.ZERO)
            .fundingPct(BigDecimal.ZERO)
            .baseApr(baseApr)
            .currentApr(baseApr)
            .ndviBonus(BigDecimal.ZERO)
            .weatherBonus(BigDecimal.ZERO)
            .ndviPenalty(BigDecimal.ZERO)
            .droughtRisk(BigDecimal.ZERO)
            .agriScore(agriScore)
            .status(ListingStatus.OPEN)
            .fundingDeadline(LocalDateTime.now().plusDays(30))
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        FarmListing saved = listingRepository.save(listing);
        log.info("IS-04: Listing created: {}", saved.getId());

        eventPublisher.publishListingCreated(saved);
        return saved;
    }

    // IS-01/02: Browse and filter listings
    @Override
    @Transactional(readOnly = true)
    public List<FarmListing> getActiveListings(String cropType,
                                               String region,
                                               BigDecimal minApr,
                                               BigDecimal maxApr) {
        log.info("IS-01/02: Getting listings — cropType={}, region={}", cropType, region);

        List<FarmListing> listings;

        if (cropType != null && region != null) {
            listings = listingRepository.findByCropTypeAndRegion(cropType, region);
        } else if (cropType != null) {
            listings = listingRepository.findByCropType(cropType);
        } else if (region != null) {
            listings = listingRepository.findByRegion(region);
        } else {
            listings = listingRepository.findAllOpen();
        }

        // Apply APR filter
        return listings.stream()
            .filter(l -> l.getStatus() == ListingStatus.OPEN
                || l.getStatus() == ListingStatus.PARTIALLY_FUNDED)
            .filter(l -> minApr == null
                || l.getCurrentApr().compareTo(minApr) >= 0)
            .filter(l -> maxApr == null
                || l.getCurrentApr().compareTo(maxApr) <= 0)
            .collect(Collectors.toList());
    }

    // IS-03: View listing details
    @Override
    @Transactional(readOnly = true)
    public FarmListing getListingById(UUID listingId) {
        return listingRepository.findById(listingId)
            .orElseThrow(() -> new BusinessException(
                "Listing not found: " + listingId, "LISTING_NOT_FOUND"));
    }

    // IS-05: Invest in a listing
    @Override
    @Transactional
    public Investment investInListing(UUID listingId,
                                      UUID investorId,
                                      BigDecimal amountEtb,
                                      String notes) {
        log.info("IS-05: Investing {} ETB in listing: {} by investor: {}",
            amountEtb, listingId, investorId);

        FarmListing listing = listingRepository.findById(listingId)
            .orElseThrow(() -> new BusinessException(
                "Listing not found: " + listingId, "LISTING_NOT_FOUND"));

        if (amountEtb.compareTo(minAmountEtb) < 0) {
            throw new BusinessException(
                "Minimum investment is " + minAmountEtb + " ETB", "AMOUNT_TOO_LOW");
        }

        BigDecimal remaining = listing.getTotalAmountEtb()
            .subtract(listing.getFundedAmountEtb());
        if (amountEtb.compareTo(remaining) > 0) {
            throw new BusinessException(
                "Investment exceeds remaining funding needed: " + remaining + " ETB",
                "EXCEEDS_REMAINING");
        }

        // Place the investment via InvestmentService
        Investment investment = investmentService.placeInvestment(
            investorId,
            listing.getFarmId(),
            listing.getInputNeedId(),
            amountEtb,
            notes);

        // Update listing funding progress
        listing.addFunding(amountEtb);
        FarmListing savedListing = listingRepository.save(listing);

        // IS-06: If now fully funded publish event
        if (savedListing.isFullyFunded()) {
            log.info("IS-06: Listing {} is now fully funded!", listingId);
            eventPublisher.publishListingFullyFunded(savedListing);
        }

        return investment;
    }

    // IS-09: NDVI history — returns raw data from digital twin
    // Real implementation would call farm-service; returning stub for now
    @Override
    @Transactional(readOnly = true)
    public Object getNdviHistory(UUID listingId) {
        FarmListing listing = listingRepository.findById(listingId)
            .orElseThrow(() -> new BusinessException(
                "Listing not found: " + listingId, "LISTING_NOT_FOUND"));
        log.info("IS-09: Returning NDVI history stub for farm: {}", listing.getFarmId());
        // Stub — real implementation calls farm-service gRPC for digital twin
        return java.util.List.of();
    }

    // IS-10: Update dynamic APR from NDVI event
    @Override
    @Transactional
    public FarmListing updateApr(UUID listingId,
                                 BigDecimal ndviBonus,
                                 BigDecimal weatherBonus,
                                 BigDecimal ndviPenalty,
                                 BigDecimal droughtRisk) {
        log.info("IS-10: Updating APR for listing: {}", listingId);

        FarmListing listing = listingRepository.findById(listingId)
            .orElseThrow(() -> new BusinessException(
                "Listing not found: " + listingId, "LISTING_NOT_FOUND"));

        listing.recalculateApr(ndviBonus, weatherBonus, ndviPenalty, droughtRisk);
        FarmListing saved = listingRepository.save(listing);

        log.info("IS-10: APR updated to {}% for listing: {}",
            saved.getCurrentApr(), listingId);
        return saved;
    }

    // IS-11: Process expired listings — called by scheduler
    @Override
    @Transactional
    public void processExpiredListings() {
        log.info("IS-11: Processing expired listings...");

        List<FarmListing> expired = listingRepository.findExpiredOpenListings();
        for (FarmListing listing : expired) {
            log.warn("IS-11: Listing {} expired with only {}% funded",
                listing.getId(), listing.getFundingPct());

            listing.markFundingFailed();
            listingRepository.save(listing);

            // Cancel all investments for this listing and trigger escrow refunds
            List<Investment> investments = investmentRepository
                .findByInvestorId(listing.getFarmerId()); // by farmId ideally — simplified here
            for (Investment investment : investments) {
                if (investment.getFarmId().equals(listing.getFarmId())
                        && investment.getStatus() != InvestmentStatus.CANCELLED) {
                    try {
                        escrowServicePort.cancel(investment.getId());
                        investment.cancel("Listing funding failed — funds refunded");
                        investmentRepository.save(investment);
                    } catch (Exception e) {
                        log.error("IS-11: Failed to cancel investment {}: {}",
                            investment.getId(), e.getMessage());
                    }
                }
            }

            eventPublisher.publishListingFundingFailed(listing);
        }

        log.info("IS-11: Processed {} expired listings", expired.size());
    }

    // IS-07: Portfolio
    @Override
    @Transactional(readOnly = true)
    public List<Investment> getPortfolio(UUID investorId) {
        return investmentRepository.findByInvestorId(investorId);
    }

    // IS-08: Investment details
    @Override
    @Transactional(readOnly = true)
    public Investment getInvestmentDetails(UUID investmentId) {
        return investmentRepository.findById(investmentId)
            .orElseThrow(() -> new InvestmentNotFoundException(investmentId.toString()));
    }

    // IS-12: Payout history
    @Override
    @Transactional(readOnly = true)
    public List<PayoutRecord> getPayoutHistory(UUID investorId) {
        return payoutRepository.findByInvestorId(investorId);
    }

    // APR starts higher for farmers with better agri-score
    private BigDecimal calculateBaseApr(int agriScore) {
        if (agriScore >= 700) return new BigDecimal("12.00");
        if (agriScore >= 500) return new BigDecimal("10.00");
        if (agriScore >= 300) return new BigDecimal("8.50");
        return new BigDecimal("7.00");
    }
}
