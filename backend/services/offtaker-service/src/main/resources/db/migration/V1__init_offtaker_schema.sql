CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE bids (
    id                    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    offtaker_id           UUID NOT NULL,
    farm_id               UUID NOT NULL,
    crop_cycle_id         UUID NOT NULL,
    quantity_quintals      NUMERIC(10,2) NOT NULL,
    price_per_quintal_etb NUMERIC(10,2) NOT NULL,
    total_value_etb       NUMERIC(15,2) GENERATED ALWAYS AS (quantity_quintals * price_per_quintal_etb) STORED,
    bid_deposit_etb       NUMERIC(12,2) GENERATED ALWAYS AS (quantity_quintals * price_per_quintal_etb * 0.10) STORED,
    status                VARCHAR(30)    NOT NULL DEFAULT 'PENDING',
    expires_at            TIMESTAMPTZ    NOT NULL,
    accepted_at           TIMESTAMPTZ,
    created_at            TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at            TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_bid_status CHECK (status IN (
        'PENDING','ACCEPTED','REJECTED','CONTRACT_SIGNED','COMPLETED','DEFAULTED','EXPIRED'
    ))
);

CREATE TABLE purchase_agreements (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    bid_id              UUID NOT NULL UNIQUE REFERENCES bids(id),
    contract_hash       VARCHAR(255) NOT NULL,
    contract_pdf_url    VARCHAR(500) NOT NULL,
    farmer_signed_at    TIMESTAMPTZ,
    offtaker_signed_at  TIMESTAMPTZ,
    is_fully_executed   BOOLEAN NOT NULL DEFAULT FALSE,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE truck_dispatches (
    id                         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    agreement_id               UUID NOT NULL REFERENCES purchase_agreements(id),
    driver_fayda_id            VARCHAR(50) NOT NULL,
    truck_count                INTEGER NOT NULL,
    scheduled_pickup_date      DATE NOT NULL,
    actual_pickup_date         DATE,
    driver_penalty_escrow_etb  NUMERIC(10,2) NOT NULL DEFAULT 500.00,
    status                     VARCHAR(30) NOT NULL DEFAULT 'SCHEDULED',
    created_at                 TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at                 TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_dispatch_status CHECK (status IN (
        'SCHEDULED','ARRIVED','LOADED','DELIVERED','DRIVER_DEFAULTED'
    ))
);

CREATE INDEX idx_bids_offtaker_id  ON bids(offtaker_id);
CREATE INDEX idx_bids_farm_id      ON bids(farm_id);
CREATE INDEX idx_bids_status       ON bids(status);
CREATE INDEX idx_agreements_bid_id ON purchase_agreements(bid_id);
CREATE INDEX idx_dispatches_agreement_id ON truck_dispatches(agreement_id);
