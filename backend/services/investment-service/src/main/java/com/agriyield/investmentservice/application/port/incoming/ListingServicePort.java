package com.agriyield.investmentservice.application.port.incoming;

import com.agriyield.investmentservice.domain.model.FarmListing;
import com.agriyield.investmentservice.domain.model.Investment;
import com.agriyield.investmentservice.domain.model.PayoutRecord;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface ListingServicePort {

    /** IS-04: Create listing from input.needs.created event */
    FarmListing createFromInputNeeds(UUID farmId,
                                     UUID farmerId,
                                     UUID inputNeedId,
                                     UUID cropCycleId,
                                     BigDecimal totalAmountEtb,
                                     String cropType,
                                     String region,
                                     String kebeleCode,
                                     String seasonName,
                                     int agriScore);

    /** IS-01/02: Browse and filter listings */
    List<FarmListing> getActiveListings(String cropType,
                                        String region,
                                        BigDecimal minApr,
                                        BigDecimal maxApr);

    /** IS-03: View listing details */
    FarmListing getListingById(UUID listingId);

    /** IS-05: Invest in a listing */
    Investment investInListing(UUID listingId,
                               UUID investorId,
                               BigDecimal amountEtb,
                               String notes);

    /** IS-09: NDVI history for a listing (from farm digital twin) */
    Object getNdviHistory(UUID listingId);

    /** IS-10: Update dynamic APR */
    FarmListing updateApr(UUID listingId,
                          BigDecimal ndviBonus,
                          BigDecimal weatherBonus,
                          BigDecimal ndviPenalty,
                          BigDecimal droughtRisk);

    /** IS-11: Check and process expired funding deadlines */
    void processExpiredListings();

    /** IS-07: Investor portfolio */
    List<Investment> getPortfolio(UUID investorId);

    /** IS-08: Investment details */
    Investment getInvestmentDetails(UUID investmentId);

    /** IS-12: Payout history */
    List<PayoutRecord> getPayoutHistory(UUID investorId);
}
