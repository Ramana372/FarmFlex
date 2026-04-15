-- Insert image URLs for all listings
-- First, let's see what listings we have
SELECT id, title FROM listings;

-- Now insert images for each listing
INSERT INTO listing_images (listing_id, image_url) VALUES
-- John Deere Tractor 5310F
(2, '/uploads/images/John Deere Tractor 53.avif'),
-- Mahindra Harvester PRO
(3, '/uploads/images/Mahindra_Harvester_PRO.jpg'),
-- Used Agricultural Plough
(4, '/uploads/images/Used Agricultural Plough.webp'),
-- Sonalika DI 60 Tractor
(5, '/uploads/images/Sonalika DI 60 Tractor.webp'),
-- Advanced Crop Sprayer
(6, '/uploads/images/Advanced Crop Sprayer.jpg'),
-- Electric Seeder Machine
(7, '/uploads/images/Electric Seeder Machine.jpg'),
-- Premium Quality Wheat (if exists)
(14, '/uploads/images/GRAINS.jpg'),
(14, '/uploads/images/Premium Quality Wheat'),
-- Fresh Organic Vegetables Bundle
(15, '/uploads/images/VEGETABLES.jpg'),
-- Sugarcane - Bulk Quantity
(16, '/uploads/images/GRAINS.jpg'),
-- Chicken Layer Birds
(17, '/uploads/images/LIVESTOCK.jpg'),
-- Maize Seeds - Hybrid Variety
(18, '/uploads/images/SEEDS.jpg'),
-- Cotton Bales - Grade A
(19, '/uploads/images/GRAINS.jpg'),
-- More equipment images
(2, '/uploads/images/John Deere Tractor 53.avif'),
(3, '/uploads/images/Mahindra_Harvester_PRO.jpg')
ON CONFLICT DO NOTHING;

-- Verify images were inserted
SELECT l.id, l.title, COUNT(li.id) as image_count
FROM listings l
LEFT JOIN listing_images li ON l.id = li.listing_id
GROUP BY l.id, l.title
ORDER BY l.id;
