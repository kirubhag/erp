#!/bin/bash

##############################################################################
# ERP Development Reinit Script (Simple Version)
#
# This script reinitializes the ERP application for development mode.
# Prerequisites: MySQL must be running on localhost:3307
#
# Usage: ./reinit-dev-simple.sh
##############################################################################

set -e  # Exit on error

# Color output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}ERP Development Reinit (Simple)${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""

# Step 1: Kill any running application
echo -e "${YELLOW}[1/3] Stopping any running application...${NC}"
pkill -f "java.*ErpApplication\|java.*spring-boot:run" || true
sleep 1
echo -e "${GREEN}✓ Done${NC}"
echo ""

# Step 2: Clean and rebuild
echo -e "${YELLOW}[2/3] Cleaning and rebuilding...${NC}"
cd "${SCRIPT_DIR}"
# Remove problematic node_modules if they exist (can cause Maven clean to fail)
sudo rm -rf target/classes/static/angular/node_modules 2>/dev/null || rm -rf target/classes/static/angular/node_modules 2>/dev/null || true
./mvnw clean package -DskipTests -q
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Build successful${NC}"
else
    echo -e "${RED}✗ Build failed${NC}"
    exit 1
fi
echo ""

# Step 3: Start with dev profile
echo -e "${YELLOW}[3/3] Starting application...${NC}"
echo ""
echo -e "${BLUE}========================================${NC}"
echo -e "${GREEN}Application starting...${NC}"
echo -e "${BLUE}Profile: dev${NC}"
echo -e "${BLUE}Port: 8081${NC}"
echo -e "${BLUE}Database: localhost:3307/erp_database${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""
echo "Press Ctrl+C to stop"
echo ""

./mvnw spring-boot:run \
  -Dspring-boot.run.profiles=dev \
  -Dspring-boot.run.arguments="--server.port=8081"
