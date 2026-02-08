-- AgriMart Database Population Script
-- This script adds sample users, listings, and other data to the agribuy database

-- Insert Users (ADMIN and FARMERS)
INSERT INTO users (name, email, phone, location, password, role, email_verified, created_at, updated_at) VALUES
('Admin User', 'admin@agribuy.com', '9999999999', 'India', '$2a$10$slYQmyNdGzin7olVnH/1OPST9/PgBkqquzi8Ay0IQI7tEMYyejVDm', 'ADMIN', true, NOW(), NOW()),
('Rajesh Kumar', 'rajesh@agribuy.com', '9876543210', 'Maharashtra', '$2a$10$slYQmyNdGzin7olVnH/1OPST9/PgBkqquzi8Ay0IQI7tEMYyejVDm', 'FARMER', true, NOW(), NOW()),
('Priya Singh', 'priya@agribuy.com', '9876543211', 'Punjab', '$2a$10$slYQmyNdGzin7olVnH/1OPST9/PgBkqquzi8Ay0IQI7tEMYyejVDm', 'FARMER', true, NOW(), NOW()),
('Arun Patel', 'arun@agribuy.com', '9876543212', 'Gujarat', '$2a$10$slYQmyNdGzin7olVnH/1OPST9/PgBkqquzi8Ay0IQI7tEMYyejVDm', 'FARMER', true, NOW(), NOW()),
('Suresh Verma', 'suresh@agribuy.com', '9876543213', 'Uttar Pradesh', '$2a$10$slYQmyNdGzin7olVnH/1OPST9/PgBkqquzi8Ay0IQI7tEMYyejVDm', 'FARMER', true, NOW(), NOW()),
('Meera Gupta', 'meera@agribuy.com', '9876543214', 'Haryana', '$2a$10$slYQmyNdGzin7olVnH/1OPST9/PgBkqquzi8Ay0IQI7tEMYyejVDm', 'FARMER', true, NOW(), NOW());

-- Insert RENT Listings (For Rajesh Kumar - user_id 2)
INSERT INTO listings (title, description, category, type, status, rent_price_per_day, location, owner_id, created_at, updated_at) VALUES
('John Deere Tractor 5050D', 'Well-maintained 50 HP tractor, ideal for ploughing and farming. Recently serviced with full documentation.', 'TRACTOR', 'RENT', 'LIVE', 800, 'Nashik, Maharashtra', 2, NOW(), NOW()),
('CLAAS Lexion 480 Harvester', 'Combine harvester for wheat and rice. 10 years old but in excellent condition. Perfect for harvesting large areas.', 'HARVESTER', 'RENT', 'LIVE', 2500, 'Nashik, Maharashtra', 2, NOW(), NOW()),
('Mahindra Disc Plough 2+2', 'Heavy-duty disc plough for deep ploughing. Perfect for soil preparation before sowing season.', 'PLOUGH', 'RENT', 'LIVE', 300, 'Ludhiana, Punjab', 3, NOW(), NOW()),
('Stihl Backpack Sprayer', 'Powerful backpack sprayer for pesticide and fertilizer application. 2L capacity with adjustable nozzle.', 'SPRAYER', 'RENT', 'LIVE', 150, 'Ludhiana, Punjab', 3, NOW(), NOW()),
('Jyothi Seeder - 5 Row', 'Automatic seed drill for precision planting. Adjustable for different seed sizes. Perfect for wheat and rice.', 'SEEDER', 'RENT', 'PENDING', 250, 'Delhi', 5, NOW(), NOW()),
('Rajat Thresher with Motor', 'Electric motor-driven thresher. Separates grain from stalks efficiently. Ideal for small to medium farms.', 'THRESHER', 'RENT', 'PENDING', 400, 'Delhi', 5, NOW(), NOW());

