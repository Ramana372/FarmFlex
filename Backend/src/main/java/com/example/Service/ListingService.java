package com.example.Service;

import com.example.Model.Listing;
import com.example.Model.User;
import com.example.Repo.ListingRepository;
import com.example.Repo.UserRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ListingService {

    private final ListingRepository listingRepository;
    private final UserRepo userRepository;

    /**
     * Create a new listing for a farmer
     * Status starts as PENDING, awaiting admin approval
     */
    public Listing createListing(Long farmerId,
                                 String title,
                                 String description,
                                 Listing.ListingCategory category,
                                 Listing.ListingType type,
                                 BigDecimal salePrice,
                                 BigDecimal rentPricePerDay,
                                 String location,
                                 List<String> imageUrls) {
        User farmer = userRepository.findById(farmerId)
                .orElseThrow(() -> new IllegalArgumentException("Farmer not found"));

        if (!farmer.getRole().equals(User.UserRole.FARMER)) {
            throw new IllegalArgumentException("Only farmers can create listings");
        }

        validateListingPrices(type, salePrice, rentPricePerDay);

        Listing listing = new Listing();
        listing.setOwner(farmer);
        listing.setTitle(title);
        listing.setDescription(description);
        listing.setCategory(category);
        listing.setType(type);
        listing.setSalePrice(type == Listing.ListingType.SALE ? salePrice : null);
        listing.setRentPricePerDay(type == Listing.ListingType.RENT ? rentPricePerDay : null);
        listing.setLocation(location);
        listing.setImageUrls(imageUrls == null ? new ArrayList<>() : imageUrls);
        listing.setStatus(Listing.ListingStatus.PENDING);

        Listing saved = listingRepository.save(listing);
        log.info("Listing created by farmer {}: {}", farmerId, saved.getId());
        return saved;
    }

    /**
     * Get all live (approved) listings for public marketplace
     */
    public List<Listing> getLiveListings() {
        // Get all publicly available listings (either APPROVED or LIVE status)
        List<Listing> listings = new ArrayList<>();
        listings.addAll(listingRepository.findByStatus(Listing.ListingStatus.APPROVED));
        listings.addAll(listingRepository.findByStatus(Listing.ListingStatus.LIVE));
        return listings;
    }

    /**
     * Get a specific live listing by ID
     */
    public Optional<Listing> getLiveListingById(Long listingId) {
        // Check for both APPROVED and LIVE statuses
        Optional<Listing> listing = listingRepository.findByIdAndStatus(listingId, Listing.ListingStatus.LIVE);
        if (listing.isEmpty()) {
            listing = listingRepository.findByIdAndStatus(listingId, Listing.ListingStatus.APPROVED);
        }
        return listing;
    }

    /**
     * Get all listings owned by a farmer (regardless of status)
     */
    public List<Listing> getMyListings(Long farmerId) {
        User farmer = userRepository.findById(farmerId)
                .orElseThrow(() -> new IllegalArgumentException("Farmer not found"));
        return listingRepository.findByOwner(farmer);
    }

    /**
     * Get farmer's listings grouped by status (PENDING, LIVE, REJECTED, SOLD, BOOKED)
     */
    public Map<Listing.ListingStatus, List<Listing>> getMyListingsGrouped(Long farmerId) {
        List<Listing> listings = getMyListings(farmerId);
        Map<Listing.ListingStatus, List<Listing>> grouped = new EnumMap<>(Listing.ListingStatus.class);
        for (Listing.ListingStatus status : Listing.ListingStatus.values()) {
            grouped.put(status, new ArrayList<>());
        }
        listings.forEach(listing -> grouped.get(listing.getStatus()).add(listing));
        return grouped;
    }

    /**
     * Search and filter live listings by type, category, location, price, and keywords
     */
    public List<Listing> searchListings(String type,
                                        String category,
                                        String location,
                                        BigDecimal minPrice,
                                        BigDecimal maxPrice,
                                        String search) {
        List<Listing> listings = getLiveListings();

        return listings.stream()
                .filter(l -> type == null || type.isBlank() || l.getType().name().equalsIgnoreCase(type))
                .filter(l -> category == null || category.isBlank() || l.getCategory().name().equalsIgnoreCase(category))
                .filter(l -> location == null || location.isBlank() || (l.getLocation() != null && l.getLocation().toLowerCase().contains(location.toLowerCase())))
                .filter(l -> matchesPrice(l, minPrice, maxPrice))
                .filter(l -> search == null || search.isBlank() ||
                    l.getTitle().toLowerCase().contains(search.toLowerCase()) ||
                    (l.getDescription() != null && l.getDescription().toLowerCase().contains(search.toLowerCase())))
                .collect(Collectors.toList());
    }

    /**
     * Get all pending listings awaiting admin approval
     */
    public List<Listing> getPendingApprovals() {
        return listingRepository.findByStatus(Listing.ListingStatus.PENDING);
    }

    /**
     * Approve a pending listing (admin only) - changes status to LIVE
     */
    public Listing approveListing(Long listingId, Long adminId) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new IllegalArgumentException("Admin not found"));

        if (!admin.getRole().equals(User.UserRole.ADMIN)) {
            throw new IllegalArgumentException("Only admins can approve listings");
        }

        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new IllegalArgumentException("Listing not found"));

        if (listing.getStatus() != Listing.ListingStatus.PENDING) {
            throw new IllegalArgumentException("Only pending listings can be approved");
        }

        listing.setStatus(Listing.ListingStatus.LIVE);
        listing.setRejectionReason(null);

        Listing saved = listingRepository.save(listing);
        log.info("Listing {} approved by admin {}", listingId, adminId);
        return saved;
    }

    /**
     * Reject a pending listing (admin only) - changes status to REJECTED with reason
     */
    public Listing rejectListing(Long listingId, Long adminId, String rejectionReason) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new IllegalArgumentException("Admin not found"));

        if (!admin.getRole().equals(User.UserRole.ADMIN)) {
            throw new IllegalArgumentException("Only admins can reject listings");
        }

        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new IllegalArgumentException("Listing not found"));

        if (listing.getStatus() != Listing.ListingStatus.PENDING) {
            throw new IllegalArgumentException("Only pending listings can be rejected");
        }

        listing.setStatus(Listing.ListingStatus.REJECTED);
        listing.setRejectionReason(rejectionReason);

        Listing saved = listingRepository.save(listing);
        log.info("Listing {} rejected by admin {}", listingId, adminId);
        return saved;
    }

    /**
     * Get a listing by ID (includes non-live listings - for farmer and admin)
     */
    public Optional<Listing> getListingById(Long listingId) {
        return listingRepository.findById(listingId);
    }

    /**
     * Update listing details (farmer can edit pending listings)
     */
    public Listing updateListing(Long listingId, Long farmerId, Listing.ListingCategory category,
                                 String title, String description, BigDecimal salePrice,
                                 BigDecimal rentPricePerDay, String location, List<String> imageUrls) {
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new IllegalArgumentException("Listing not found"));

        // Only owner can edit, and only if PENDING or REJECTED
        if (!listing.getOwner().getId().equals(farmerId)) {
            throw new IllegalArgumentException("You can only edit your own listings");
        }

        if (listing.getStatus() != Listing.ListingStatus.PENDING && listing.getStatus() != Listing.ListingStatus.REJECTED) {
            throw new IllegalArgumentException("Can only edit PENDING or REJECTED listings");
        }

        listing.setTitle(title);
        listing.setDescription(description);
        listing.setCategory(category);
        listing.setLocation(location);
        listing.setSalePrice(salePrice);
        listing.setRentPricePerDay(rentPricePerDay);
        if (imageUrls != null && !imageUrls.isEmpty()) {
            listing.setImageUrls(imageUrls);
        }
        // Reset rejection reason if re-editing a rejected listing
        if (listing.getStatus() == Listing.ListingStatus.REJECTED) {
            listing.setStatus(Listing.ListingStatus.PENDING);
            listing.setRejectionReason(null);
        }

        Listing saved = listingRepository.save(listing);
        log.info("Listing {} updated by farmer {}", listingId, farmerId);
        return saved;
    }

    /**
     * Delete a listing (farmer can only delete PENDING or REJECTED listings)
     */
    public void deleteListing(Long listingId, Long farmerId) {
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new IllegalArgumentException("Listing not found"));

        if (!listing.getOwner().getId().equals(farmerId)) {
            throw new IllegalArgumentException("You can only delete your own listings");
        }

        if (listing.getStatus() != Listing.ListingStatus.PENDING && listing.getStatus() != Listing.ListingStatus.REJECTED) {
            throw new IllegalArgumentException("Can only delete PENDING or REJECTED listings");
        }

        listingRepository.delete(listing);
        log.info("Listing {} deleted by farmer {}", listingId, farmerId);
    }

    /**
     * Validate listing prices based on type
     */
    private void validateListingPrices(Listing.ListingType type, BigDecimal salePrice, BigDecimal rentPricePerDay) {
        if (type == Listing.ListingType.SALE) {
            if (salePrice == null || salePrice.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Sale price must be provided for SALE listings");
            }
        }
        if (type == Listing.ListingType.RENT) {
            if (rentPricePerDay == null || rentPricePerDay.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Rent price per day must be provided for RENT listings");
            }
        }
    }

    /**
     * Check if a price matches the price range filter
     */
    private boolean matchesPrice(Listing listing, BigDecimal minPrice, BigDecimal maxPrice) {
        BigDecimal price = listing.getType() == Listing.ListingType.SALE
                ? listing.getSalePrice()
                : listing.getRentPricePerDay();
        if (price == null) {
            return false;
        }
        if (minPrice != null && price.compareTo(minPrice) < 0) {
            return false;
        }
        if (maxPrice != null && price.compareTo(maxPrice) > 0) {
            return false;
        }
        return true;
    }
}
