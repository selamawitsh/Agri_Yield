package com.agriyield.userservice.core.port.outgoing;

public interface FaydaVerificationPort {
    boolean verifyFaydaId(String faydaId, String phone, String fullName);
    KycData pullKycData(String faydaId);

    class KycData {
        private final String faydaId;
        private final String fullName;
        private final String dateOfBirth;
        private final String region;
        private final boolean verified;

        public KycData(String faydaId, String fullName, String dateOfBirth, String region, boolean verified) {
            this.faydaId = faydaId;
            this.fullName = fullName;
            this.dateOfBirth = dateOfBirth;
            this.region = region;
            this.verified = verified;
        }

        // Getters
        public String getFaydaId() { return faydaId; }
        public String getFullName() { return fullName; }
        public String getDateOfBirth() { return dateOfBirth; }
        public String getRegion() { return region; }
        public boolean isVerified() { return verified; }
    }
}