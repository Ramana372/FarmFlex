-- ============================================================
-- FARMFLEX SEED DATA - Sample Listings & Products
-- ============================================================

-- 0. CLEAN UP EXISTING TEST DATA (respecting FK constraints)
DELETE FROM listing_images WHERE listing_id >= 100;
DELETE FROM listings WHERE id >= 100;

-- 1. NOTE: Using existing users (IDs 1-5) from database, not creating new ones
-- Users already exist: ID 1-5 are FARMER/BUYER accounts from initial setup

-- 2. INSERT SAMPLE LISTINGS (For Sale) - Using high IDs to avoid conflicts, referencing existing users (IDs 1-5)
INSERT INTO listings (id, owner_id, title, description, category, type, status, sale_price, location, created_at) VALUES
(100, 1, 'Premium Quality Wheat', 'High-quality wheat grown with organic farming methods. Suitable for commercial grain mills.', 'GRAINS', 'SALE', 'APPROVED', 2500.00, 'Maharashtra', NOW()),
(101, 2, 'Fresh Organic Vegetables Bundle', 'Mixed bundle of fresh vegetables - tomatoes, potatoes, onions, carrots. Harvested daily.', 'VEGETABLES', 'SALE', 'APPROVED', 1500.00, 'Punjab', NOW()),
(102, 3, 'John Deere Tractor', 'John Deere Tractor in excellent condition. Recently serviced. Complete with implements.', 'EQUIPMENT', 'RENT', 'APPROVED', 5000.00, 'Haryana', NOW()),
(103, 1, 'Sugarcane - Bulk Quantity', 'Fresh sugarcane for processing or direct consumption. Sweet variety. Bulk orders welcomed.', 'GRAINS', 'SALE', 'APPROVED', 3500.00, 'Maharashtra', NOW()),
(104, 4, 'Hydraulic Press Machine', 'Industrial hydraulic press for oil extraction. Well-maintained, ready for immediate use.', 'EQUIPMENT', 'RENT', 'APPROVED', 8000.00, 'Uttar Pradesh', NOW()),
(105, 2, 'Chicken Layer Birds', 'High-yield layer birds for poultry farming. Healthy birds, good feather condition. Min order 50.', 'LIVESTOCK', 'SALE', 'APPROVED', 800.00, 'Punjab', NOW()),
(106, 3, 'Maize Seeds - Hybrid Variety', 'Premium hybrid maize seeds with 95% germination rate. Certified and tested.', 'SEEDS', 'SALE', 'APPROVED', 1200.00, 'Haryana', NOW()),
(107, 1, 'Cotton Bales - Grade A', 'Premium grade cotton bales, bleached and cleaned. Ready for textile industry.', 'GRAINS', 'SALE', 'APPROVED', 4500.00, 'Maharashtra', NOW());

-- 3. INSERT LISTING IMAGES
INSERT INTO listing_images (listing_id, image_url) VALUES
(100, '/uploads/images/Advanced Crop Sprayer.jpg'),
(101, '/uploads/images/John Deere Tractor 53.avif'),
(102, '/uploads/images/Mahindra_Harvester_PRO.jpg'),
(103, '/uploads/images/Sonalika DI 60 Tractor.webp'),
(104, '/uploads/images/Used Agricultural Plough.webp'),
(105, '/uploads/images/Electric Seeder Machine.jpg'),
(106, '/uploads/images/Advanced Crop Sprayer.jpg'),
(107, '/uploads/images/Electric Seeder Machine.jpg');

-- 4. INSERT SAMPLE FAVORIES
-- These will be added by users as they browse

-- 5. VERIFY DATA
SELECT COUNT(*) as total_users FROM users;
SELECT COUNT(*) as total_listings FROM listings;
SELECT COUNT(*) as approved_listings FROM listings WHERE status = 'APPROVED';

-- Display sample data
SELECT id, title, category, type, sale_price, location, status FROM listings ORDER BY created_at DESC;
