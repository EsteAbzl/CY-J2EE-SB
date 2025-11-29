#!/bin/bash
# Spring Boot Application Starter for Linux/Mac

echo "Starting CY-J2EE-SB Spring Boot Application..."
echo ""

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "Maven is not installed. Please install Maven first."
    exit 1
fi

# Run the Spring Boot application
echo "Building and starting the application..."
mvn clean spring-boot:run
