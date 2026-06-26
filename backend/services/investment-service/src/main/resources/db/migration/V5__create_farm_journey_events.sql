CREATE TABLE IF NOT EXISTS farm_journey_events (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    farm_id         UUID NOT NULL,
    event_type      VARCHAR(50) NOT NULL,
    event_data      JSONB,
    occurred_at     TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_journey_farm_id ON farm_journey_events(farm_id);
CREATE INDEX idx_journey_event_type ON farm_journey_events(event_type);
