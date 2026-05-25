package com.agriyield.userservice.application.port.outgoing;

import lombok.Getter;

public interface FaydaVerificationPort {

    boolean verifyFaydaId(String faydaId, String phone, String fullName);

    KycData pullKycData(String faydaId);

    @Getter
    class KycData {
        private final String fullName;
        private final String dateOfBirth;
        private final String region;

        public KycData(String fullName, String dateOfBirth, String region) {
            this.fullName = fullName;
            this.dateOfBirth = dateOfBirth;
            this.region = region;
        }
    }
}
