@echo off
REM AgriBuy Project Setup and Configuration Script for Windows
REM This script helps set up the entire AgriBuy project locally on Windows

echo.
echo 🌾 Welcome to AgriBuy Setup! 🚜
echo ======================================
echo.

setlocal enabledelayedexpansion

REM Check prerequisites
echo Checking prerequisites...
echo.

REM Check Java
java -version >nul 2>&1
if %errorlevel% equ 0 (
    echo [✓] Java is installed
    for /f "tokens=*" %%i in ('java -version 2^>^&1 ^| findstr /R "version"') do (
        echo   %%i
    )
) else (
    echo [✗] Java is not installed. Please install Java 17 or later.
    echo   Download from: https://www.oracle.com/java/technologies/downloads/
    exit /b 1
)

REM Check Maven
mvn --version >nul 2>&1
if %errorlevel% equ 0 (
    echo [✓] Maven is installed
    for /f "tokens=*" %%i in ('mvn --version ^| findstr /R "version"') do (
        echo   %%i
    )
) else (
    echo [!] Maven not found. Checking in Backend directory...
    if exist "Backend\mvnw.cmd" (
        echo [✓] Maven wrapper found in Backend directory
    ) else (
        echo [✗] Maven is not installed and mvnw not found.
        echo   Install Maven: https://maven.apache.org/download.cgi
        exit /b 1
    )
)

REM Check Node.js
node -v >nul 2>&1
if %errorlevel% equ 0 (
    echo [✓] Node.js is installed
    for /f "tokens=*" %%i in ('node -v') do (
        echo   %%i
    )
) else (
    echo [✗] Node.js is not installed.
    echo   Download from: https://nodejs.org/
    exit /b 1
)

REM Check npm
npm -v >nul 2>&1
if %errorlevel% equ 0 (
    echo [✓] npm is installed
    for /f "tokens=*" %%i in ('npm -v') do (
        echo   %%i
    )
) else (
    echo [✗] npm is not installed.
    exit /b 1
)

REM Check PostgreSQL
psql --version >nul 2>&1
if %errorlevel% equ 0 (
    echo [✓] PostgreSQL client is installed
    for /f "tokens=*" %%i in ('psql --version') do (
        echo   %%i
    )
) else (
    echo [!] PostgreSQL client not found. Ensure PostgreSQL server is running.
    echo   Download from: https://www.postgresql.org/download/
)

echo.
echo ======================================
echo.

REM Database setup
echo 📦 Setting up PostgreSQL Database...
echo.

set /p DB_USER="Enter PostgreSQL username (default: postgres): "
if "%DB_USER%"=="" set DB_USER=postgres

set /p DB_PASSWORD="Enter PostgreSQL password: "

set /p DB_PORT="Enter database port (default: 5432): "
if "%DB_PORT%"=="" set DB_PORT=5432

REM Create database
echo Creating 'agrimart' database...
psql -U %DB_USER% -h localhost -p %DB_PORT% -c "CREATE DATABASE agrimart;" 2>nul

if %errorlevel% equ 0 (
    echo [✓] Database 'agrimart' created successfully
) else (
    echo [!] Database might already exist or connection failed
)

echo.
echo ======================================
echo.

REM Backend configuration
echo ⚙️  Configuring Backend (Spring Boot)...
echo.

if exist "Backend\src\main\resources\application.properties" (
    echo Updating application.properties with database credentials...
    
    REM Create backup
    copy Backend\src\main\resources\application.properties Backend\src\main\resources\application.properties.backup >nul
    
    REM Note: Windows batch file has limited string replacement capability
    REM User should manually update these properties:
    echo.
    echo [!] Please manually update Backend\src\main\resources\application.properties with:
    echo.
    echo spring.datasource.url=jdbc:postgresql://localhost:%DB_PORT%/agrimart
    echo spring.datasource.username=%DB_USER%
    echo spring.datasource.password=%DB_PASSWORD%
    echo.
    
) else (
    echo [✗] application.properties not found
)

echo.
echo ⚠️  Important: Configure Third-Party Services
echo.
echo Edit Backend\src\main\resources\application.properties and set:
echo   1. Razorpay: razorpay.key-id, razorpay.key-secret
echo.
echo Get credentials from:
echo   - Razorpay: https://dashboard.razorpay.com/app/keys
echo.

pause "Press Enter once you've updated the configuration..."

echo.
echo ======================================
echo.

REM Build Backend
echo 🔨 Building Backend (Spring Boot)...
echo.

cd Backend

if exist "mvnw.cmd" (
    call mvnw.cmd clean package -DskipTests
) else (
    call mvn clean package -DskipTests
)

if %errorlevel% equ 0 (
    echo.
    echo [✓] Backend built successfully
) else (
    echo.
    echo [✗] Backend build failed. Check logs above.
    cd ..
    exit /b 1
)

cd ..

echo.
echo ======================================
echo.

REM Frontend setup
echo ⚙️  Configuring Frontend (React)...
echo.

cd Frontend

if not exist "node_modules" (
    echo Installing npm dependencies...
    call npm install
    
    if %errorlevel% equ 0 (
        echo [✓] Frontend dependencies installed
    ) else (
        echo [✗] Frontend dependency installation failed
        cd ..
        exit /b 1
    )
) else (
    echo [✓] Frontend dependencies already installed
)

cd ..

echo.
echo ======================================
echo.
echo ✅ Setup Complete!
echo.
echo 🚀 To start the application:
echo.
echo Backend (Command Prompt 1):
echo   cd Backend
echo   mvnw.cmd spring-boot:run
echo   Server will run on: http://localhost:8090
echo.
echo Frontend (Command Prompt 2):
echo   cd Frontend
echo   npm run dev
echo   App will open on: http://localhost:3000
echo.
echo ======================================
echo.
echo 📚 Documentation:
echo   - Setup Guide: PROJECT_SETUP.md
echo   - API Reference: API_DOCUMENTATION.md
echo   - Backend Tests: cd Backend && mvn test
echo   - Frontend Tests: cd Frontend && npm test
echo.
echo 🌾 Happy farming with AgriBuy! 🚜
echo.

pause
