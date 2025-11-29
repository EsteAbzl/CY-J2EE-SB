#!/bin/bash
# Database Setup Script for CY-J2EE-SB

echo "Setting up MySQL database for CY-J2EE-SB..."
echo ""

# Check if MySQL is installed
if ! command -v mysql &> /dev/null; then
    echo "MySQL is not installed. Please install MySQL first."
    exit 1
fi

# Read database credentials
read -p "Enter MySQL username (default: root): " DB_USER
DB_USER=${DB_USER:-root}

read -sp "Enter MySQL password: " DB_PASSWORD
echo ""

# Create database and tables
echo "Creating database and tables..."
mysql -u "$DB_USER" -p"$DB_PASSWORD" < conception/JeeDb.sql

if [ $? -eq 0 ]; then
    echo ""
    echo "Database setup completed successfully!"
    echo "You can now run the application with: mvn spring-boot:run"
else
    echo "Error: Failed to set up the database."
    exit 1
fi
