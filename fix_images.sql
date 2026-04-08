-- Update listing images with working placeholder URLs
DELETE FROM listing_images;

INSERT INTO listing_images (listing_id, image_url) VALUES
(2, 'https://via.placeholder.com/400x300?text=John+Deere+Tractor'),
(3, 'https://via.placeholder.com/400x300?text=Mahindra+Harvester'),
(4, 'https://via.placeholder.com/400x300?text=Agricultural+Plough'),
(5, 'https://via.placeholder.com/400x300?text=Sonalika+Tractor'),
(6, 'https://via.placeholder.com/400x300?text=Crop+Sprayer'),
(7, 'https://via.placeholder.com/400x300?text=Electric+Seeder'),
(8, 'https://via.placeholder.com/400x300?text=Harvester+Machine'),
(13, 'https://via.placeholder.com/400x300?text=Electric+Seeder+Machine');
