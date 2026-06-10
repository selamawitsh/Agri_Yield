-- SRS §4.1.1: Add investment_pct, locked_at, payout_amount_etb, payout_at
ALTER TABLE investments
    ADD COLUMN IF NOT EXISTS investment_pct    NUMERIC(8,5),
    ADD COLUMN IF NOT EXISTS locked_at         TIMESTAMPTZ,
    ADD COLUMN IF NOT EXISTS payout_amount_etb NUMERIC(14,2),
    ADD COLUMN IF NOT EXISTS payout_at         TIMESTAMPTZ;

-- SRS §4.1.1: Rename funding_deadline → listing_expires_at
ALTER TABLE farm_listings
    RENAME COLUMN funding_deadline TO listing_expires_at;

COMMENT ON COLUMN investments.status IS
    'PENDING | ESCROW_LOCKED | ACTIVE | COMPLETED | REFUNDED | CANCELLED | FAILED';

CREATE INDEX IF NOT EXISTS idx_investments_investment_pct ON investments(investment_pct);
