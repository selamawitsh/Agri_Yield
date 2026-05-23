package com.agriyield.investmentservice.application.port.outgoing;

import com.agriyield.investmentservice.domain.model.FarmListing;
import com.agriyield.investmentservice.domain.model.Investment;

public interface EventPublisherPort {

    void publishInvestmentPlaced(Investment investment);

    void publishInvestmentEscrowLocked(Investment investment);

    void publishInvestmentCancelled(Investment investment);

    void publishInvestmentCompleted(Investment investment);

    void publishListingCreated(FarmListing listing);

    void publishListingFullyFunded(FarmListing listing);

    void publishListingFundingFailed(FarmListing listing);
}
