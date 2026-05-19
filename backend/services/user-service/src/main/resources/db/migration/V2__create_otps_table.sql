-- SRS Page 15: OTPs table specification
CREATE TABLE IF NOT EXISTS otps (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    otp_code VARCHAR(6) NOT NULL,
    purpose VARCHAR(30) NOT NULL CHECK (purpose IN ('REGISTRATION', 'LOGIN', 'PASSWORD_RESET', 'BANK_VERIFY')),
    expires_at TIMESTAMP NOT NULL,
    used_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_otps_user_id ON otps(user_id);
CREATE INDEX idx_otps_code_purpose ON otps(otp_code, purpose);
CREATE INDEX idx_otps_expires_at ON otps(expires_at);
