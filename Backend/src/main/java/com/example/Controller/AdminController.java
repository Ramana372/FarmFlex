package com.example.Controller;

import com.example.DTO.RejectListingRequest;
import com.example.Model.Listing;
import com.example.Model.User;
import com.example.Repo.UserRepo;
import com.example.Repo.ListingRepository;
import com.example.Service.ListingService;
import com.example.Service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class AdminController {

    private final ListingService listingService;
    private final UserRepo userRepository;
    private final ListingRepository listingRepository;
    private final EmailService emailService;
    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getDashboardStats() {
        try {
            List<Listing> pendingListings = listingService.getPendingApprovals();
            List<User> farmers = userRepository.findByRole(User.UserRole.FARMER);
            long totalLiveListings = listingService.getLiveListings().size();

            Map<String, Object> stats = new HashMap<>();
            stats.put("pendingApprovals", pendingListings.size());
            stats.put("totalFarmers", farmers.size());
            stats.put("totalLiveListings", totalLiveListings);
            stats.put("recentPendingListings", pendingListings.stream().limit(5).toList());

            log.info("Admin dashboard stats retrieved");
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Error getting dashboard stats: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get dashboard stats"));
        }
    }

    @GetMapping("/listings/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getPendingListings() {
        try {
            List<Listing> pendingListings = listingService.getPendingApprovals();
            log.info("Retrieved {} pending listings", pendingListings.size());
            return ResponseEntity.ok(Map.of(
                    "count", pendingListings.size(),
                    "listings", pendingListings
            ));
        } catch (Exception e) {
            log.error("Error getting pending listings: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get pending listings"));
        }
    }

    @PostMapping("/listings/{listingId}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> approveListing(
            @PathVariable Long listingId,
            @RequestAttribute("userId") Long adminId) {
        try {
            Listing listing = listingService.approveListing(listingId, adminId);
            
            if (listing.getOwner() != null) {
                emailService.sendListingStatusEmail(
                        listing.getOwner().getEmail(),
                        listing.getOwner().getName(),
                        listing.getTitle(),
                        "APPROVED",
                        null
                );
            }
            
            log.info("Listing {} approved by admin {} and email sent", listingId, adminId);
            return ResponseEntity.ok(Map.of(
                    "message", "Listing approved successfully and now live on marketplace. Farmer notified via email.",
                    "listing", listing
            ));
        } catch (IllegalArgumentException e) {
            log.warn("Invalid approval request: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error approving listing: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to approve listing"));
        }
    }

    @PostMapping("/listings/{listingId}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> rejectListing(
            @PathVariable Long listingId,
            @RequestAttribute("userId") Long adminId,
            @RequestBody RejectListingRequest request) {
        try {
            Listing listing = listingService.rejectListing(listingId, adminId, request.getReason());
            
            if (listing.getOwner() != null) {
                emailService.sendListingStatusEmail(
                        listing.getOwner().getEmail(),
                        listing.getOwner().getName(),
                        listing.getTitle(),
                        "REJECTED",
                        request.getReason()
                );
            }
            
            log.info("Listing {} rejected by admin {} and email sent with reason: {}", listingId, adminId, request.getReason());
            return ResponseEntity.ok(Map.of(
                    "message", "Listing rejected. Farmer notified with rejection reason via email.",
                    "listing", listing
            ));
        } catch (IllegalArgumentException e) {
            log.warn("Invalid rejection request: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error rejecting listing: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to reject listing"));
        }
    }

    @GetMapping("/farmers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllFarmers() {
        try {
            List<User> farmers = userRepository.findByRole(User.UserRole.FARMER);
            log.info("Retrieved {} farmers", farmers.size());
            return ResponseEntity.ok(Map.of(
                    "count", farmers.size(),
                    "farmers", farmers
            ));
        } catch (Exception e) {
            log.error("Error getting farmers: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get farmers"));
        }
    }

    @GetMapping("/listings")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllListings() {
        try {
            List<Listing> liveListings = listingService.getLiveListings();
            log.info("Retrieved {} live listings", liveListings.size());
            return ResponseEntity.ok(Map.of(
                    "count", liveListings.size(),
                    "listings", liveListings
            ));
        } catch (Exception e) {
            log.error("Error getting listings: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get listings"));
        }
    }

    @GetMapping("/farmers/{farmerId}/listings")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getFarmerListings(@PathVariable Long farmerId) {
        try {
            User farmer = userRepository.findById(farmerId)
                    .orElseThrow(() -> new IllegalArgumentException("Farmer not found"));

            List<Listing> listings = listingService.getMyListings(farmerId);
            Map<String, Object> response = new HashMap<>();
            response.put("farmer", Map.of(
                    "id", farmer.getId(),
                    "name", farmer.getName(),
                    "email", farmer.getEmail(),
                    "phone", farmer.getPhone(),
                    "location", farmer.getLocation(),
                    "role", farmer.getRole().toString()
            ));
            response.put("count", listings.size());
            response.put("listings", listings);

            log.info("Retrieved {} listings for farmer {}", listings.size(), farmerId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("Farmer not found: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error getting farmer listings: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get farmer listings"));
        }
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllUsers() {
        try {
            List<User> users = userRepository.findAll();
            log.info("Retrieved {} users", users.size());
            return ResponseEntity.ok(Map.of(
                    "count", users.size(),
                    "users", users
            ));
        } catch (Exception e) {
            log.error("Error getting users: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get users"));
        }
    }

    @GetMapping("/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUserDetails(@PathVariable Long userId) {
        try {
            return userRepository.findById(userId)
                    .map(user -> ResponseEntity.ok((Object) Map.of(
                            "id", user.getId(),
                            "name", user.getName(),
                            "email", user.getEmail(),
                            "phone", user.getPhone(),
                            "location", user.getLocation(),
                            "role", user.getRole().toString(),
                            "emailVerified", user.getEmailVerified(),
                            "createdAt", user.getCreatedAt()
                    )))
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error getting user details: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get user details"));
        }
    }

    /**
     * Admin endpoint to update images with exact matching based on titles
     * Matches new images to existing listings by title
     */
    @PostMapping("/update-images-exact")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateImagesExactMatch() {
        return performExactImageMatch();
    }

    /**
     * Public endpoint for class project - update images with exact matching
     * This allows updating images without admin authentication for demo purposes
     */
    @PostMapping("/update-images")
    public ResponseEntity<?> updateImagesPublic() {
        return performExactImageMatch();
    }

    private ResponseEntity<?> performExactImageMatch() {
        try {
            log.info("Starting exact image matching for all listings");
            
            int updatedCount = 0;
            
            // Get all listings
            List<Listing> approvedListings = listingRepository.findByStatus(Listing.ListingStatus.APPROVED);
            List<Listing> liveListings = listingRepository.findByStatus(Listing.ListingStatus.LIVE);
            
            java.util.List<Listing> allListings = new java.util.ArrayList<>();
            allListings.addAll(approvedListings);
            allListings.addAll(liveListings);
            
            // Define exact image mappings by title keywords and filename matching
            java.util.Map<String, String> titleToImage = new java.util.HashMap<>();
            
            // Equipment with new versions - prioritize new ones
            titleToImage.put("John Deere Tractor 5310F", "/uploads/images/John Deere Tractor 53 IOF.webp");
            titleToImage.put("John Deere Tractor", "/uploads/images/John Deere Tractor 53 IOF.webp");
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
            
            // Process each listing
            for (Listing listing : allListings) {
                try {
                    String imageUrl = null;
                    String title = listing.getTitle();
                    
                    // Try exact title match first
                    for (java.util.Map.Entry<String, String> entry : titleToImage.entrySet()) {
                        if (title.contains(entry.getKey()) || entry.getKey().contains(title)) {
                            imageUrl = entry.getValue();
                            break;
                        }
                    }
                    
                    // If no exact match, try by category
                    if (imageUrl == null && listing.getCategory() != null) {
                        switch(listing.getCategory().name()) {
                            case "GRAINS":
                                imageUrl = "/uploads/images/GRAINS.jpg";
                                break;
                            case "VEGETABLES":
                                imageUrl = "/uploads/images/VEGETABLES.jpg";
                                break;
                            case "SEEDS":
                                imageUrl = "/uploads/images/SEEDS.jpg";
                                break;
                            case "LIVESTOCK":
                                imageUrl = "/uploads/images/LIVESTOCK.jpg";
                                break;
                            case "TRACTOR":
                                imageUrl = "/uploads/images/John Deere Tractor 53 IOF.webp";
                                break;
                            case "HARVESTER":
                                imageUrl = "/uploads/images/Mahindra Harvester PRO.jpg";
                                break;
                            case "SPRAYER":
                                imageUrl = "/uploads/images/Advanced Crop Sprayer1.webp";
                                break;
                            case "SEEDER":
                                imageUrl = "/uploads/images/Electric Seeder Machine1.jpg";
                                break;
                            case "PLOUGH":
                                imageUrl = "/uploads/images/Used Agricultural Plough1.jpg";
                                break;
                            default:
                                imageUrl = "/uploads/images/GRAINS.jpg";
                        }
                    }
                    
                    if (imageUrl != null) {
                        // Clear existing images and set new one
                        listing.getImages().clear();
                        listing.setImageUrls(List.of(imageUrl));
                        listingRepository.save(listing);
                        updatedCount++;
                        log.info("Updated listing {} ({}) with image: {}", listing.getId(), title, imageUrl);
                    }
                } catch (Exception e) {
                    log.warn("Failed to update image for listing {}: {}", listing.getId(), e.getMessage());
                }
            }
            
            log.info("Exact image matching completed: {} listings updated out of {}", updatedCount, allListings.size());
            return ResponseEntity.ok(Map.of(
                    "message", "Images updated successfully with exact matching",
                    "updatedCount", updatedCount,
                    "totalListings", allListings.size()
            ));
        } catch (Exception e) {
            log.error("Error updating images with exact match: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update images: " + e.getMessage()));
        }
    }

    /**
     * Admin endpoint to populate images for all listings (for class project showcase)
     * This helps display all products with proper images
     */
    @PostMapping("/populate-images")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> populateImages() {
        try {
            log.info("Starting image population for all listings");
            
            int successCount = 0;
            
            // Get all APPROVED and LIVE listings
            List<Listing> approvedListings = listingRepository.findByStatus(Listing.ListingStatus.APPROVED);
            List<Listing> liveListings = listingRepository.findByStatus(Listing.ListingStatus.LIVE);
            
            List<Listing> allListings = new java.util.ArrayList<>();
            allListings.addAll(approvedListings);
            allListings.addAll(liveListings);
            
            // Define image mapping
            String[] equipmentImages = {
                "/uploads/images/John Deere Tractor 53.avif",
                "/uploads/images/Mahindra_Harvester_PRO.jpg",
                "/uploads/images/Used Agricultural Plough.webp",
                "/uploads/images/Sonalika DI 60 Tractor.webp",
                "/uploads/images/Advanced Crop Sprayer.jpg",
                "/uploads/images/Electric Seeder Machine.jpg"
            };
            
            String[] categoryImages = {
                "/uploads/images/GRAINS.jpg",
                "/uploads/images/VEGETABLES.jpg",
                "/uploads/images/SEEDS.jpg",
                "/uploads/images/LIVESTOCK.jpg"
            };
            
            // Apply images to listings
            for (Listing listing : allListings) {
                try {
                    // Skip if already has images
                    if (listing.getImageUrls() != null && !listing.getImageUrls().isEmpty()) {
                        log.debug("Listing {} already has images, skipping", listing.getId());
                        continue;
                    }
                    
                    String imageUrl = null;
                    
                    // Try to match based on listing ID for specific equipment (IDs 2-7)
                    if (listing.getId() >= 2 && listing.getId() <= 7) {
                        int index = (int)(listing.getId() - 2);
                        if (index < equipmentImages.length) {
                            imageUrl = equipmentImages[index];
                        }
                    }
                    
                    // If no specific match, use category-based selection
                    if (imageUrl == null && listing.getCategory() != null) {
                        int categoryIndex = (int)(listing.getId() % categoryImages.length);
                        imageUrl = categoryImages[categoryIndex];
                    }
                    
                    if (imageUrl != null) {
                        listing.setImageUrls(List.of(imageUrl));
                        listingRepository.save(listing);
                        successCount++;
                        log.debug("Added image {} to listing {} ({})", imageUrl, listing.getId(), listing.getTitle());
                    }
                } catch (Exception e) {
                    log.warn("Failed to add image to listing {}: {}", listing.getId(), e.getMessage());
                }
            }
            
            log.info("Image population completed: {} listings updated out of {}", successCount, allListings.size());
            return ResponseEntity.ok(Map.of(
                    "message", "Images populated successfully",
                    "successCount", successCount,
                    "totalListings", allListings.size()
            ));
        } catch (Exception e) {
            log.error("Error populating images: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to populate images: " + e.getMessage()));
        }
    }
}
