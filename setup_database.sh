#!/bin/bash
# ============================================================
# AgriBuy Database Setup Script
# Run this to create database and all tables
# ============================================================

echo "🚀 AgriBuy Database Setup"
echo "================================"
echo ""

# Step 1: Create Database
echo "📌 Step 1: Creating database 'agribuy'..."
psql -h localhost -p 5444 -U postgres -c "CREATE DATABASE agribuy ENCODING 'UTF8';"

if [ $? -ne 0 ]; then
    echo "❌ Failed to create database"
    echo "   Possible reasons:"
    echo "   1. Database already exists"
    echo "   2. PostgreSQL not running"
    echo "   3. Wrong credentials"
    exit 1
fi

echo "✅ Database 'agribuy' created successfully!"
echo ""

# Step 2: Create Tables
echo "📌 Step 2: Creating tables..."
psql -h localhost -p 5444 -U postgres -d agribuy -f "SIMPLE_SETUP.sql"

if [ $? -ne 0 ]; then
    echo "❌ Failed to create tables"
    exit 1
fi

echo "✅ All tables created successfully!"
echo ""

# Step 3: Verify
echo "📌 Step 3: Verifying tables..."
echo ""
psql -h localhost -p 5444 -U postgres -d agribuy -c "
SELECT table_name FROM information_schema.tables 
WHERE table_schema = 'public' 
ORDER BY table_name;
"

echo ""
echo "✅ Setup Complete!"
echo ""
echo "📊 Database Information:"
echo "   Database: agribuy"
echo "   Host: localhost"
echo "   Port: 5444"
echo "   User: postgres"
echo ""
echo "🎯 Next Steps:"
echo "   1. Restart your Backend server"
echo "   2. Visit http://localhost:3000/products"
echo "   3. See 8 sample equipment items"
echo ""
echo "🎉 Your AgriBuy marketplace is ready!"
