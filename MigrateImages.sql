-- FarmFlex Image Migration Script
-- Add image URLs to all listings in the database

-- First, check what listings exist
-- This will help us understand what data we're working with

-- Clear existing images (optional - comment out if you want to preserve)
-- DELETE FROM listing_images;

-- Phase 1: Equipment Listings (IDs 2-7)
DELETE FROM listing_images WHERE listing_id IN (2, 3, 4, 5, 6, 7);

INSERT INTO listing_images (listing_id, image_url) VALUES
(2, '/uploads/images/John Deere Tractor 53.avif'),
(3, '/uploads/images/Mahindra_Harvester_PRO.jpg'),
(4, '/uploads/images/Used Agricultural Plough.webp'),
(5, '/uploads/images/Sonalika DI 60 Tractor.webp'),
(6, '/uploads/images/Advanced Crop Sprayer.jpg'),
(7, '/uploads/images/Electric Seeder Machine.jpg');

-- Phase 2: Product Listings (IDs 8-25, adjust based on actual IDs)
-- These will use our category-based images
DELETE FROM listing_images WHERE listing_id >= 8;

INSERT INTO listing_images (listing_id, image_url) VALUES
-- GRAINS - Premium Quality Wheat
(8, '/uploads/images/GRAINS.jpg'),
-- VEGETABLES - Fresh Organic Vegetables Bundle
(9, '/uploads/images/VEGETABLES.jpg'),
-- GRAINS - Sugarcane
(10, '/uploads/images/GRAINS.jpg'),
-- LIVESTOCK - Chicken Layer Birds
(11, '/uploads/images/LIVESTOCK.jpg'),
-- SEEDS - Maize Seeds
(12, '/uploads/images/SEEDS.jpg'),
-- GRAINS - Cotton Bales
(13, '/uploads/images/GRAINS.jpg'),
-- Fallback for additional listings
(14, '/uploads/images/GRAINS.jpg'),
(15, '/uploads/images/VEGETABLES.jpg'),
(16, '/uploads/images/GRAINS.jpg'),
(17, '/uploads/images/LIVESTOCK.jpg'),
(18, '/uploads/images/SEEDS.jpg'),
(19, '/uploads/images/GRAINS.jpg'),
(20, '/uploads/images/VEGETABLES.jpg'),
(21, '/uploads/images/LIVESTOCK.jpg');

-- Verify the insertions
SELECT l.id, l.title, l.category, COUNT(li.id) as image_count
FROM listings l
LEFT JOIN listing_images li ON l.id = li.listing_id
GROUP BY l.id, l.title, l.category
ORDER BY l.id;
