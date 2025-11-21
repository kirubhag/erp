# Reinit Implementation Summary

## Overview
Implemented a complete reinit option for the ERP application that removes the database and restarts everything in development mode **without Docker**.

## Files Created

### 1. **reinit-dev-simple.sh** ⭐ RECOMMENDED
A simple, lightweight reinit script for quick startup.

**Usage:**
```bash
./reinit-dev-simple.sh
```

**What it does:**
1. Kills any running ERP application
2. Cleans and rebuilds the project with Maven
3. Starts the application in development mode (port 8081)

**Prerequisites:**
- MySQL must be running on `localhost:3307`
- Java 21+ installed
- Maven installed

**Perfect for:**
- Daily development work
- Quick restarts during development
- Testing locally

---

### 2. **reinit-dev.sh** (Advanced Version)
A comprehensive reinit script with database reset capabilities.

**Usage:**
```bash
./reinit-dev.sh [mysql-root-password]
./reinit-dev.sh root
```

**What it does:**
1. Stops running application
2. Drops and recreates the database (`erp_database`)
3. Cleans Maven build artifacts
4. Rebuilds the project
5. Starts the application

**Perfect for:**
- Complete database reset
- Troubleshooting database issues
- Full clean slate initialization

---

### 3. **application-dev.properties**
Spring Boot configuration profile for development mode.

**Key Settings:**
```properties
spring.jpa.hibernate.ddl-auto=create-drop
spring.sql.init.mode=always
server.port=8081
spring.datasource.url=jdbc:mysql://localhost:3307/erp_database
```

**Features:**
- Tables are dropped and recreated on each startup (clean state)
- Schema is automatically loaded from `schema.sql`
- Debug logging enabled for troubleshooting
- Running on port 8081

---

### 4. **REINIT_GUIDE.md**
Comprehensive guide covering:
- Quick start instructions
- Manual setup steps
- Troubleshooting guide
- Configuration details
- File references

---

## How It Works

### Development Flow

```
User runs: ./reinit-dev-simple.sh
           ↓
   Kill any running ERP app
           ↓
   Clean: ./mvnw clean
           ↓
   Build: ./mvnw package -DskipTests
           ↓
   Start: ./mvnw spring-boot:run --profile=dev
           ↓
   Application starts on port 8081
           ↓
   Hibernate: Creates tables from entity definitions (ddl-auto=create-drop)
           ↓
   SQL Init: Loads schema.sql
           ↓
   Ready to use! http://localhost:8081
```

### Database Initialization

When the application starts:

1. **Hibernate** reads entity classes and creates/updates tables
2. **Spring SQL Init** runs schema.sql with `sql.init.mode=always`
3. **Data Initializers** (if re-enabled) populate sample data
4. **Application** is ready for requests

**Key Point:** With `ddl-auto=create-drop`, the database starts fresh each time, ensuring:
- No schema conflicts
- Clean state for testing
- All entities properly initialized

---

## Configuration Details

### Application-dev.properties Settings

| Setting | Value | Purpose |
|---------|-------|---------|
| `ddl-auto` | `create-drop` | Drop and recreate tables on startup |
| `sql.init.mode` | `always` | Load schema.sql automatically |
| `server.port` | `8081` | Avoid port conflicts |
| `database.url` | `localhost:3307` | Local MySQL connection |
| `show-sql` | `false` | Don't log every SQL statement |
| `logging.level` | `DEBUG` | Verbose logging for development |

---

## Usage Examples

### Quick Restart During Development
```bash
./reinit-dev-simple.sh
```
Takes ~30 seconds, restarts with fresh database.

### Full Reset with Database
```bash
./reinit-dev.sh root
```
Takes ~2 minutes, completely resets everything.

### Manual Restart (Without Script)
```bash
# Kill app
pkill -f "spring-boot:run"

# Clean rebuild
./mvnw clean package -DskipTests

# Start
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### Check Database Status
```bash
mysql -u root -D erp_database
SHOW TABLES;
DESCRIBE users;
EXIT;
```

### View Application Logs
```bash
# Already displayed in terminal where app is running
# Look for: "Started ErpApplication in X.XXX seconds"
```

---

## Troubleshooting

### MySQL Connection Error
```
ERROR: Connection refused at localhost:3307
```
**Solution:** Start MySQL
```bash
brew services start mysql  # macOS
sudo systemctl start mysql # Linux
```

### Build Failure
```
BUILD FAILURE
```
**Solution:**
```bash
./mvnw clean
./mvnw package -DskipTests
```

### Port Already in Use
```
Port 8081 already in use
```
**Solution:**
```bash
pkill -f "spring-boot:run"
# OR change port in application-dev.properties
```

### Database Doesn't Exist
```
Unknown database 'erp_database'
```
**Solution:**
```bash
mysql -u root
CREATE DATABASE erp_database CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

---

## Development Workflow

### 1. Initial Setup
```bash
# First time
./reinit-dev.sh root
```

### 2. Daily Development
```bash
# Start application
./reinit-dev-simple.sh

# Make code changes (will be auto-reloaded if configured)
# Restart only when needed: Ctrl+C, then run again
```

### 3. Test Database Changes
```bash
# Restart with fresh database
./reinit-dev-simple.sh
```

### 4. Stop Application
```bash
# Press Ctrl+C in the terminal
# OR from another terminal:
pkill -f "spring-boot:run"
```

---

## Key Advantages

✅ **No Docker Required** - Direct local development  
✅ **Fast Startup** - ~30 seconds with reinit-dev-simple.sh  
✅ **Clean State** - Fresh database each time with create-drop  
✅ **Easy Debugging** - Access logs directly in terminal  
✅ **Simple Scripts** - Easy to understand and modify  
✅ **Full Control** - Direct access to MySQL and source code  

---

## Files Modified/Created

### Created:
- `reinit-dev.sh` - Full reinit with database reset
- `reinit-dev-simple.sh` - Quick reinit (recommended)
- `application-dev.properties` - Development configuration
- `REINIT_GUIDE.md` - Comprehensive guide
- This summary file

### Modified:
- `README.md` - Added Quick Start section

---

## Next Steps

1. **Try it out:**
   ```bash
   ./reinit-dev-simple.sh
   ```

2. **Verify it works:**
   - Check http://localhost:8081
   - Check application logs in terminal
   - Verify MySQL: `mysql -u root -D erp_database -e "SHOW TABLES;"`

3. **Customize if needed:**
   - Edit `application-dev.properties` for different port, database name, etc.
   - Edit reinit scripts if different MySQL location

4. **Reference the guide:**
   - See `REINIT_GUIDE.md` for detailed troubleshooting

---

## Summary

The reinit implementation provides:
- **Two ready-to-use scripts** for starting the application in development mode
- **Proper Spring Boot configuration** with development profile
- **Comprehensive documentation** for setup and troubleshooting
- **No Docker dependencies** - Pure local development

Simply run `./reinit-dev-simple.sh` to start developing!
