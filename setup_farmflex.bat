@echo off
REM ============================================================
REM FarmFlex Database Setup Script
REM ============================================================
REM This script sets up the PostgreSQL database for FarmFlex

setlocal enabledelayedexpansion

REM Database configuration
set DB_HOST=localhost
set DB_PORT=5444
set DB_USER=postgres
set DB_NAME=farmflex

REM Check if psql is available
where psql >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo Error: PostgreSQL is not installed or not in PATH
    echo Please install PostgreSQL and add it to PATH
    pause
    exit /b 1
)

echo.
echo ============================================================
echo FarmFlex Database Setup
echo ============================================================
echo.
echo Database Configuration:
echo Host: %DB_HOST%
echo Port: %DB_PORT%
echo User: %DB_USER%
echo Database: %DB_NAME%
echo.

REM Create database if it doesn't exist
echo [1/3] Creating database...
psql -h %DB_HOST% -p %DB_PORT% -U %DB_USER% -tc "SELECT 1 FROM pg_database WHERE datname = '%DB_NAME%'" | findstr /C:"1" >nul
if %ERRORLEVEL% NEQ 0 (
    echo      Creating new database '%DB_NAME%'...
    psql -h %DB_HOST% -p %DB_PORT% -U %DB_USER% -c "CREATE DATABASE %DB_NAME% ENCODING 'UTF8';"
    if %ERRORLEVEL% NEQ 0 (
        echo Error: Failed to create database
        pause
        exit /b 1
    )
    echo      Database created successfully!
) else (
    echo      Database '%DB_NAME%' already exists
)

REM Create tables
echo [2/3] Creating tables...
psql -h %DB_HOST% -p %DB_PORT% -U %DB_USER% -d %DB_NAME% -f SIMPLE_SETUP.sql
if %ERRORLEVEL% NEQ 0 (
    echo Error: Failed to create tables
    pause
    exit /b 1
)
echo      Tables created successfully!

REM Load seed data
echo [3/3] Loading sample data...
psql -h %DB_HOST% -p %DB_PORT% -U %DB_USER% -d %DB_NAME% -f seed_data.sql
if %ERRORLEVEL% NEQ 0 (
    echo Warning: Some errors occurred while loading seed data
) else (
    echo      Sample data loaded successfully!
)

echo.
echo ============================================================
echo Setup Complete!
echo ============================================================
echo.
echo Your FarmFlex database is ready to use.
echo.
pause
