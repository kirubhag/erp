#!/bin/bash

# PMD Analysis Script for Spring Boot ERP Application
# This script runs PMD static code analysis with different options

echo "🔍 PMD Static Code Analysis for Spring Boot ERP Application"
echo "==========================================================="

# Function to display usage
show_usage() {
    echo "Usage: $0 [option]"
    echo ""
    echo "Options:"
    echo "  check     - Run PMD check (default)"
    echo "  report    - Generate PMD reports (HTML, XML)"
    echo "  strict    - Run with failOnViolation=true"
    echo "  help      - Show this help message"
    echo ""
}

# Default option
OPTION=${1:-check}

case $OPTION in
    "check")
        echo "Running PMD check..."
        mvn pmd:check
        ;;
    "report")
        echo "Generating PMD reports..."
        mvn pmd:pmd
        echo ""
        echo "Reports generated in: target/site/pmd/"
        echo "- HTML Report: target/site/pmd/pmd.html"
        echo "- XML Report: target/site/pmd/pmd.xml"
        ;;
    "strict")
        echo "Running PMD with strict validation..."
        mvn pmd:check -Pcode-quality
        ;;
    "help")
        show_usage
        ;;
    *)
        echo "Unknown option: $OPTION"
        show_usage
        exit 1
        ;;
esac

echo ""
echo "PMD Analysis completed!"
