-- SRS Page 17-21: Farm Service PostgreSQL schema

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE farms (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    farmer_id UUID NOT NULL,
    farm_name VARCHAR(255),
    crop_type VARCHAR(30) NOT NULL CHECK (crop_type IN ('WHEAT','TEFF','BARLEY','MAIZE','SORGHUM','COFFEE','BEANS','MILLET')),
    area_hectares NUMERIC(8,4) NOT NULL DEFAULT 0,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING_VERIFICATION',
    kebele_code VARCHAR(20) NOT NULL,
    region VARCHAR(50) NOT NULL,
    gps_centroid_lat NUMERIC(10,7) NOT NULL,
    gps_centroid_lng NUMERIC(10,7) NOT NULL,
    geo_json_polygon TEXT,
    satellite_verified BOOLEAN NOT NULL DEFAULT FALSE,
    satellite_verified_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE crop_cycles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    farm_id UUID NOT NULL REFERENCES farms(id) ON DELETE CASCADE,
    season_name VARCHAR(50) NOT NULL,
    planting_date DATE,
    expected_harvest_date DATE NOT NULL,
    actual_harvest_date DATE,
    status VARCHAR(30) NOT NULL DEFAULT 'PLANNING',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE input_needs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    farm_id UUID NOT NULL REFERENCES farms(id) ON DELETE CASCADE,
    crop_cycle_id UUID NOT NULL REFERENCES crop_cycles(id),
    total_amount_etb NUMERIC(12,2) NOT NULL,
    funded_amount_etb NUMERIC(12,2) NOT NULL DEFAULT 0.00,
    status VARCHAR(30) NOT NULL DEFAULT 'OPEN',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE input_need_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    input_need_id UUID NOT NULL REFERENCES input_needs(id) ON DELETE CASCADE,
    product_category VARCHAR(30) NOT NULL CHECK (product_category IN ('SEED','FERTILIZER','PESTICIDE','TOOL','OTHER')),
    product_name VARCHAR(255) NOT NULL,
    quantity NUMERIC(10,2) NOT NULL,
    unit VARCHAR(20) NOT NULL,
    estimated_price_etb NUMERIC(12,2) NOT NULL,
    sequence_order INTEGER NOT NULL
);

CREATE TABLE farm_photos (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    farm_id UUID NOT NULL REFERENCES farms(id) ON DELETE CASCADE,
    photo_url VARCHAR(500) NOT NULL,
    gps_lat NUMERIC(10,7) NOT NULL,
    gps_lng NUMERIC(10,7) NOT NULL,
    photo_type VARCHAR(20) NOT NULL CHECK (photo_type IN ('REGISTRATION','CROP_HEALTH','HARVEST')),
    gps_verified BOOLEAN NOT NULL DEFAULT FALSE,
    uploaded_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE agri_scores (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    farmer_id UUID NOT NULL,
    crop_cycle_id UUID NOT NULL REFERENCES crop_cycles(id),
    score INTEGER NOT NULL CHECK (score >= 0 AND score <= 900),
    voucher_discipline_pts INTEGER NOT NULL DEFAULT 0,
    yield_accuracy_pts INTEGER NOT NULL DEFAULT 0,
    contract_fulfillment_pts INTEGER NOT NULL DEFAULT 0,
    repayment_completion_pts INTEGER NOT NULL DEFAULT 0,
    season_completion_pts INTEGER NOT NULL DEFAULT 100,
    agronomist_assessment_pts INTEGER NOT NULL DEFAULT 0,
    calculated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Indexes for common queries
CREATE INDEX idx_farms_farmer_id ON farms(farmer_id);
CREATE INDEX idx_farms_status ON farms(status);
CREATE INDEX idx_crop_cycles_farm_id ON crop_cycles(farm_id);
CREATE INDEX idx_input_needs_farm_id ON input_needs(farm_id);
CREATE INDEX idx_farm_photos_farm_id ON farm_photos(farm_id);
CREATE INDEX idx_agri_scores_farmer_id ON agri_scores(farmer_id);
