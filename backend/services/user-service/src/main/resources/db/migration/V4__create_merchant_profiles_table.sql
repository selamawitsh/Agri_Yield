-- SRS Page 14: Merchant profiles table specification
CREATE TABLE IF NOT EXISTS merchant_profiles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    business_name VARCHAR(255) NOT NULL,
    business_license_number VARCHAR(100) NOT NULL UNIQUE,
    store_gps_lat NUMERIC(10,7) NOT NULL,
    store_gps_lng NUMERIC(10,7) NOT NULL,
    is_physically_verified BOOLEAN NOT NULL DEFAULT FALSE,
    physically_verified_at TIMESTAMP,
    verified_by_agent_id UUID,
    subscription_tier VARCHAR(20) NOT NULL DEFAULT 'BASIC' CHECK (subscription_tier IN ('BASIC', 'PREMIUM')),
    cbe_account VARCHAR(30),
    telebirr_account VARCHAR(30),
    is_premium BOOLEAN NOT NULL DEFAULT FALSE,
    premium_expiry TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_merchant_profiles_user_id ON merchant_profiles(user_id);
CREATE INDEX idx_merchant_profiles_business_license ON merchant_profiles(business_license_number);
CREATE INDEX idx_merchant_profiles_verification ON merchant_profiles(is_physically_verified);
