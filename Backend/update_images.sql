-- Clear existing images first
DELETE FROM listing_images;

-- Insert image URLs for each listing
INSERT INTO listing_images (listing_id, image_url) VALUES
(1, '/images/john-deere-5050.avif'),
(2, '/images/mahindra575.jpg'),
(3, '/images/CLAAS_Lexion480_Harvester.webp'),
(4, '/images/mahindra-disc-plough.avif'),
(5, '/images/Jyothi-Seeder - 9 Row.webp'),
(6, '/images/Stihl_Backpack-Sprayer.jpg'),
(7, '/images/3-HPSubmersible-PumpSet.jpg');
