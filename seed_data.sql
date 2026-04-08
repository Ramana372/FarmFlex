-- ============================================================
-- FARMFLEX SEED DATA - Sample Listings & Products
-- ============================================================

-- 1. INSERT SAMPLE USERS (Farmers & Buyers)
INSERT INTO users (id, name, email, phone, location, password, role, email_verified, created_at) VALUES
(1, 'Rajesh Patel', 'rajesh@example.com', '9876543210', 'Maharashtra', '$2a$10$8f5e1c3b2a9d8e7f4c3b2a9d8e7f4c3b2a9d8e7f4c3b2a9d8e7f', 'FARMER', true, NOW()),
(2, 'Priya Singh', 'priya@example.com', '9876543211', 'Punjab', '$2a$10$8f5e1c3b2a9d8e7f4c3b2a9d8e7f4c3b2a9d8e7f4c3b2a9d8e7f', 'FARMER', true, NOW()),
(3, 'Ramesh Kumar', 'ramesh@example.com', '9876543212', 'Haryana', '$2a$10$8f5e1c3b2a9d8e7f4c3b2a9d8e7f4c3b2a9d8e7f4c3b2a9d8e7f', 'FARMER', true, NOW()),
(4, 'Sunita Gupta', 'sunita@example.com', '9876543213', 'Uttar Pradesh', '$2a$10$8f5e1c3b2a9d8e7f4c3b2a9d8e7f4c3b2a9d8e7f4c3b2a9d8e7f', 'FARMER', true, NOW()),
(5, 'Amit Verma', 'amit@example.com', '9876543214', 'Karnataka', '$2a$10$8f5e1c3b2a9d8e7f4c3b2a9d8e7f4c3b2a9d8e7f4c3b2a9d8e7f', 'BUYER', true, NOW()),
(6, 'Neha Sharma', 'neha@example.com', '9876543215', 'Delhi', '$2a$10$8f5e1c3b2a9d8e7f4c3b2a9d8e7f4c3b2a9d8e7f4c3b2a9d8e7f', 'BUYER', true, NOW());

-- 2. INSERT SAMPLE LISTINGS (For Sale)
INSERT INTO listings (id, owner_id, title, description, category, type, status, sale_price, location, created_at) VALUES
(1, 1, 'Premium Quality Wheat', 'High-quality wheat grown with organic farming methods. Suitable for commercial grain mills.', 'GRAINS', 'SALE', 'APPROVED', 2500.00, 'Maharashtra', NOW()),
(2, 2, 'Fresh Organic Vegetables Bundle', 'Mixed bundle of fresh vegetables - tomatoes, potatoes, onions, carrots. Harvested daily.', 'VEGETABLES', 'SALE', 'APPROVED', 1500.00, 'Punjab', NOW()),
(3, 3, 'John Deere Tractor', 'John Deere Tractor in excellent condition. Recently serviced. Complete with implements.', 'EQUIPMENT', 'RENT', 'APPROVED', 5000.00, 'Haryana', NOW()),
(4, 1, 'Sugarcane - Bulk Quantity', 'Fresh sugarcane for processing or direct consumption. Sweet variety. Bulk orders welcomed.', 'GRAINS', 'SALE', 'APPROVED', 3500.00, 'Maharashtra', NOW()),
(5, 4, 'Hydraulic Press Machine', 'Industrial hydraulic press for oil extraction. Well-maintained, ready for immediate use.', 'EQUIPMENT', 'RENT', 'APPROVED', 8000.00, 'Uttar Pradesh', NOW()),
(6, 2, 'Chicken Layer Birds', 'High-yield layer birds for poultry farming. Healthy birds, good feather condition. Min order 50.', 'LIVESTOCK', 'SALE', 'APPROVED', 800.00, 'Punjab', NOW()),
(7, 3, 'Maize Seeds - Hybrid Variety', 'Premium hybrid maize seeds with 95% germination rate. Certified and tested.', 'SEEDS', 'SALE', 'APPROVED', 1200.00, 'Haryana', NOW()),
(8, 1, 'Cotton Bales - Grade A', 'Premium grade cotton bales, bleached and cleaned. Ready for textile industry.', 'GRAINS', 'SALE', 'APPROVED', 4500.00, 'Maharashtra', NOW());

-- 3. INSERT LISTING IMAGES
INSERT INTO listing_images (listing_id, image_url) VALUES
(1, '/api/images/wheat-1.jpg'),
(2, '/api/images/vegetables-1.jpg'),
(3, '/api/images/tractor-1.jpg'),
(4, '/api/images/sugarcane-1.jpg'),
(5, '/api/images/press-1.jpg'),
(6, '/api/images/chicken-1.jpg'),
(7, '/api/images/seeds-1.jpg'),
(8, '/api/images/cotton-1.jpg');

-- 4. INSERT SAMPLE FAVORIES
-- These will be added by users as they browse

-- 5. VERIFY DATA
SELECT COUNT(*) as total_users FROM users;
SELECT COUNT(*) as total_listings FROM listings;
SELECT COUNT(*) as approved_listings FROM listings WHERE status = 'APPROVED';

-- Display sample data
SELECT id, title, category, type, sale_price, location, status FROM listings ORDER BY created_at DESC;
