CREATE TABLE IF NOT EXISTS merchant_service.merchant_profiles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL UNIQUE,
    business_name VARCHAR(255) NOT NULL DEFAULT '',
    business_license_number VARCHAR(100) NOT NULL UNIQUE DEFAULT '',
    kebele_code VARCHAR(50),
    store_gps_lat NUMERIC(10,7) NOT NULL DEFAULT 0,
    store_gps_lng NUMERIC(10,7) NOT NULL DEFAULT 0,
    is_physically_verified BOOLEAN NOT NULL DEFAULT FALSE,
    physically_verified_at TIMESTAMPTZ,
    verified_by_agent_id UUID,
    subscription_tier VARCHAR(20) NOT NULL DEFAULT 'BASIC',
    cbe_account VARCHAR(30),
    telebirr_account VARCHAR(30),
    is_premium BOOLEAN NOT NULL DEFAULT FALSE,
    premium_expiry TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_merchant_profiles_user_id ON merchant_service.merchant_profiles(user_id);
CREATE INDEX IF NOT EXISTS idx_merchant_profiles_kebele ON merchant_service.merchant_profiles(kebele_code);
