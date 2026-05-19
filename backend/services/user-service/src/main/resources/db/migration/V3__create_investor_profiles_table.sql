-- SRS Page 13: Investor profiles table specification
CREATE TABLE IF NOT EXISTS investor_profiles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    agri_score INTEGER NOT NULL DEFAULT 50 CHECK (agri_score >= 0 AND agri_score <= 900),
    cooperative_id UUID,
    telebird_account VARCHAR(30),
    total_seasons_completed INTEGER NOT NULL DEFAULT 0,
    preferred_language VARCHAR(10) NOT NULL DEFAULT 'am' CHECK (preferred_language IN ('am', 'om', 'ti', 'en')),
    risk_tolerance VARCHAR(20) NOT NULL DEFAULT 'MODERATE' CHECK (risk_tolerance IN ('LOW', 'MODERATE', 'HIGH')),
    investment_goal VARCHAR(50),
    total_invested_etb NUMERIC(15,2) NOT NULL DEFAULT 0.00,
    total_returned_etb NUMERIC(15,2) NOT NULL DEFAULT 0.00,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_investor_profiles_user_id ON investor_profiles(user_id);
CREATE INDEX idx_investor_profiles_agri_score ON investor_profiles(agri_score);
