# ERP Development Reinit Guide

This guide explains how to reinitialize the ERP application for local development.

## Quick Start (Recommended)

### Prerequisites
- MySQL 8.0+ running on `localhost:3307`
- Java 21 installed
- Maven 3.6+ installed

### Option 1: Simple Reinit (Fastest)

```bash
./reinit-dev-simple.sh
```

This script:
1. Kills any running ERP applications
2. Cleans and rebuilds the project
3. Starts the application in development mode on port 8081

**Note:** Make sure MySQL is already running before executing this script.

---

## Detailed Setup

### Option 2: Full Reinit with Database Reset

```bash
./reinit-dev.sh [mysql-root-password]
```

Example:
```bash
./reinit-dev.sh root
```

This script:
1. Stops any running ERP applications
2. Resets the database (drops and recreates)
3. Cleans Maven build artifacts
4. Rebuilds the project
5. Starts the application

---

## Manual Setup (Step-by-Step)

If you prefer to set up manually:

### 1. Ensure MySQL is Running

**macOS (with Homebrew):**
```bash
brew services start mysql
```

**Linux (with systemd):**
```bash
sudo systemctl start mysql
```

**Windows:**
- Start MySQL from Services or MySQL Workbench

### 2. Reset the Database

```bash
# Connect to MySQL
mysql -u root -p

# Execute these commands:
DROP DATABASE IF EXISTS erp_database;
CREATE DATABASE erp_database CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
EXIT;
```

### 3. Build the Application

```bash
cd /path/to/erp
./mvnw clean package -DskipTests
```

### 4. Start the Application

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

The application will:
- Start on `http://localhost:8081`
- Automatically create tables from entity definitions (ddl-auto=create-drop)
- Load schema.sql from `src/main/resources/schema.sql`
- Initialize the database with predefined data

---

## Development Mode Configuration

The development profile (`application-dev.properties`) includes:

- **DDL Mode:** `create-drop` - Tables are recreated on each startup for a clean state
- **SQL Init:** `always` - Loads schema.sql automatically
- **Show SQL:** `false` - SQL statements are not logged (for cleaner output)
- **Logging Level:** `DEBUG` for the ERP application
- **Port:** 8081 (to avoid conflicts with other applications)
- **Database:** `localhost:3307/erp_database`

---

## Troubleshooting

### "Connection refused" Error
**Problem:** Application can't connect to MySQL
**Solution:**
```bash
# Check if MySQL is running
brew services list          # macOS
sudo systemctl status mysql # Linux

# Start MySQL if not running
brew services start mysql
sudo systemctl start mysql
```

### "Unknown database" Error
**Problem:** Database `erp_database` doesn't exist
**Solution:**
```bash
mysql -u root -p
CREATE DATABASE erp_database CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
EXIT;
```

### Build Failures
**Problem:** Maven build fails
**Solution:**
```bash
# Clean all build artifacts
./mvnw clean

# Rebuild
./mvnw package -DskipTests

# If still failing, check Java version
java -version  # Should be 21 or higher
```

### Tables Not Created
**Problem:** Database tables missing after startup
**Solution:**
1. Check the application logs for errors
2. Verify `ddl-auto=create-drop` in application-dev.properties
3. Ensure entities in `src/main/java/krs/erp/model/` are valid
4. Check that `schema.sql` is in `src/main/resources/`

---

## Key Files

- **reinit-dev-simple.sh** - Quick reinit (recommended for most cases)
- **reinit-dev.sh** - Full reinit including database reset
- **application-dev.properties** - Development configuration
- **src/main/resources/schema.sql** - Database schema definition
- **src/main/java/krs/erp/model/** - Entity classes

---

## Stopping the Application

Press `Ctrl+C` in the terminal where the application is running.

To kill any background application:
```bash
pkill -f "java.*ErpApplication"
pkill -f "spring-boot:run"
```

---

## Next Steps

After successful startup:

1. **Access the Application**
   - Web: http://localhost:8081
   - Health Check: http://localhost:8081/actuator/health

2. **Verify Database**
   ```bash
   mysql -u root -D erp_database
   SHOW TABLES;
   DESCRIBE users;  # Check table structure
   ```

3. **Check Logs**
   - Application logs appear in the terminal
   - Look for "Started ErpApplication in X.XXX seconds"

4. **Develop**
   - Make code changes
   - Changes are picked up by Spring Boot (hot reload if configured)
   - For schema changes, restart the application

---

## Profiles

Available Spring profiles:

- **dev** (default) - Development with schema reset
- **docker** - Docker deployment with external MySQL
- **prod** - Production configuration

To use a different profile:
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=docker
```
