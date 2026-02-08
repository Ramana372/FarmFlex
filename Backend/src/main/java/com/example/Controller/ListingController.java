package com.example.Controller;

import com.example.Model.Listing;
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

/**
 * ListingController - Public marketplace and farmer listing endpoints
 */
@RestController
@RequestMapping("/api/listings")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class ListingController {

    private final ListingService listingService;

    /**
     * Public marketplace - live listings with filters
     */
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
            return ResponseEntity.ok(listingService.searchListings(type, category, location, minPrice, maxPrice, search));
        } catch (Exception e) {
            log.error("Error getting listings: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get listings"));
        }
    }

    /**
     * Public listing details (LIVE only)
     */
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

    /**
     * Farmer creates listing (with image URLs)
     */
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
            
            // Convert string prices to BigDecimal
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
                String uploadsDir = "src/main/resources/public/images";
                File uploadsFolder = new File(uploadsDir);
                if (!uploadsFolder.exists()) {
                    uploadsFolder.mkdirs();
                }
                
                for (MultipartFile imageFile : images) {
                    try {
                        // Generate unique filename
                        String originalFilename = imageFile.getOriginalFilename();
                        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
                        String uniqueFilename = UUID.randomUUID().toString() + fileExtension;
                        
                        // Save file to disk
                        Path filePath = Paths.get(uploadsDir, uniqueFilename);
                        Files.write(filePath, imageFile.getBytes());
                        
                        // Store URL reference (relative path to serve from /images endpoint)
                        String imageUrl = "/images/" + uniqueFilename;
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

    /**
     * Farmer dashboard - grouped listings by status
     */
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

    /**
     * Get listing details (for farmers to see their own listing details including draft)
     */
    @GetMapping("/{id}/details")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getListingDetails(@PathVariable Long id, @RequestAttribute("userId") Long userId) {
        try {
            return listingService.getListingById(id)
                    .map(listing -> {
                        // Allow public view of LIVE listings or owner viewing their own listing
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

    /**
     * Farmer updates their listing (only PENDING or REJECTED listings can be edited)
     */
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
            List<String> imageUrls = List.of();
            if (imageUrls == null || imageUrls.isEmpty()) {
                // Keep existing images if no new images uploaded
                List<String> finalImageUrls = imageUrls;
                imageUrls = listingService.getListingById(id)
                        .map(listing -> listing.getImageUrls())
                        .orElse(finalImageUrls);
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

    /**
     * Farmer deletes their listing (only PENDING or REJECTED listings can be deleted)
     */
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
}
