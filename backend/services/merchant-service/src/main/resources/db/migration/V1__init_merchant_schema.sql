CREATE SCHEMA IF NOT EXISTS merchant_service;

SET search_path TO merchant_service;

CREATE TABLE IF NOT EXISTS products (
                                        id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    merchant_id UUID NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    product_category VARCHAR(30) NOT NULL CHECK (product_category IN ('SEED','FERTILIZER','PESTICIDE','TOOL','OTHER')),
    unit VARCHAR(20) NOT NULL,
    current_price_etb NUMERIC(12,2) NOT NULL,
    is_available BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
    );

CREATE TABLE IF NOT EXISTS price_history (
                                             id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id UUID NOT NULL REFERENCES products(id),
    old_price_etb NUMERIC(12,2) NOT NULL,
    new_price_etb NUMERIC(12,2) NOT NULL,
    changed_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    changed_by UUID NOT NULL
    );

CREATE TABLE IF NOT EXISTS price_anomalies (
                                               id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    merchant_id UUID NOT NULL,
    product_id UUID NOT NULL REFERENCES products(id),
    merchant_price_etb NUMERIC(12,2) NOT NULL,
    regional_median_etb NUMERIC(12,2) NOT NULL,
    deviation_pct NUMERIC(6,2) NOT NULL,
    flagged_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    resolved_at TIMESTAMPTZ
    );

CREATE INDEX IF NOT EXISTS idx_products_merchant_id ON products(merchant_id);
CREATE INDEX IF NOT EXISTS idx_products_category ON products(product_category);
CREATE INDEX IF NOT EXISTS idx_price_history_product_id ON price_history(product_id);
CREATE INDEX IF NOT EXISTS idx_price_anomalies_merchant_id ON price_anomalies(merchant_id);