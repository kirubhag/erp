# Authentication System - Login Credentials

## ✅ Status
Authentication system is **working** and operational.

## Server Information
- **Backend**: http://localhost:8081
- **Frontend**: http://localhost:4200
- **Database**: MySQL on localhost:3307

## Login Endpoint
**POST** `/settings/auth/login`

### Request Body
```json
{
  "username": "admin",
  "password": "admin123"
}
```

### Success Response (HTTP 200)
```json
{
  "message": "Login successful",
  "status": "success",
  "user": {
    "id": 1,
    "username": "admin",
    "email": "admin@school.edu",
    "firstName": "System",
    "lastName": "Administrator",
    "userType": "ADMIN",
    "enabled": true
  }
}
```

### Error Response (HTTP 401)
```json
{
  "message": "Invalid credentials",
  "status": "error"
}
```

## Test User Credentials

### Admin User
- **Username**: `admin`
- **Password**: `admin123`
- **Email**: `admin@school.edu`
- **Type**: ADMIN

## Configuration Details

### CORS Configuration
- ✅ Enabled for `http://localhost:4200`
- ✅ Allows credentials
- ✅ Exposes necessary headers: Authorization, Content-Type, X-CSRF-TOKEN

### CSRF Protection
- ✅ Enabled with CookieCsrfTokenRepository
- ✅ Ignores CSRF for `/settings/auth/**` endpoints
- ✅ Uses HTTP-only cookies

### Spring Security Configuration
- ✅ BCrypt password encoding enabled
- ✅ Form login disabled (REST-only)
- ✅ `/settings/auth/**` endpoints are publicly accessible
- ✅ All other endpoints require authentication

### Database
- **Table**: `iam_users`
- **Password Field**: `password_hash` (BCrypt encoded)
- **Account Status**: `enabled`, `account_non_expired`, `account_non_locked`, `credentials_non_expired`

## Flyway Migrations
- ✅ Enabled and running
- ✅ All migrations executed (V002-V1000)
- ✅ V999 validation disabled (contains SQL statements)
- ✅ iam_users table created and populated

## Testing

### Via curl
```bash
curl -X POST http://localhost:8081/settings/auth/login \
  -H "Content-Type: application/json" \
  -H "Origin: http://localhost:4200" \
  -d '{"username":"admin","password":"admin123"}'
```

### Via Angular Frontend
The authentication service in Angular should use:
- **Base URL**: `http://localhost:8081`
- **Login endpoint**: `/settings/auth/login`
- **Method**: POST
- **Body**: `{username: string, password: string}`
- **Credentials**: `include` (for session cookies)

## Session Management
- ✅ HttpSession-based authentication
- ✅ Session cookie: `JSESSIONID`
- ✅ Security context persisted in session

## Next Steps
1. Test login from Angular frontend at http://localhost:4200
2. Verify session is created and persisted
3. Test dashboard access after successful login
4. Test logout functionality
5. Update other test users with valid password hashes

## Password Hash Details
- **Algorithm**: BCrypt
- **Format**: `$2b$12$...` (BCrypt $2b$ version)
- **Cost**: 12 rounds
- **Each hash is unique** (includes salt)

## Notes
- The password hashes are salted and cannot be recovered
- To set a password for new users, use the `passwordEncoder.encode()` method in Spring Security
- All account status flags (enabled, account_non_expired, etc.) must be `true` for login to succeed
