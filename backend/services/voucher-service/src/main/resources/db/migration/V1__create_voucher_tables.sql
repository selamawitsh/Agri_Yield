CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE vouchers (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    voucher_code        VARCHAR(30) NOT NULL UNIQUE,
    investment_id       UUID NOT NULL,
    farm_id             UUID NOT NULL,
    farmer_id           UUID NOT NULL,
    merchant_id         UUID,
    input_need_id       UUID NOT NULL,
    input_need_item_id  UUID NOT NULL,
    crop_cycle_id       UUID NOT NULL,
    product_name        VARCHAR(255) NOT NULL,
    product_category    VARCHAR(30) NOT NULL,
    amount_etb          NUMERIC(14,2) NOT NULL,
    status              VARCHAR(30) NOT NULL DEFAULT 'GENERATED',
    issued_at           TIMESTAMPTZ,
    redeemed_at         TIMESTAMPTZ,
    expires_at          TIMESTAMPTZ NOT NULL,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE voucher_redemptions (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    voucher_id      UUID NOT NULL REFERENCES vouchers(id),
    merchant_id     UUID NOT NULL,
    redeemed_by     UUID NOT NULL,
    amount_etb      NUMERIC(14,2) NOT NULL,
    escrow_released BOOLEAN NOT NULL DEFAULT FALSE,
    notes           VARCHAR(500),
    redeemed_at     TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_vouchers_investment_id   ON vouchers(investment_id);
CREATE INDEX idx_vouchers_farm_id         ON vouchers(farm_id);
CREATE INDEX idx_vouchers_farmer_id       ON vouchers(farmer_id);
CREATE INDEX idx_vouchers_merchant_id     ON vouchers(merchant_id);
CREATE INDEX idx_vouchers_voucher_code    ON vouchers(voucher_code);
CREATE INDEX idx_vouchers_status          ON vouchers(status);
CREATE INDEX idx_redemptions_voucher_id   ON voucher_redemptions(voucher_id);
CREATE INDEX idx_redemptions_merchant_id  ON voucher_redemptions(merchant_id);
