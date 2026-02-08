@echo off
setlocal enabledelayedexpansion

REM Wait for database to be ready
echo Waiting for database...
timeout /t 5 /nobreak

REM Run SQL script using psql
echo Updating listing images...
psql -h localhost -p 5444 -U postgres -d agribuy -f update_images.sql

if errorlevel 1 (
    echo Failed to update images
    exit /b 1
)

echo Images updated successfully!
