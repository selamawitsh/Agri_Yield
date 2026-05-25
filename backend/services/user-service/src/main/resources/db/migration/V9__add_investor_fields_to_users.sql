-- Add investor-specific fields to users table
ALTER TABLE users 
ADD COLUMN IF NOT EXISTS risk_tolerance VARCHAR(20),
ADD COLUMN IF NOT EXISTS investment_goal VARCHAR(255);
