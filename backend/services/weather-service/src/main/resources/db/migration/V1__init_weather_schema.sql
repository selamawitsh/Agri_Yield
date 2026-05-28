CREATE TABLE IF NOT EXISTS weather_readings (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    farm_id UUID NOT NULL,
    gps_lat NUMERIC(10,7) NOT NULL,
    gps_lng NUMERIC(10,7) NOT NULL,
    temperature_c NUMERIC(5,2) NOT NULL,
    rainfall_mm NUMERIC(8,2) NOT NULL DEFAULT 0.0,
    humidity_pct NUMERIC(5,2),
    is_dry_day BOOLEAN NOT NULL GENERATED ALWAYS AS (rainfall_mm < 1.0) STORED,
    forecast_type VARCHAR(10) NOT NULL CHECK (forecast_type IN ('ACTUAL','FORECAST')),
    forecast_horizon_days INTEGER,
    recorded_date DATE NOT NULL,
    fetched_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS drought_conditions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    farm_id UUID NOT NULL UNIQUE,
    consecutive_dry_days INTEGER NOT NULL DEFAULT 0,
    drought_threshold_days INTEGER NOT NULL DEFAULT 30,
    is_triggered BOOLEAN NOT NULL DEFAULT FALSE,
    triggered_at TIMESTAMPTZ,
    last_checked TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS weather_alerts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    farm_id UUID NOT NULL,
    alert_type VARCHAR(30) NOT NULL,
    severity VARCHAR(10) NOT NULL CHECK (severity IN ('LOW','MEDIUM','HIGH','CRITICAL')),
    message_en TEXT NOT NULL,
    message_am TEXT,
    message_om TEXT,
    forecast_value NUMERIC(10,2),
    forecast_date DATE,
    is_sent BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_weather_readings_farm_id ON weather_readings(farm_id);
CREATE INDEX IF NOT EXISTS idx_weather_readings_recorded_date ON weather_readings(recorded_date);
CREATE INDEX IF NOT EXISTS idx_drought_conditions_farm_id ON drought_conditions(farm_id);
CREATE INDEX IF NOT EXISTS idx_weather_alerts_farm_id ON weather_alerts(farm_id);
