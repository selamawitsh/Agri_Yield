package com.agriyield.offtakerservice.application.port.outgoing;

import com.agriyield.offtakerservice.domain.enums.BidStatus;
import com.agriyield.offtakerservice.domain.model.Bid;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BidRepositoryPort {
    Bid save(Bid bid);
    Optional<Bid> findById(UUID id);
    List<Bid> findByOfftakerId(UUID offtakerId);
    List<Bid> findByFarmId(UUID farmId);
    List<Bid> findByStatusAndExpiresAtBefore(BidStatus status, OffsetDateTime dateTime);

    /** Alias used by AgreementServiceImpl — delegates to findById */
    default Bid findByBidId(UUID bidId) {
        return findById(bidId)
            .orElseThrow(() -> new com.agriyield.offtakerservice.domain.exception.ResourceNotFoundException(
                "Bid not found: " + bidId));
    }
}
