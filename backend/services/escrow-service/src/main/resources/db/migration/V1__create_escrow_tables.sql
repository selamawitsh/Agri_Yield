CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE escrow_accounts (
    id                   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    investment_id        UUID NOT NULL UNIQUE,
    farmer_id            UUID NOT NULL,
    investor_id          UUID NOT NULL,
    total_amount_etb     NUMERIC(14,2) NOT NULL,
    locked_amount_etb    NUMERIC(14,2) NOT NULL DEFAULT 0.00,
    released_amount_etb  NUMERIC(14,2) NOT NULL DEFAULT 0.00,
    status               VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    lock_expires_at      TIMESTAMPTZ,
    created_at           TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at           TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE escrow_transactions (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    escrow_account_id   UUID NOT NULL REFERENCES escrow_accounts(id) ON DELETE CASCADE,
    transaction_type    VARCHAR(30) NOT NULL,
    amount_etb          NUMERIC(14,2) NOT NULL,
    reference_id        UUID,
    description         VARCHAR(500),
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE escrow_releases (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    escrow_account_id   UUID NOT NULL REFERENCES escrow_accounts(id),
    voucher_id          UUID,
    amount_etb          NUMERIC(14,2) NOT NULL,
    release_reason      VARCHAR(255),
    released_at         TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_escrow_accounts_investment_id ON escrow_accounts(investment_id);
CREATE INDEX idx_escrow_accounts_farmer_id     ON escrow_accounts(farmer_id);
CREATE INDEX idx_escrow_accounts_investor_id   ON escrow_accounts(investor_id);
CREATE INDEX idx_escrow_accounts_status        ON escrow_accounts(status);
CREATE INDEX idx_escrow_transactions_account   ON escrow_transactions(escrow_account_id);
CREATE INDEX idx_escrow_releases_account       ON escrow_releases(escrow_account_id);