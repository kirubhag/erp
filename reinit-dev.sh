#!/bin/bash

##############################################################################
# ERP Development Reinit Script
# 
# This script reinitializes the ERP application for development mode:
# 1. Stops any running application instances
# 2. Removes the development database and recreates it
# 3. Cleans Maven build artifacts
# 4. Rebuilds the application
# 5. Starts the application in development mode
#
# Usage: ./reinit-dev.sh [mysql-root-password]
# Example: ./reinit-dev.sh mypassword
##############################################################################

set -e  # Exit on error

# Color output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
DB_NAME="erp_database"
DB_USER="root"
DB_PORT="3307"
MYSQL_PASSWORD="${1:---root-password-not-provided--}"

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}ERP Development Reinit Script${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""

# Step 1: Stop any running application
echo -e "${YELLOW}[1/5] Checking for running application...${NC}"
if pgrep -f "java.*erp" > /dev/null; then
    echo "Found running ERP application. Attempting to stop..."
    pkill -f "java.*erp" || true
    sleep 2
    echo -e "${GREEN}✓ Application stopped${NC}"
else
    echo -e "${GREEN}✓ No running application found${NC}"
fi
echo ""

# Step 2: Remove and recreate database
echo -e "${YELLOW}[2/5] Resetting database (${DB_NAME})...${NC}"

# Check if MySQL is running
if ! command -v mysql &> /dev/null; then
    echo -e "${RED}✗ MySQL client not found. Please install mysql-client.${NC}"
    echo "  On macOS: brew install mysql-client"
    echo "  On Ubuntu/Debian: sudo apt-get install mysql-client"
    exit 1
fi

# Try to connect to MySQL and recreate database
if mysql -u${DB_USER} -p${MYSQL_PASSWORD} -h 127.0.0.1 -P ${DB_PORT} -e "SELECT 1" &> /dev/null; then
    echo "Connected to MySQL. Dropping and recreating database..."
    mysql -u${DB_USER} -p${MYSQL_PASSWORD} -h 127.0.0.1 -P ${DB_PORT} << EOF
DROP DATABASE IF EXISTS ${DB_NAME};
CREATE DATABASE ${DB_NAME} CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
EOF
    echo -e "${GREEN}✓ Database reset complete${NC}"
else
    echo -e "${RED}✗ Could not connect to MySQL${NC}"
    echo "  Make sure MySQL is running on localhost:${DB_PORT}"
    echo "  Check your connection credentials"
    exit 1
fi
echo ""

# Step 3: Clean Maven
echo -e "${YELLOW}[3/5] Cleaning Maven build artifacts...${NC}"
cd "${SCRIPT_DIR}"
./mvnw clean -q
echo -e "${GREEN}✓ Maven clean complete${NC}"
echo ""

# Step 4: Build application
echo -e "${YELLOW}[4/5] Building application...${NC}"
./mvnw package -DskipTests -q
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Build successful${NC}"
else
    echo -e "${RED}✗ Build failed${NC}"
    exit 1
fi
echo ""

# Step 5: Start application
echo -e "${YELLOW}[5/5] Starting application in development mode...${NC}"
echo ""
echo -e "${GREEN}Starting on port 8081...${NC}"
echo -e "${GREEN}Access the application at: http://localhost:8081${NC}"
echo ""
echo "Press Ctrl+C to stop the application"
echo ""

# Start the application with development profile
./mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev" &
APP_PID=$!

# Wait a moment for the app to start
sleep 3

# Check if app is running
if kill -0 $APP_PID 2>/dev/null; then
    echo -e "${GREEN}✓ Application started (PID: $APP_PID)${NC}"
    echo ""
    wait $APP_PID
else
    echo -e "${RED}✗ Application failed to start${NC}"
    exit 1
fi
