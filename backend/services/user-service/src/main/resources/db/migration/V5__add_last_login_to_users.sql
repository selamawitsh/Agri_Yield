-- Add last_login_at column to users table
ALTER TABLE users ADD COLUMN last_login_at TIMESTAMP;

-- Update account_status to include PENDING_VERIFICATION
ALTER TABLE users DROP CONSTRAINT users_account_status_check;
ALTER TABLE users ADD CONSTRAINT users_account_status_check 
    CHECK (account_status IN ('PENDING_VERIFICATION', 'ACTIVE', 'SUSPENDED', 'DELETED'));