-- Insert SALE Listings (For Arun Patel - user_id 4)
INSERT INTO listings (title, description, category, type, status, sale_price, location, owner_id, created_at, updated_at) VALUES
('Massey Ferguson 350 (1985)', 'Classic vintage tractor. 35 HP, good condition. Requires minor repairs but fully operational.', 'TRACTOR', 'SALE', 'LIVE', 150000, 'Ahmedabad, Gujarat', 4, NOW(), NOW()),
('3 HP Submersible Pump Set', 'Brand new, never used. Perfect for irrigation from wells and borewells. Comes with 2-year warranty.', 'OTHER', 'SALE', 'LIVE', 45000, 'Ahmedabad, Gujarat', 4, NOW(), NOW()),
('Jyothi Seeder - 9 Row', 'Semi-automatic seed drill. Excellent for precise seed placement. Used for 1 season only.', 'SEEDER', 'SALE', 'LIVE', 35000, 'Ahmedabad, Gujarat', 4, NOW(), NOW()),
('BCS 770 Thresher', 'Walking tractor with thresher attachment. Great for small holdings. Very reliable with low maintenance cost.', 'THRESHER', 'SALE', 'LIVE', 250000, 'Ahmedabad, Gujarat', 4, NOW(), NOW()),
('Kubota Mini Tractor L245', 'Japanese brand mini tractor. 24 HP, perfect for small to medium farms. Excellent condition with service records.', 'TRACTOR', 'SALE', 'LIVE', 450000, 'Gujarat', 4, NOW(), NOW());

-- Insert more SALE Listings (For Meera Gupta - user_id 6)
INSERT INTO listings (title, description, category, type, status, sale_price, location, owner_id, created_at, updated_at) VALUES
('John Deere 310 Backhoe', 'Versatile machine for digging, loading, and landscaping. 4WD, low hours, well-maintained.', 'OTHER', 'SALE', 'PENDING', 800000, 'Haryana', 6, NOW(), NOW()),
('Diesel Water Pump - 2 inch', 'High-capacity water pump. New condition. Suitable for agricultural and construction use.', 'OTHER', 'SALE', 'PENDING', 55000, 'Haryana', 6, NOW(), NOW());

-- Insert Listing Images
INSERT INTO listing_images (listing_id, image_url) VALUES
-- Tractor images
(1, 'https://via.placeholder.com/500x400?text=John+Deere+Tractor'),
(1, 'https://via.placeholder.com/500x400?text=Tractor+Front+View'),
(1, 'https://via.placeholder.com/500x400?text=Tractor+Side+View'),
-- Harvester images
(2, 'https://via.placeholder.com/500x400?text=CLAAS+Harvester'),
(2, 'https://via.placeholder.com/500x400?text=Harvester+Working'),
-- Plough images
(3, 'https://via.placeholder.com/500x400?text=Mahindra+Plough'),
-- Sprayer images
(4, 'https://via.placeholder.com/500x400?text=Stihl+Sprayer'),
-- Seeder images
(5, 'https://via.placeholder.com/500x400?text=Jyothi+Seeder'),
-- Thresher images
(6, 'https://via.placeholder.com/500x400?text=Rajat+Thresher'),
-- Sale listings
(7, 'https://via.placeholder.com/500x400?text=Massey+Ferguson'),
(8, 'https://via.placeholder.com/500x400?text=Pump+Set'),
(9, 'https://via.placeholder.com/500x400?text=Jyothi+Seeder+9Row'),
(10, 'https://via.placeholder.com/500x400?text=BCS+Thresher'),
(11, 'https://via.placeholder.com/500x400?text=Kubota+Mini'),
(12, 'https://via.placeholder.com/500x400?text=Backhoe'),
(13, 'https://via.placeholder.com/500x400?text=Water+Pump');

-- Verify data insertion
SELECT 'Users:' as section, COUNT(*) as count FROM users
UNION ALL
SELECT 'Listings:', COUNT(*) FROM listings
UNION ALL
SELECT 'Listing Images:', COUNT(*) FROM listing_images;
