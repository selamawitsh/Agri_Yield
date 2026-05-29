CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE investments (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    investor_id      UUID NOT NULL,
    farm_id          UUID NOT NULL,
    farmer_id        UUID NOT NULL,
    input_need_id    UUID NOT NULL,
    crop_cycle_id    UUID NOT NULL,
    amount_etb       NUMERIC(14,2) NOT NULL,
    status           VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    crop_type        VARCHAR(30) NOT NULL,
    region           VARCHAR(50) NOT NULL,
    season_name      VARCHAR(50) NOT NULL,
    expected_return_pct  NUMERIC(5,2) NOT NULL DEFAULT 0.00,
    actual_return_pct    NUMERIC(5,2),
    notes            TEXT,
    cancelled_reason VARCHAR(255),
    created_at       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_investments_investor_id ON investments(investor_id);
CREATE INDEX idx_investments_farm_id     ON investments(farm_id);
CREATE INDEX idx_investments_farmer_id   ON investments(farmer_id);
CREATE INDEX idx_investments_status      ON investments(status);
CREATE INDEX idx_investments_input_need  ON investments(input_need_id);
