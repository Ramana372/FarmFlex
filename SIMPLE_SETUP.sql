-- ============================================================
-- COPY & PASTE THESE COMMANDS INTO PostgreSQL
-- ============================================================

-- 1. CREATE DATABASE
CREATE DATABASE agribuy ENCODING 'UTF8';

-- After creating, connect to agribuy:
-- psql -h localhost -p 5444 -U postgres -d agribuy

-- 2. CREATE TABLES (Run all of these)

-- Users table
CREATE TABLE users (id BIGSERIAL PRIMARY KEY, name VARCHAR(255) NOT NULL, email VARCHAR(255) UNIQUE NOT NULL, phone VARCHAR(20), location VARCHAR(255), password VARCHAR(255) NOT NULL, role VARCHAR(50) NOT NULL DEFAULT 'FARMER', email_verified BOOLEAN DEFAULT FALSE, created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP);

-- Listings table
CREATE TABLE listings (id BIGSERIAL PRIMARY KEY, owner_id BIGINT NOT NULL, title VARCHAR(255) NOT NULL, description TEXT, category VARCHAR(100) NOT NULL, type VARCHAR(20) NOT NULL, status VARCHAR(50) NOT NULL DEFAULT 'PENDING', sale_price NUMERIC(12, 2), rent_price_per_day NUMERIC(12, 2), location VARCHAR(255) NOT NULL, rejection_reason TEXT, created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE CASCADE);

-- Listing images table
CREATE TABLE listing_images (id BIGSERIAL PRIMARY KEY, listing_id BIGINT NOT NULL, image_url TEXT NOT NULL, FOREIGN KEY (listing_id) REFERENCES listings(id) ON DELETE CASCADE);

-- Orders table
CREATE TABLE orders (id BIGSERIAL PRIMARY KEY, listing_id BIGINT NOT NULL, buyer_id BIGINT NOT NULL, order_type VARCHAR(20) NOT NULL, quantity INT DEFAULT 1, total_amount NUMERIC(12, 2) NOT NULL, status VARCHAR(50) NOT NULL DEFAULT 'PENDING', start_date TIMESTAMP, end_date TIMESTAMP, delivery_address VARCHAR(500), payment_id VARCHAR(255), created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, FOREIGN KEY (listing_id) REFERENCES listings(id) ON DELETE CASCADE, FOREIGN KEY (buyer_id) REFERENCES users(id) ON DELETE CASCADE);

-- Payments table
CREATE TABLE payments (id BIGSERIAL PRIMARY KEY, order_id BIGINT NOT NULL, amount NUMERIC(12, 2) NOT NULL, payment_method VARCHAR(50), transaction_id VARCHAR(255) UNIQUE, status VARCHAR(50) NOT NULL DEFAULT 'PENDING', created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE);

-- Reviews table
CREATE TABLE reviews (id BIGSERIAL PRIMARY KEY, listing_id BIGINT, reviewer_id BIGINT NOT NULL, rating INT CHECK (rating >= 1 AND rating <= 5), comment TEXT, created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, FOREIGN KEY (listing_id) REFERENCES listings(id) ON DELETE SET NULL, FOREIGN KEY (reviewer_id) REFERENCES users(id) ON DELETE CASCADE);

-- Notifications table
CREATE TABLE notifications (id BIGSERIAL PRIMARY KEY, user_id BIGINT NOT NULL, title VARCHAR(255) NOT NULL, message TEXT, type VARCHAR(50), is_read BOOLEAN DEFAULT FALSE, created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE);

-- 3. CREATE INDEXES for better performance
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_listings_owner ON listings(owner_id);
CREATE INDEX idx_listings_status ON listings(status);
CREATE INDEX idx_listings_type ON listings(type);
CREATE INDEX idx_listings_category ON listings(category);
CREATE INDEX idx_listings_location ON listings(location);
CREATE INDEX idx_listing_images_listing ON listing_images(listing_id);
CREATE INDEX idx_orders_listing ON orders(listing_id);
CREATE INDEX idx_orders_buyer ON orders(buyer_id);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_payments_order ON payments(order_id);
CREATE INDEX idx_payments_status ON payments(status);
CREATE INDEX idx_reviews_listing ON reviews(listing_id);
CREATE INDEX idx_reviews_reviewer ON reviews(reviewer_id);
CREATE INDEX idx_notifications_user ON notifications(user_id);
CREATE INDEX idx_notifications_is_read ON notifications(is_read);

-- 4. VERIFY all tables created
SELECT table_name FROM information_schema.tables WHERE table_schema = 'public' ORDER BY table_name;

-- Should show: notifications, listing_images, listings, orders, payments, reviews, users
