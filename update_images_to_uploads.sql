-- Update listing images to point to uploaded files
DELETE FROM listing_images;

INSERT INTO listing_images (listing_id, image_url) VALUES
(2, '/uploads/images/John Deere Tractor 53.avif'),
(3, '/uploads/images/Mahindra_Harvester_PRO.jpg'),
(4, '/uploads/images/Used Agricultural Plough.webp'),
(5, '/uploads/images/Sonalika DI 60 Tractor.webp'),
(6, '/uploads/images/Advanced Crop Sprayer.jpg'),
(7, '/uploads/images/Electric Seeder Machine.jpg'),
(8, '/uploads/images/Electric Seeder Machine.jpg'),
(13, '/uploads/images/Electric Seeder Machine.jpg');
