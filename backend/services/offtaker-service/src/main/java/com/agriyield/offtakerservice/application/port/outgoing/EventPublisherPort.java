package com.agriyield.offtakerservice.application.port.outgoing;

import java.math.BigDecimal;
import java.util.UUID;

public interface EventPublisherPort {
    void publishBidPlaced(UUID bidId, UUID farmId, UUID offtakerId,
                          BigDecimal quantityQuintals, BigDecimal pricePerQuintalEtb,
                          BigDecimal totalValueEtb, String expiresAt);
    void publishBidAccepted(UUID bidId, UUID farmId, UUID farmerId,
                            UUID offtakerId, UUID agreementId);
    void publishHarvestConfirmed(UUID farmId, UUID agreementId,
                                 BigDecimal actualQuantityQuintals,
                                 String qualityGrade, BigDecimal totalPaymentEtb);
    void publishOfftakerDefaulted(UUID bidId, UUID farmId,
                                  UUID offtakerId, BigDecimal forfeitAmountEtb);
}
