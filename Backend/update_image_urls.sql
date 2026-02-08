-- Update image URLs in listings with actual images from folder

-- Get all listings first (optional - to see what exists)
-- SELECT id, title, image_urls FROM listing WHERE status = 'LIVE' LIMIT 10;

-- Update listings with real image URLs

-- Listing 1: Mahindra 575 - Large tractors (SALE)
UPDATE listing SET image_urls = ARRAY['/images/mahindra575.jpg'] 
WHERE id = 1;

-- Listing 2: John Deere 5050 - Tractor (SALE)
UPDATE listing SET image_urls = ARRAY['/images/john-deere-5050.avif'] 
WHERE id = 2;

-- Listing 3: CLAAS Lexion 480 - Harvester (RENT)
UPDATE listing SET image_urls = ARRAY['/images/CLAAS_Lexion480_Harvester.webp'] 
WHERE id = 3;

-- Listing 4: Mahindra Disc Plough (SALE)
UPDATE listing SET image_urls = ARRAY['/images/mahindra-disc-plough.avif'] 
WHERE id = 4;

-- Listing 5: Jyothi Seeder - 9 Row (SALE)
UPDATE listing SET image_urls = ARRAY['/images/Jyothi-Seeder - 9 Row.webp'] 
WHERE id = 5;

-- Listing 6: Stihl Backpack Sprayer (RENT)
UPDATE listing SET image_urls = ARRAY['/images/Stihl_Backpack-Sprayer.jpg'] 
WHERE id = 6;

-- Listing 7: 3-HP Submersible Pump Set (SALE)
UPDATE listing SET image_urls = ARRAY['/images/3-HPSubmersible-PumpSet.jpg'] 
WHERE id = 7;

-- Verify updates
SELECT id, title, image_urls FROM listing WHERE status = 'LIVE' LIMIT 10;
