INSERT INTO users (
    id,
    phone,
    email,
    fayda_id,
    password_hash,
    role,
    kyc_status,
    account_status,
    preferred_language,
    fayda_verified_at,
    created_at,
    updated_at
)
SELECT
    gen_random_uuid(),
    '+251911111111',
    'admin@agriyield.com',
    'FAYDA-ADMIN-001',
    '$2a$10$XQxQ0xL7VYhQ9l0v4H6Y7e6jM0M4s2Vx5g8KQ6sF0Pz4wWm6N7B8K',
    'ADMIN',
    'VERIFIED',
    'ACTIVE',
    'en',
    NOW(),
    NOW(),
    NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM users
    WHERE phone = '+251911111111'
);
