package com.agriyield.investmentservice.infrastructure.config;

import com.agriyield.investmentservice.application.port.incoming.ListingServicePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ListingExpiryScheduler {

    private final ListingServicePort listingService;

    /** IS-11: Runs every hour to check for expired funding deadlines */
    @Scheduled(fixedRate = 3600000)
    public void processExpiredListings() {
        log.info("Scheduler: checking for expired listings...");
        listingService.processExpiredListings();
    }
}
