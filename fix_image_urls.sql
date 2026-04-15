-- Fix image URLs in the database
-- Update all image URLs to point to actual images in /Backend/uploads/images/

DELETE FROM listing_images;

-- Insert images that actually exist in uploads/images/
INSERT INTO listing_images (listing_id, image_url) VALUES
(1, '/uploads/images/Advanced Crop Sprayer.jpg'),
(2, '/uploads/images/John Deere Tractor 53.avif'),
(3, '/uploads/images/Mahindra_Harvester_PRO.jpg'),
(4, '/uploads/images/Sonalika DI 60 Tractor.webp'),
(5, '/uploads/images/Used Agricultural Plough.webp'),
(6, '/uploads/images/Electric Seeder Machine.jpg'),
(7, '/uploads/images/Advanced Crop Sprayer.jpg'),
(8, '/uploads/images/Electric Seeder Machine.jpg'),
(13, '/uploads/images/John Deere Tractor 53.avif');

-- Verify the changes
SELECT id, listing_id, image_url FROM listing_images ORDER BY id;

