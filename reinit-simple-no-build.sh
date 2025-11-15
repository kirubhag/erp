#!/bin/bash

##############################################################################
# ERP Simple Reinit - Just DB Reset + Start (No Build)
#
# Prerequisites: 
# - Application already built (target/erp-*.war exists)
# - MySQL running on localhost:3307
#
# Usage: ./reinit-simple-no-build.sh
##############################################################################

set -e

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

echo -e "${BLUE}════════════════════════════════════════════════════════${NC}"
echo -e "${BLUE}  ERP Simple Reinit (DB Reset + Start)${NC}"
echo -e "${BLUE}════════════════════════════════════════════════════════${NC}"
echo ""

# Step 1: Kill running app
echo -e "${YELLOW}[1/3] Stopping running application...${NC}"
pkill -f "spring-boot:run\|java.*ErpApplication" 2>/dev/null || true
sleep 2
echo -e "${GREEN}✓ Done${NC}"
echo ""

# Step 2: Reset database
echo -e "${YELLOW}[2/3] Resetting database...${NC}"
if mysql -u root -h 127.0.0.1 -P 3307 -e "SELECT 1" &>/dev/null; then
    echo "Dropping database..."
    mysql -u root -h 127.0.0.1 -P 3307 -e "DROP DATABASE IF EXISTS erp_database;" 2>/dev/null || true
    sleep 1
    echo "Creating database..."
    mysql -u root -h 127.0.0.1 -P 3307 -e "CREATE DATABASE erp_database CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;" 2>/dev/null
    echo -e "${GREEN}✓ Database reset complete${NC}"
else
    echo -e "${RED}✗ Could not connect to MySQL on localhost:3307${NC}"
    echo "Make sure MySQL is running: brew services start mysql"
    exit 1
fi
echo ""

# Step 3: Start application
echo -e "${YELLOW}[3/3] Starting application with dev profile...${NC}"
echo ""
echo -e "${BLUE}════════════════════════════════════════════════════════${NC}"
echo -e "${GREEN}Starting ERP Application${NC}"
echo -e "${BLUE}Profile: dev${NC}"
echo -e "${BLUE}Port: 8081${NC}"
echo -e "${BLUE}Database: localhost:3307/erp_database${NC}"
echo -e "${BLUE}════════════════════════════════════════════════════════${NC}"
echo ""
echo "Waiting for startup..."
echo ""

cd "${SCRIPT_DIR}"
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev -q
