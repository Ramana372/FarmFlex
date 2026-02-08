#!/bin/bash

# AgriBuy Project Setup and Configuration Script
# This script helps set up the entire AgriBuy project locally

echo "🌾 Welcome to AgriBuy Setup! 🚜"
echo "======================================"
echo ""

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Check if running on Windows
if [[ "$OSTYPE" == "msys" || "$OSTYPE" == "win32" ]]; then
  IS_WINDOWS=true
else
  IS_WINDOWS=false
fi

# Function to print colored output
print_status() {
  echo -e "${GREEN}[✓]${NC} $1"
}

print_error() {
  echo -e "${RED}[✗]${NC} $1"
}

print_warning() {
  echo -e "${YELLOW}[!]${NC} $1"
}

# Check prerequisites
echo "Checking prerequisites..."
echo ""

# Check Java
if command -v java &> /dev/null; then
  JAVA_VERSION=$(java -version 2>&1 | grep -oP '(?<=version ")(?:\d+\.)*\d+' | head -1)
  print_status "Java is installed (version: $JAVA_VERSION)"
else
  print_error "Java is not installed. Please install Java 17 or later."
  echo "  Download from: https://www.oracle.com/java/technologies/downloads/"
  exit 1
fi

# Check Maven
if command -v mvn &> /dev/null; then
  MVN_VERSION=$(mvn --version | head -1)
  print_status "$MVN_VERSION"
else
  print_warning "Maven not found. Checking in Backend directory..."
  if [ -f "Backend/mvnw" ]; then
    print_status "Maven wrapper found in Backend directory"
  else
    print_error "Maven is not installed and mvnw not found."
    echo "  Install Maven: https://maven.apache.org/download.cgi"
    exit 1
  fi
fi

# Check Node.js
if command -v node &> /dev/null; then
  NODE_VERSION=$(node -v)
  print_status "Node.js is installed ($NODE_VERSION)"
else
  print_error "Node.js is not installed."
  echo "  Download from: https://nodejs.org/"
  exit 1
fi

# Check npm
if command -v npm &> /dev/null; then
  NPM_VERSION=$(npm -v)
  print_status "npm is installed (version: $NPM_VERSION)"
else
  print_error "npm is not installed."
  exit 1
fi

# Check PostgreSQL
if command -v psql &> /dev/null; then
  PSQL_VERSION=$(psql --version)
  print_status "$PSQL_VERSION"
else
  print_warning "PostgreSQL client not found. Ensure PostgreSQL server is running."
  echo "  Download from: https://www.postgresql.org/download/"
fi

echo ""
echo "======================================"
echo ""

# Database setup
echo "📦 Setting up PostgreSQL Database..."
echo ""

read -p "Enter PostgreSQL username (default: postgres): " DB_USER
DB_USER=${DB_USER:-postgres}

read -sp "Enter PostgreSQL password: " DB_PASSWORD
echo ""

read -p "Enter database port (default: 5432): " DB_PORT
DB_PORT=${DB_PORT:-5432}

# Create database
echo "Creating 'agrimart' database..."
if [[ "$IS_WINDOWS" == true ]]; then
  psql -U $DB_USER -h localhost -p $DB_PORT -c "CREATE DATABASE agrimart;" 2>/dev/null
else
  PGPASSWORD=$DB_PASSWORD psql -U $DB_USER -h localhost -p $DB_PORT -c "CREATE DATABASE agrimart;" 2>/dev/null
fi

if [ $? -eq 0 ]; then
  print_status "Database 'agrimart' created successfully"
else
  print_warning "Database might already exist or connection failed"
fi

echo ""
echo "======================================"
echo ""

# Backend configuration
echo "⚙️  Configuring Backend (Spring Boot)..."
echo ""

if [ -f "Backend/src/main/resources/application.properties" ]; then
  echo "Updating application.properties with database credentials..."
  
  # Create backup
  cp Backend/src/main/resources/application.properties Backend/src/main/resources/application.properties.backup
  
  # Update database URL
  if [[ "$IS_WINDOWS" == true ]]; then
    sed -i "s/spring.datasource.url=.*/spring.datasource.url=jdbc:postgresql:\/\/localhost:$DB_PORT\/agrimart/" Backend/src/main/resources/application.properties
    sed -i "s/spring.datasource.username=.*/spring.datasource.username=$DB_USER/" Backend/src/main/resources/application.properties
    sed -i "s/spring.datasource.password=.*/spring.datasource.password=$DB_PASSWORD/" Backend/src/main/resources/application.properties
  else
    sed -i "s|spring.datasource.url=.*|spring.datasource.url=jdbc:postgresql://localhost:$DB_PORT/agrimart|" Backend/src/main/resources/application.properties
    sed -i "s|spring.datasource.username=.*|spring.datasource.username=$DB_USER|" Backend/src/main/resources/application.properties
    sed -i "s|spring.datasource.password=.*|spring.datasource.password=$DB_PASSWORD|" Backend/src/main/resources/application.properties
  fi
  
  print_status "application.properties updated"
else
  print_error "application.properties not found"
fi

echo ""
echo "⚠️  Important: Configure Third-Party Services"
echo ""
echo "Edit Backend/src/main/resources/application.properties and set:"
echo "  1. Razorpay: razorpay.key-id, razorpay.key-secret"
echo ""
echo "Get credentials from:"
echo "  - Razorpay: https://dashboard.razorpay.com/app/keys"
echo ""

read -p "Press Enter once you've updated the configuration..."

echo ""
echo "======================================"
echo ""

# Build Backend
echo "🔨 Building Backend (Spring Boot)..."
echo ""

cd Backend

if [ -f "mvnw" ]; then
  if [[ "$IS_WINDOWS" == true ]]; then
    ./mvnw.cmd clean package -DskipTests
  else
    chmod +x mvnw
    ./mvnw clean package -DskipTests
  fi
else
  mvn clean package -DskipTests
fi

if [ $? -eq 0 ]; then
  print_status "Backend built successfully"
else
  print_error "Backend build failed. Check logs above."
  exit 1
fi

cd ..

echo ""
echo "======================================"
echo ""

# Frontend setup
echo "⚙️  Configuring Frontend (React)..."
echo ""

cd Frontend

if [ ! -d "node_modules" ]; then
  echo "Installing npm dependencies..."
  npm install
  
  if [ $? -eq 0 ]; then
    print_status "Frontend dependencies installed"
  else
    print_error "Frontend dependency installation failed"
    exit 1
  fi
else
  print_status "Frontend dependencies already installed"
fi

cd ..

echo ""
echo "======================================"
echo ""
echo "✅ Setup Complete!"
echo ""
echo "🚀 To start the application:"
echo ""
echo "Backend (Terminal 1):"
echo "  cd Backend"
if [[ "$IS_WINDOWS" == true ]]; then
  echo "  mvnw.cmd spring-boot:run"
else
  echo "  ./mvnw spring-boot:run"
fi
echo "  Server will run on: http://localhost:8090"
echo ""
echo "Frontend (Terminal 2):"
echo "  cd Frontend"
echo "  npm run dev"
echo "  App will open on: http://localhost:3000"
echo ""
echo "======================================"
echo ""
echo "📚 Documentation:"
echo "  - Setup Guide: ./PROJECT_SETUP.md"
echo "  - API Reference: ./API_DOCUMENTATION.md"
echo "  - Backend Tests: cd Backend && mvn test"
echo "  - Frontend Tests: cd Frontend && npm test"
echo ""
echo "🌾 Happy farming with AgriBuy! 🚜"
