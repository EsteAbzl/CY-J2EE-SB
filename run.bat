@echo off
REM Spring Boot Application Starter for Windows

echo Starting CY-J2EE-SB Spring Boot Application...
echo.

REM Check if Maven is installed
where mvn >nul 2>nul
if errorlevel 1 (
    echo Maven is not installed. Installing Maven wrapper...
    mvn -v
)

REM Run the Spring Boot application
echo Building and starting the application...
mvn clean spring-boot:run

pause
