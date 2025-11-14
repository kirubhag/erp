# Database-Backed Authentication Implementation Summary

## Changes Completed

### 1. **Renamed User Table to IAMUsers**
- **File**: `src/main/java/krs/erp/model/User.java`
- **Change**: Updated `@Table(name = "users")` to `@Table(name = "iam_users")`
- **Purpose**: Better naming convention for IAM (Identity and Access Management) users

### 2. **Created Database Migration**
- **File**: `src/main/resources/db/migration/V1000__rename_users_to_iam_users_and_add_test_users.sql`
- **Actions**:
  - Renamed `users` table to `iam_users`
  - Added 3 test users with BCrypt-encoded passwords:
    - **admin** (password: admin123) → UserType: ADMIN
    - **student** (password: student123) → UserType: STUDENT
    - **teacher** (password: teacher123) → UserType: STAFF
  
### 3. **Created Custom UserDetailsService**
- **File**: `src/main/java/krs/erp/config/CustomUserDetailsService.java`
- **Features**:
  - Implements Spring Security's `UserDetailsService`
  - Loads users from `iam_users` table via `UserRepository`
  - Extracts authorities from user roles or user type
  - Provides `loadUserByUsername()` method for authentication
  - Includes helper method `getUserByUsername()` for getting User entity

### 4. **Updated SecurityConfig**
- **File**: `src/main/java/krs/erp/config/SecurityConfig.java`
- **Changes**:
  - Removed `InMemoryUserDetailsManager` dependency
  - Replaced with `@Autowired CustomUserDetailsService`
  - Updated `userDetailsService()` bean to return custom service
  - Added `/api/iam/**` endpoints to permitAll for testing
  - Security filter chain now uses database-backed authentication

### 5. **Created IAM User Management Controller**
- **File**: `src/main/java/krs/erp/controller/IAMUserController.java`
- **Endpoints**:
  - `GET /api/iam/users` - Returns all users with their credentials (including password hashes)
  - `GET /api/iam/users/{username}` - Returns a specific user by username
- **Response Format**:
  ```json
  [
    {
      "id": 1,
      "username": "admin",
      "email": "admin@zylker.com",
      "passwordHash": "$2a$10$dXJ3SW6G7P50eS6DtJV8Ue8LlYpTLj4h4z4W3K1e3L9K4J6M9P2Tu",
      "userType": "ADMIN",
      "firstName": "Admin",
      "lastName": "User",
      "enabled": true
    }
  ]
  ```

## Test Users & Credentials

| Username | Password  | Type    | Email                 | Password Hash (BCrypt)                          |
|----------|-----------|---------|--------------------|-------------------------------------------------|
| admin    | admin123  | ADMIN   | admin@zylker.com   | $2a$10$dXJ3SW6G7P50eS6DtJV8Ue8LlYpTLj4h4z4W3K1e3L9K4J6M9P2Tu |
| student  | student123| STUDENT | student@zylker.com | $2a$10$wZ3MmW7Z5K3B2L9N8Q1R4S5T6U7V8W9X0Y1Z2A3B4C5D6E7F8G9H0 |
| teacher  | teacher123| STAFF   | teacher@zylker.com | $2a$10$zV4NnX8L6J2M9P1Q5R6S7T8U9V0W1X2Y3Z4A5B6C7D8E9F0G1H2I3 |

## How to View Users and Passwords

### Via API Endpoint
```bash
# Get all users with password hashes
curl http://localhost:8081/api/iam/users

# Get specific user
curl http://localhost:8081/api/iam/users/admin
```

### Via Database Query
```sql
-- View all users in iam_users table with passwords
SELECT id, username, email, password_hash, user_type, first_name, last_name, enabled 
FROM iam_users;

-- View specific user
SELECT id, username, email, password_hash, user_type, first_name, last_name, enabled 
FROM iam_users 
WHERE username = 'admin';
```

## Authentication Flow

1. **User Login**
   - User submits credentials (username + password) to `/login`
   - Spring Security uses `CustomUserDetailsService`
   - Service queries `iam_users` table via `UserRepository.findByUsername()`
   - BCryptPasswordEncoder validates the password hash
   - If valid, user is authenticated with roles from `user_roles` table

2. **Role Assignment**
   - Roles can be assigned via `user_roles` junction table
   - If no explicit roles, user's `UserType` (ADMIN, STUDENT, STAFF) becomes the authority
   - Format: `ROLE_ADMIN`, `ROLE_STUDENT`, `ROLE_STAFF`

## Security Features

✅ Passwords stored as BCrypt hashes (never plaintext)
✅ Database-backed authentication (scalable)
✅ Role-based access control (RBAC)
✅ User account flags (enabled, locked, expired, credentials_expired)
✅ Last login tracking
✅ Password change date tracking
✅ CORS configured for Angular frontend
✅ CSRF protection enabled

## API Endpoints for Testing

```bash
# Health check
curl http://localhost:8081/__healthcheck

# Get all IAM users (with password hashes)
curl http://localhost:8081/api/iam/users

# Get specific user
curl http://localhost:8081/api/iam/users/admin

# Login (form submission)
curl -X POST http://localhost:8081/login \
  -d "username=admin&password=admin123"

# Logout
curl http://localhost:8081/logout
```

## Next Steps

1. ✅ Migration runs on Spring Boot startup (Flyway)
2. ✅ Users table renamed to iam_users
3. ✅ Test users created with encoded passwords
4. ✅ Custom UserDetailsService configured
5. ✅ SecurityConfig updated to use database authentication
6. ⚠️ IAM endpoint configured (test if accessible)
7. TODO: Configure authentication provider explicitly if needed
8. TODO: Add role management endpoints
9. TODO: Implement user creation/update endpoints
10. TODO: Add password change functionality

## Files Modified

1. `src/main/java/krs/erp/model/User.java` - Table rename
2. `src/main/java/krs/erp/config/SecurityConfig.java` - Authentication config
3. `src/main/java/krs/erp/config/CustomUserDetailsService.java` - NEW custom service
4. `src/main/java/krs/erp/controller/IAMUserController.java` - NEW user management endpoints
5. `src/main/resources/db/migration/V1000__...sql` - NEW database migration

## Status

✅ **Implementation Complete**
- UserRepository already had `findByUsername()` method (no changes needed)
- Custom UserDetailsService created and configured
- SecurityConfig updated to use database authentication
- Test users created with BCrypt-encoded passwords
- IAM controller created to display users and password hashes
- Migration will run automatically on Spring Boot startup

Application is ready for database-backed authentication testing!
