-- ============================================================
-- AgriBuy Database Setup Script
-- Creates database and all required tables
-- ============================================================

-- 1. CREATE DATABASE
CREATE DATABASE agribuy
  WITH ENCODING 'UTF8'
  LOCALE_PROVIDER 'libc'
  COLLATE 'English_India.1252'
  CTYPE 'English_India.1252';

-- 2. CONNECT TO THE NEW DATABASE
\c agribuy

-- ============================================================
-- TABLE 1: users (Farmers, Sellers, Admins)
-- ============================================================
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    phone VARCHAR(20),
    location VARCHAR(255),
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL DEFAULT 'FARMER', -- FARMER, ADMIN, BUYER
    email_verified BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT role_check CHECK (role IN ('FARMER', 'ADMIN', 'BUYER'))
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);

-- ============================================================
-- TABLE 2: listings (Agricultural Equipment)
-- ============================================================
CREATE TABLE IF NOT EXISTS listings (
    id BIGSERIAL PRIMARY KEY,
    owner_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    category VARCHAR(100) NOT NULL,
    type VARCHAR(20) NOT NULL, -- RENT or SALE
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING', -- PENDING, LIVE, REJECTED, SOLD, BOOKED
    sale_price NUMERIC(12, 2),
    rent_price_per_day NUMERIC(12, 2),
    location VARCHAR(255) NOT NULL,
    rejection_reason TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_listings_owner FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT type_check CHECK (type IN ('RENT', 'SALE')),
    CONSTRAINT status_check CHECK (status IN ('PENDING', 'LIVE', 'REJECTED', 'SOLD', 'BOOKED')),
    CONSTRAINT category_check CHECK (category IN ('TRACTOR', 'HARVESTER', 'PLOUGH', 'SEEDER', 'SPRAYER', 'THRESHER', 'OTHER'))
);

CREATE INDEX idx_listings_owner ON listings(owner_id);
CREATE INDEX idx_listings_status ON listings(status);
CREATE INDEX idx_listings_type ON listings(type);
CREATE INDEX idx_listings_category ON listings(category);
CREATE INDEX idx_listings_location ON listings(location);

-- ============================================================
-- TABLE 3: listing_images (Product Images)
-- ============================================================
CREATE TABLE IF NOT EXISTS listing_images (
    id BIGSERIAL PRIMARY KEY,
    listing_id BIGINT NOT NULL,
    image_url TEXT NOT NULL,
    CONSTRAINT fk_listing_images_listing FOREIGN KEY (listing_id) REFERENCES listings(id) ON DELETE CASCADE
);

CREATE INDEX idx_listing_images_listing ON listing_images(listing_id);

-- ============================================================
-- TABLE 4: orders (Rental/Purchase Orders)
-- ============================================================
CREATE TABLE IF NOT EXISTS orders (
    id BIGSERIAL PRIMARY KEY,
    listing_id BIGINT NOT NULL,
    buyer_id BIGINT NOT NULL,
    order_type VARCHAR(20) NOT NULL, -- RENT or PURCHASE
    quantity INT DEFAULT 1,
    total_amount NUMERIC(12, 2) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING', -- PENDING, CONFIRMED, DELIVERED, COMPLETED, CANCELLED
    start_date TIMESTAMP,
    end_date TIMESTAMP,
    delivery_address VARCHAR(500),
    payment_id VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_orders_listing FOREIGN KEY (listing_id) REFERENCES listings(id) ON DELETE CASCADE,
    CONSTRAINT fk_orders_buyer FOREIGN KEY (buyer_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT order_type_check CHECK (order_type IN ('RENT', 'PURCHASE')),
    CONSTRAINT order_status_check CHECK (status IN ('PENDING', 'CONFIRMED', 'DELIVERED', 'COMPLETED', 'CANCELLED'))
);

CREATE INDEX idx_orders_listing ON orders(listing_id);
CREATE INDEX idx_orders_buyer ON orders(buyer_id);
CREATE INDEX idx_orders_status ON orders(status);

-- ============================================================
-- TABLE 5: payments (Payment Records)
-- ============================================================
CREATE TABLE IF NOT EXISTS payments (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL,
    amount NUMERIC(12, 2) NOT NULL,
    payment_method VARCHAR(50), -- RAZORPAY, BANK_TRANSFER, etc.
    transaction_id VARCHAR(255) UNIQUE,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING', -- PENDING, SUCCESS, FAILED
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_payments_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    CONSTRAINT payment_status_check CHECK (status IN ('PENDING', 'SUCCESS', 'FAILED'))
);

CREATE INDEX idx_payments_order ON payments(order_id);
CREATE INDEX idx_payments_status ON payments(status);

-- ============================================================
-- TABLE 6: reviews (User Reviews & Ratings)
-- ============================================================
CREATE TABLE IF NOT EXISTS reviews (
    id BIGSERIAL PRIMARY KEY,
    listing_id BIGINT,
    reviewer_id BIGINT NOT NULL,
    rating INT CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_reviews_listing FOREIGN KEY (listing_id) REFERENCES listings(id) ON DELETE SET NULL,
    CONSTRAINT fk_reviews_reviewer FOREIGN KEY (reviewer_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_reviews_listing ON reviews(listing_id);
CREATE INDEX idx_reviews_reviewer ON reviews(reviewer_id);

-- ============================================================
-- TABLE 7: notifications (User Notifications)
-- ============================================================
CREATE TABLE IF NOT EXISTS notifications (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT,
    type VARCHAR(50), -- ORDER, LISTING, PAYMENT, etc.
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notifications_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_notifications_user ON notifications(user_id);
CREATE INDEX idx_notifications_is_read ON notifications(is_read);

-- ============================================================
-- SUMMARY
-- ============================================================
-- Database: agribuy
-- Tables Created:
--   1. users (Farmers, Admins, Buyers)
--   2. listings (Agricultural Equipment)
--   3. listing_images (Product Images)
--   4. orders (Rental/Purchase Orders)
--   5. payments (Payment Records)
--   6. reviews (User Reviews & Ratings)
--   7. notifications (User Notifications)
-- ============================================================

-- Verify tables
\dt

-- Check table details
\d+ users
\d+ listings
\d+ listing_images
\d+ orders
\d+ payments
\d+ reviews
\d+ notifications
