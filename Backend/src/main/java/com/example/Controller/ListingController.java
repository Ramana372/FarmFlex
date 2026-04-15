package com.example.Controller;

import com.example.Model.Listing;
import com.example.Repo.ListingRepository;
import com.example.Service.ListingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
@RestController
@RequestMapping("/api/listings")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class ListingController {

    private final ListingService listingService;
    private final ListingRepository listingRepository;

    /**
     * Public endpoint to fetch all live listings with images (no authentication required)
     * Useful for showcase/demo purposes
     */
    @GetMapping("/all")
    public ResponseEntity<?> getAllListingsWithImages() {
        try {
            List<Listing> listings = listingService.getAllLiveListings();
            // Ensure images are loaded for all listings
            listings.forEach(listing -> {
                if (listing.getImages() != null) {
                    listing.getImages().size(); // Trigger lazy loading
                }
            });
            return ResponseEntity.ok(listings);
        } catch (Exception e) {
            log.error("Error getting all listings: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch listings", "details", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> getLiveListings(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String search
    ) {
        try {
            List<Listing> listings = listingService.searchListings(type, category, location, minPrice, maxPrice, search);
            // Ensure images are loaded for all listings
            listings.forEach(listing -> {
                if (listing.getImages() != null) {
                    listing.getImages().size(); // Trigger lazy loading
                }
            });
            return ResponseEntity.ok(listings);
        } catch (Exception e) {
            log.error("Error getting listings: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get listings"));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getListing(@PathVariable Long id) {
        try {
            return listingService.getLiveListingById(id)
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error getting listing: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get listing"));
        }
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('FARMER')")
    public ResponseEntity<?> createListing(
            @RequestAttribute("userId") Long farmerId,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @RequestPart("title") String title,
            @RequestPart("description") String description,
            @RequestPart("category") String category,
            @RequestPart("type") String type,
            @RequestPart(value = "salePrice", required = false) String salePriceStr,
            @RequestPart(value = "rentPricePerDay", required = false) String rentPriceStr,
            @RequestPart("location") String location
    ) {
        try {
            log.info("Creating listing for farmer: {} with title: {} and {} images", farmerId, title, 
                    images != null ? images.size() : 0);
            
            BigDecimal salePrice = null;
            BigDecimal rentPricePerDay = null;
            
            if ("SALE".equalsIgnoreCase(type) && salePriceStr != null && !salePriceStr.isEmpty()) {
                try {
                    salePrice = new BigDecimal(salePriceStr);
                } catch (NumberFormatException e) {
                    return ResponseEntity.badRequest().body(Map.of("error", "Invalid sale price format"));
                }
            }
            
            if ("RENT".equalsIgnoreCase(type) && rentPriceStr != null && !rentPriceStr.isEmpty()) {
                try {
                    rentPricePerDay = new BigDecimal(rentPriceStr);
                } catch (NumberFormatException e) {
                    return ResponseEntity.badRequest().body(Map.of("error", "Invalid rent price format"));
                }
            }
            
            // Save images to disk and get URLs
            List<String> imageUrls = new ArrayList<>();
            if (images != null && !images.isEmpty()) {
                String uploadsDir = "uploads/images";
                File uploadsFolder = new File(uploadsDir);
                if (!uploadsFolder.exists()) {
                    uploadsFolder.mkdirs();
                }
                for (MultipartFile imageFile : images) {
                    try {
                        String originalFilename = imageFile.getOriginalFilename();
                        if (originalFilename == null || originalFilename.isEmpty()) {
                            log.warn("Image filename is null or empty");
                            continue;
                        }
                        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
                        String uniqueFilename = UUID.randomUUID().toString() + fileExtension;
                        Path filePath = Paths.get(uploadsDir, uniqueFilename);
                        Files.write(filePath, imageFile.getBytes());
                        String imageUrl = "/uploads/images/" + uniqueFilename;
                        imageUrls.add(imageUrl);
                        
                        log.info("Saved image {} to disk, URL: {}", uniqueFilename, imageUrl);
                    } catch (IOException e) {
                        log.warn("Failed to save image to disk: {}", e.getMessage());
                    }
                }
            }
            
            log.info("Saved {} images for listing", imageUrls.size());

            Listing listing = listingService.createListing(
                    farmerId,
                    title,
                    description,
                    Listing.ListingCategory.valueOf(category.toUpperCase()),
                    Listing.ListingType.valueOf(type.toUpperCase()),
                    salePrice,
                    rentPricePerDay,
                    location,
                    imageUrls
            );

            log.info("Listing created successfully with ID: {}", listing.getId());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "Listing created successfully. Awaiting admin approval.", "listing", listing));
        } catch (IllegalArgumentException e) {
            log.error("Validation error creating listing: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error creating listing: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create listing: " + e.getMessage()));
        }
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('FARMER')")
    public ResponseEntity<?> getMyListingsGrouped(@RequestAttribute("userId") Long farmerId) {
        try {
            return ResponseEntity.ok(listingService.getMyListingsGrouped(farmerId));
        } catch (Exception e) {
            log.error("Error getting farmer listings: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get your listings"));
        }
    }

    @GetMapping("/{id}/details")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getListingDetails(@PathVariable Long id, @RequestAttribute("userId") Long userId) {
        try {
            return listingService.getListingById(id)
                    .map(listing -> {
                        if (listing.getStatus() == Listing.ListingStatus.LIVE || listing.getOwner().getId().equals(userId)) {
                            return ResponseEntity.ok((Object) listing);
                        }
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).body((Object) Map.of("error", "You don't have permission to view this listing"));
                    })
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error getting listing details: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get listing"));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('FARMER')")
    public ResponseEntity<?> updateListing(
            @PathVariable Long id,
            @RequestAttribute("userId") Long farmerId,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @RequestPart("title") String title,
            @RequestPart("description") String description,
            @RequestPart("category") String category,
            @RequestPart(value = "salePrice", required = false) BigDecimal salePrice,
            @RequestPart(value = "rentPricePerDay", required = false) BigDecimal rentPricePerDay,
            @RequestPart("location") String location
    ) {
        try {
            List<String> imageUrls = new ArrayList<>();
            
            // Process new images if provided
            if (images != null && !images.isEmpty()) {
                String uploadsDir = "uploads/images";
                File uploadsFolder = new File(uploadsDir);
                if (!uploadsFolder.exists()) {
                    uploadsFolder.mkdirs();
                }
                for (MultipartFile imageFile : images) {
                    try {
                        String originalFilename = imageFile.getOriginalFilename();
                        if (originalFilename == null || originalFilename.isEmpty()) {
                            log.warn("Image filename is null or empty");
                            continue;
                        }
                        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
                        String uniqueFilename = UUID.randomUUID().toString() + fileExtension;
                        Path filePath = Paths.get(uploadsDir, uniqueFilename);
                        Files.write(filePath, imageFile.getBytes());
                        String imageUrl = "/uploads/images/" + uniqueFilename;
                        imageUrls.add(imageUrl);
                        
                        log.info("Saved image {} to disk, URL: {}", uniqueFilename, imageUrl);
                    } catch (IOException e) {
                        log.warn("Failed to save image to disk: {}", e.getMessage());
                    }
                }
            } else {
                // Keep existing images if no new images uploaded
                imageUrls = listingService.getListingById(id)
                        .map(listing -> listing.getImageUrls())
                        .orElse(new ArrayList<>());
            }

            Listing listing = listingService.updateListing(
                    id,
                    farmerId,
                    Listing.ListingCategory.valueOf(category.toUpperCase()),
                    title,
                    description,
                    salePrice,
                    rentPricePerDay,
                    location,
                    imageUrls
            );

            return ResponseEntity.ok(Map.of("message", "Listing updated successfully.", "listing", listing));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error updating listing: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update listing"));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('FARMER')")
    public ResponseEntity<?> deleteListing(@PathVariable Long id, @RequestAttribute("userId") Long farmerId) {
        try {
            listingService.deleteListing(id, farmerId);
            log.info("Listing {} deleted by farmer {}", id, farmerId);
            return ResponseEntity.ok(Map.of("message", "Listing deleted successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error deleting listing: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete listing"));
        }
    }

    /**
     * Public endpoint to update all images with exact title matching
     * Matches new uploaded images to listings by title
     * No authentication required (for class project demo)
     */
    @GetMapping("/sync-images")
    public ResponseEntity<?> syncImagesExactMatch() {
        try {
            log.info("Starting exact image matching for all listings");
            
            int updatedCount = 0;
            int mentionedCount = 0;
            
            // Get all listings
            List<Listing> approvedListings = listingRepository.findByStatus(Listing.ListingStatus.APPROVED);
            List<Listing> liveListings = listingRepository.findByStatus(Listing.ListingStatus.LIVE);
            
            List<Listing> allListings = new ArrayList<>();
            allListings.addAll(approvedListings);
            allListings.addAll(liveListings);
            
            // Define exact image mappings by title
            Map<String, String> titleToImage = new java.util.HashMap<>();
            
            // Equipment with new versions - prioritize newer ones
            titleToImage.put("John Deere Tractor 5310F", "/uploads/images/John Deere Tractor 53 IOF.webp");
            titleToImage.put("Mahindra Harvester PRO", "/uploads/images/Mahindra Harvester PRO.jpg");
            titleToImage.put("Used Agricultural Plough", "/uploads/images/Used Agricultural Plough1.jpg");
            titleToImage.put("Advanced Crop Sprayer", "/uploads/images/Advanced Crop Sprayer1.webp");
            titleToImage.put("Electric Seeder Machine", "/uploads/images/Electric Seeder Machine1.jpg");
            titleToImage.put("Sonalika DI 60 Tractor", "/uploads/images/Sonalika DI 60 Tractor.webp");
            
            // Product category mappings
            titleToImage.put("Premium Quality Wheat", "/uploads/images/GRAINS.jpg");
            titleToImage.put("Fresh Organic Vegetables", "/uploads/images/VEGETABLES.jpg");
            titleToImage.put("Sugarcane", "/uploads/images/GRAINS.jpg");
            titleToImage.put("Chicken Layer Birds", "/uploads/images/LIVESTOCK.jpg");
            titleToImage.put("Maize Seeds", "/uploads/images/SEEDS.jpg");
            titleToImage.put("Cotton Bales", "/uploads/images/GRAINS.jpg");
            titleToImage.put("John Deere Tractor", "/uploads/images/John Deere Tractor 53 IOF.webp");
            
            // Process each listing
            for (Listing listing : allListings) {
                try {
                    String imageUrl = null;
                    String title = listing.getTitle();
                    
                    // Try exact title match first
                    for (Map.Entry<String, String> entry : titleToImage.entrySet()) {
                        if (title.equalsIgnoreCase(entry.getKey()) || 
                            title.contains(entry.getKey()) || 
                            entry.getKey().contains(title)) {
                            imageUrl = entry.getValue();
                            break;
                        }
                    }
                    
                    // If no exact match, try by category
                    if (imageUrl == null && listing.getCategory() != null) {
                        switch(listing.getCategory().name()) {
                            case "GRAINS": imageUrl = "/uploads/images/GRAINS.jpg"; break;
                            case "VEGETABLES": imageUrl = "/uploads/images/VEGETABLES.jpg"; break;
                            case "SEEDS": imageUrl = "/uploads/images/SEEDS.jpg"; break;
                            case "LIVESTOCK": imageUrl = "/uploads/images/LIVESTOCK.jpg"; break;
                            case "TRACTOR": imageUrl = "/uploads/images/John Deere Tractor 53 IOF.webp"; break;
                            case "HARVESTER": imageUrl = "/uploads/images/Mahindra Harvester PRO.jpg"; break;
                            case "SPRAYER": imageUrl = "/uploads/images/Advanced Crop Sprayer1.webp"; break;
                            case "SEEDER": imageUrl = "/uploads/images/Electric Seeder Machine1.jpg"; break;
                            case "PLOUGH": imageUrl = "/uploads/images/Used Agricultural Plough1.jpg"; break;
                            default: imageUrl = "/uploads/images/GRAINS.jpg";
                        }
                    }
                    
                    if (imageUrl != null) {
                        // Clear existing images and set new one
                        listing.getImages().clear();
                        listing.setImageUrls(List.of(imageUrl));
                        listingRepository.save(listing);
                        updatedCount++;
                        log.info("✓ Updated listing {} ({}) → {}", listing.getId(), title, imageUrl);
                        mentionedCount++;
                    } else {
                        log.debug("? No image found for: {}", title);
                    }
                } catch (Exception e) {
                    log.warn("✗ Failed to update listing {}: {}", listing.getId(), e.getMessage());
                }
            }
            
            log.info("Image sync complete: {} updated / {} total", updatedCount, allListings.size());
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "All images synchronized with exact matching",
                    "updated", updatedCount,
                    "total", allListings.size(),
                    "detail", updatedCount + " out of " + allListings.size() + " listings updated"
            ));
        } catch (Exception e) {
            log.error("Error syncing images: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to sync images: " + e.getMessage()));
        }
    }
}

