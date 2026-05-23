CREATE TABLE farm_listings (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    farm_id             UUID NOT NULL,
    farmer_id           UUID NOT NULL,
    input_need_id       UUID NOT NULL UNIQUE,
    crop_cycle_id       UUID NOT NULL,
    crop_type           VARCHAR(30) NOT NULL,
    region              VARCHAR(50) NOT NULL,
    kebele_code         VARCHAR(20) NOT NULL,
    season_name         VARCHAR(50) NOT NULL,
    total_amount_etb    NUMERIC(14,2) NOT NULL,
    funded_amount_etb   NUMERIC(14,2) NOT NULL DEFAULT 0.00,
    funding_pct         NUMERIC(5,2) NOT NULL DEFAULT 0.00,
    base_apr            NUMERIC(5,2) NOT NULL DEFAULT 8.50,
    current_apr         NUMERIC(5,2) NOT NULL DEFAULT 8.50,
    ndvi_bonus          NUMERIC(5,2) NOT NULL DEFAULT 0.00,
    weather_bonus       NUMERIC(5,2) NOT NULL DEFAULT 0.00,
    ndvi_penalty        NUMERIC(5,2) NOT NULL DEFAULT 0.00,
    drought_risk        NUMERIC(5,2) NOT NULL DEFAULT 0.00,
    agri_score          INTEGER NOT NULL DEFAULT 0,
    status              VARCHAR(30) NOT NULL DEFAULT 'OPEN',
    funding_deadline    TIMESTAMPTZ,
    fully_funded_at     TIMESTAMPTZ,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE payout_records (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    investment_id   UUID NOT NULL REFERENCES investments(id),
    investor_id     UUID NOT NULL,
    farm_id         UUID NOT NULL,
    listing_id      UUID NOT NULL REFERENCES farm_listings(id),
    principal_etb   NUMERIC(14,2) NOT NULL,
    return_etb      NUMERIC(14,2) NOT NULL,
    total_etb       NUMERIC(14,2) NOT NULL,
    actual_apr      NUMERIC(5,2) NOT NULL,
    payout_reason   VARCHAR(255),
    paid_at         TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_listings_farm_id      ON farm_listings(farm_id);
CREATE INDEX idx_listings_status       ON farm_listings(status);
CREATE INDEX idx_listings_crop_type    ON farm_listings(crop_type);
CREATE INDEX idx_listings_region       ON farm_listings(region);
CREATE INDEX idx_listings_input_need   ON farm_listings(input_need_id);
CREATE INDEX idx_payout_investor_id    ON payout_records(investor_id);
CREATE INDEX idx_payout_investment_id  ON payout_records(investment_id);
