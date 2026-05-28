CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- FR-07 / FR-08: Fraud alerts table
CREATE TABLE fraud_alerts (
    id                    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    alert_type            VARCHAR(60)  NOT NULL,
    entity_type           VARCHAR(20)  NOT NULL,
    entity_id             UUID,
    severity              VARCHAR(20)  NOT NULL,
    description           VARCHAR(500) NOT NULL,
    evidence              TEXT,
    resolved              BOOLEAN      NOT NULL DEFAULT FALSE,
    resolved_by_admin_id  UUID,
    resolution_notes      VARCHAR(500),
    resolved_at           TIMESTAMPTZ,
    created_at            TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at            TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

-- FR-06: Fraud risk scores per entity
CREATE TABLE fraud_risk_scores (
    id                       UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    entity_id                UUID        NOT NULL,
    entity_type              VARCHAR(20) NOT NULL,
    gps_anomaly_score        INTEGER     NOT NULL DEFAULT 0,
    duplicate_voucher_score  INTEGER     NOT NULL DEFAULT 0,
    exif_mismatch_score      INTEGER     NOT NULL DEFAULT 0,
    suspicious_activity_score INTEGER    NOT NULL DEFAULT 0,
    total_score              INTEGER     NOT NULL DEFAULT 0,
    severity                 VARCHAR(20) NOT NULL DEFAULT 'LOW',
    calculated_at            TIMESTAMPTZ,
    created_at               TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at               TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (entity_id, entity_type)
);

-- FR-05: GPS submission logs
CREATE TABLE gps_logs (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    entity_id    UUID        NOT NULL,
    entity_type  VARCHAR(20) NOT NULL,
    latitude     NUMERIC(10,7) NOT NULL,
    longitude    NUMERIC(10,7) NOT NULL,
    context      VARCHAR(50),
    flagged      BOOLEAN     NOT NULL DEFAULT FALSE,
    flag_reason  VARCHAR(255),
    recorded_at  TIMESTAMPTZ NOT NULL,
    created_at   TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Indexes
CREATE INDEX idx_fraud_alerts_entity     ON fraud_alerts(entity_id, entity_type);
CREATE INDEX idx_fraud_alerts_severity   ON fraud_alerts(severity);
CREATE INDEX idx_fraud_alerts_resolved   ON fraud_alerts(resolved);
CREATE INDEX idx_fraud_alerts_created    ON fraud_alerts(created_at DESC);
CREATE INDEX idx_fraud_scores_entity     ON fraud_risk_scores(entity_id, entity_type);
CREATE INDEX idx_gps_logs_entity         ON gps_logs(entity_id, entity_type);
CREATE INDEX idx_gps_logs_recorded       ON gps_logs(recorded_at DESC);
