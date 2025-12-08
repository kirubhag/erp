#!/bin/bash
# ERP Development - Quick Reference

# ============================================
# QUICK COMMANDS (Copy & Paste)
# ============================================

# START (Recommended) - Clean rebuild and start
./reinit-dev-simple.sh

# START (Full Reset) - Reset database and start  
./reinit-dev.sh root

# STOP - Kill running application
pkill -f "spring-boot:run"

# BUILD ONLY - Without starting
./mvnw package -DskipTests

# CHECK DATABASE - List all tables
mysql -u root -D erp_database -e "SHOW TABLES;"

# VIEW TABLE STRUCTURE - Check columns
mysql -u root -D erp_database -e "DESCRIBE users;"

# START MYSQL - If not running
brew services start mysql

# ============================================
# KEY DETAILS
# ============================================

# Application URL:
# http://localhost:8081

# Health Check:
# http://localhost:8081/actuator/health

# Database:
# Host: localhost
# Port: 3307
# Database: erp_database
# User: root
# Password: (empty by default)

# Java Version Required:
# 21 or higher

# Maven Required:
# 3.6 or higher

# ============================================
# TROUBLESHOOTING
# ============================================

# Port already in use?
lsof -i :8081
kill -9 <PID>

# MySQL not running?
brew services start mysql
brew services status mysql

# Build failed?
./mvnw clean
./mvnw package -DskipTests

# Database doesn't exist?
mysql -u root -e "CREATE DATABASE erp_database CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

# ============================================
# FILES YOU'LL USE
# ============================================

# Reinit scripts:
# - reinit-dev-simple.sh (RECOMMENDED)
# - reinit-dev.sh (for full database reset)

# Configuration:
# - src/main/resources/application-dev.properties

# Source code:
# - src/main/java/krs/erp/

# Database schema:
# - src/main/resources/master_schema.sql

# ============================================
# COMMON WORKFLOWS
# ============================================

# DEVELOPMENT (Daily)
./reinit-dev-simple.sh
# Make changes
# Restart when needed

# DATABASE TROUBLESHOOTING
./reinit-dev.sh root
mysql -u root -D erp_database
SHOW TABLES;
EXIT;

# BUILD ONLY (No run)
./mvnw clean package -DskipTests

# ============================================
# DOCUMENTATION
# ============================================

# For detailed guide, see:
# - REINIT_GUIDE.md (comprehensive)
# - REINIT_IMPLEMENTATION.md (technical details)
# - README.md (project overview)

# ============================================
