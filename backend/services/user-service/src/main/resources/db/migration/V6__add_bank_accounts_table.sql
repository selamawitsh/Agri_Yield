-- Create bank_accounts table for storing user payment methods
CREATE TABLE IF NOT EXISTS bank_accounts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    account_type VARCHAR(20) NOT NULL CHECK (account_type IN ('TELEBIRR', 'CBE')),
    account_number VARCHAR(50) NOT NULL,
    account_holder_name VARCHAR(255),
    is_verified BOOLEAN NOT NULL DEFAULT FALSE,
    verified_at TIMESTAMP,
    is_default BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE(user_id, account_type)
);

-- Create index for faster lookups
CREATE INDEX idx_bank_accounts_user_id ON bank_accounts(user_id);
CREATE INDEX idx_bank_accounts_verified ON bank_accounts(is_verified);

-- Add bank_account reference to investor_profiles (for payouts)
ALTER TABLE investor_profiles ADD COLUMN default_payout_account_id UUID REFERENCES bank_accounts(id);

-- Add bank_account reference to merchant_profiles (for receipts)
ALTER TABLE merchant_profiles ADD COLUMN default_payment_account_id UUID REFERENCES bank_accounts(id);
