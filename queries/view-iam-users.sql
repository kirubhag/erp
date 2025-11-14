-- Query to view all IAM users with their credentials
SELECT 
    id,
    username,
    email,
    password_hash,
    user_type,
    first_name,
    last_name,
    enabled,
    account_non_locked,
    account_non_expired,
    credentials_non_expired
FROM iam_users
ORDER BY id ASC;

-- Expected output showing 3 test users:
-- 1. admin with BCrypt hash for password: admin123
-- 2. student with BCrypt hash for password: student123
-- 3. teacher with BCrypt hash for password: teacher123
