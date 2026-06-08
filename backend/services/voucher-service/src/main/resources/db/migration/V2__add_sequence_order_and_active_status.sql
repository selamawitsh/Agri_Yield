-- Add sequence_order column (SRS §3.5: controls unlock order)
ALTER TABLE vouchers ADD COLUMN IF NOT EXISTS sequence_order INTEGER NOT NULL DEFAULT 1;

CREATE INDEX IF NOT EXISTS idx_vouchers_sequence_order ON vouchers(farm_id, sequence_order);

-- Fix the 4 stuck vouchers: sequence_order=1 vouchers should be ACTIVE, not GENERATED
UPDATE vouchers
SET status = 'ACTIVE', updated_at = NOW()
WHERE status = 'GENERATED' AND sequence_order = 1;
