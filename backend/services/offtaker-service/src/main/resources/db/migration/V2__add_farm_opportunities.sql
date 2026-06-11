CREATE TABLE farm_opportunities (
    id                            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    farm_id                       UUID NOT NULL UNIQUE,
    farmer_id                     VARCHAR(255),
    crop_type                     VARCHAR(30),
    area_hectares                 NUMERIC(8,4),
    region                        VARCHAR(50),
    kebele_code                   VARCHAR(20),
    gps_centroid_lat              NUMERIC(10,7),
    gps_centroid_lng              NUMERIC(10,7),
    agri_score                    INTEGER NOT NULL DEFAULT 50,
    crop_cycle_id                 VARCHAR(255),
    crop_cycle_status             VARCHAR(30),
    current_ndvi                  NUMERIC(5,4),
    ndvi_health_status            VARCHAR(20),
    predicted_yield_min_quintals  NUMERIC(10,2),
    predicted_yield_max_quintals  NUMERIC(10,2),
    predicted_yield_mean_quintals NUMERIC(10,2),
    yield_confidence_pct          INTEGER,
    harvest_ready                 BOOLEAN NOT NULL DEFAULT FALSE,
    estimated_harvest_date_from   VARCHAR(30),
    estimated_harvest_date_to     VARCHAR(30),
    existing_bids_count           INTEGER NOT NULL DEFAULT 0,
    last_updated                  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_at                    TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_farm_opportunities_crop_type ON farm_opportunities(crop_type);
CREATE INDEX idx_farm_opportunities_region    ON farm_opportunities(region);
CREATE INDEX idx_farm_opportunities_harvest_ready ON farm_opportunities(harvest_ready);
