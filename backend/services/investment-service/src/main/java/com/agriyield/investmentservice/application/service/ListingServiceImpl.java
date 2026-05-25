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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
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

    // @Lazy breaks the circular dependency:
    // ListingServiceImpl -> InvestmentServicePort (InvestmentServiceImpl)
    // InvestmentServiceImpl -> EscrowServicePort -> (no cycle)
    @Lazy
    @Autowired
    private InvestmentServicePort investmentService;

    @Value("${app.investment.min-amount-etb:500.00}")
    private BigDecimal minAmountEtb;

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

        listingRepository.findByInputNeedId(inputNeedId).ifPresent(existing -> {
            throw new BusinessException(
                "Listing already exists for input need: " + inputNeedId,
                "DUPLICATE_LISTING");
        });

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

        return listings.stream()
            .filter(l -> l.getStatus() == ListingStatus.OPEN
                || l.getStatus() == ListingStatus.PARTIALLY_FUNDED)
            .filter(l -> minApr == null || l.getCurrentApr().compareTo(minApr) >= 0)
            .filter(l -> maxApr == null || l.getCurrentApr().compareTo(maxApr) <= 0)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public FarmListing getListingById(UUID listingId) {
        return listingRepository.findById(listingId)
            .orElseThrow(() -> new BusinessException(
                "Listing not found: " + listingId, "LISTING_NOT_FOUND"));
    }

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

        BigDecimal remaining = listing.getTotalAmountEtb().subtract(listing.getFundedAmountEtb());
        if (amountEtb.compareTo(remaining) > 0) {
            throw new BusinessException(
                "Investment exceeds remaining funding needed: " + remaining + " ETB",
                "EXCEEDS_REMAINING");
        }

        Investment investment = investmentService.placeInvestment(
            investorId,
            listing.getFarmId(),
            listing.getInputNeedId(),
            amountEtb,
            notes);

        listing.addFunding(amountEtb);
        FarmListing savedListing = listingRepository.save(listing);

        if (savedListing.isFullyFunded()) {
            log.info("IS-06: Listing {} is now fully funded!", listingId);
            eventPublisher.publishListingFullyFunded(savedListing);
        }

        return investment;
    }

    @Override
    @Transactional(readOnly = true)
    public Object getNdviHistory(UUID listingId) {
        listingRepository.findById(listingId)
            .orElseThrow(() -> new BusinessException(
                "Listing not found: " + listingId, "LISTING_NOT_FOUND"));
        return java.util.List.of();
    }

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
        return listingRepository.save(listing);
    }

    @Override
    @Transactional
    public void processExpiredListings() {
        log.info("IS-11: Processing expired listings...");
        List<FarmListing> expired = listingRepository.findExpiredOpenListings();
        for (FarmListing listing : expired) {
            log.warn("IS-11: Listing {} expired with {}% funded",
                listing.getId(), listing.getFundingPct());
            listing.markFundingFailed();
            listingRepository.save(listing);

            // Find investments for this farm and cancel them
            List<Investment> investments = investmentRepository.findByFarmId(listing.getFarmId())
                .map(inv -> java.util.List.of(inv))
                .orElse(java.util.List.of());

            for (Investment investment : investments) {
                if (investment.getStatus() != InvestmentStatus.CANCELLED) {
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

    @Override
    @Transactional(readOnly = true)
    public List<Investment> getPortfolio(UUID investorId) {
        return investmentRepository.findByInvestorId(investorId);
    }

    @Override
    @Transactional(readOnly = true)
    public Investment getInvestmentDetails(UUID investmentId) {
        return investmentRepository.findById(investmentId)
            .orElseThrow(() -> new InvestmentNotFoundException(investmentId.toString()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PayoutRecord> getPayoutHistory(UUID investorId) {
        return payoutRepository.findByInvestorId(investorId);
    }

    private BigDecimal calculateBaseApr(int agriScore) {
        if (agriScore >= 700) return new BigDecimal("12.00");
        if (agriScore >= 500) return new BigDecimal("10.00");
        if (agriScore >= 300) return new BigDecimal("8.50");
        return new BigDecimal("7.00");
    }
}
