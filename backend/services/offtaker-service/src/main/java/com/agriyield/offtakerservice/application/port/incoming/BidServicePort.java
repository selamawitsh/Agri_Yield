package com.agriyield.offtakerservice.application.port.incoming;

import com.agriyield.offtakerservice.domain.model.Bid;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface BidServicePort {
    Bid placeBid(UUID offtakerId, UUID farmId, BigDecimal quantityQuintals,
                 BigDecimal pricePerQuintalEtb, int expiresInDays);
    Bid acceptBid(UUID bidId, UUID farmerId);
    Bid rejectBid(UUID bidId, UUID farmerId);
    Bid getById(UUID bidId);
    List<Bid> getMyBids(UUID offtakerId);
    List<Bid> getBidsForFarm(UUID farmId);
    void expireStaleBids();
}
