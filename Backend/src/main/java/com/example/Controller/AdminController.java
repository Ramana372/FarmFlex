package com.example.Controller;

import com.example.DTO.RejectListingRequest;
import com.example.Model.Listing;
import com.example.Model.User;
import com.example.Repo.UserRepo;
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

/**
 * AdminController - Admin panel endpoints for approving listings and managing users
 * All endpoints require ADMIN role
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class AdminController {

    private final ListingService listingService;
    private final UserRepo userRepository;
    private final EmailService emailService;

    /**
     * Get admin dashboard statistics
     */
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

    /**
     * Get all pending listing approvals
     */
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

    /**
     * Approve a pending listing - changes status from PENDING to LIVE
     */
    @PostMapping("/listings/{listingId}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> approveListing(
            @PathVariable Long listingId,
            @RequestAttribute("userId") Long adminId) {
        try {
            Listing listing = listingService.approveListing(listingId, adminId);
            
            // Send approval email to farmer
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

    /**
     * Reject a pending listing with reason - changes status from PENDING to REJECTED
     */
    @PostMapping("/listings/{listingId}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> rejectListing(
            @PathVariable Long listingId,
            @RequestAttribute("userId") Long adminId,
            @RequestBody RejectListingRequest request) {
        try {
            Listing listing = listingService.rejectListing(listingId, adminId, request.getReason());
            
            // Send rejection email to farmer
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

    /**
     * Get all farmers registered in the system
     */
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

    /**
     * Get all live listings on marketplace
     */
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

    /**
     * Get all listings (live, pending, rejected) for a specific farmer
     */
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

    /**
     * Get all users in the system (farmers and admins)
     */
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

    /**
     * Get user details by ID (admin only)
     */
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
}
