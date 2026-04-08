package com.example.Repo;

import com.example.Model.Listing;
import com.example.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Listing entity
 * Provides database operations for listings (RENT/SALE equipment)
 */
public interface ListingRepository extends JpaRepository<Listing, Long> {
    
    // Basic queries
    List<Listing> findByStatus(Listing.ListingStatus status);
    List<Listing> findByOwner(User owner);
    List<Listing> findByOwnerAndStatus(User owner, Listing.ListingStatus status);
    Optional<Listing> findByIdAndStatus(Long id, Listing.ListingStatus status);
    
    // Category filters
    List<Listing> findByStatusAndCategory(Listing.ListingStatus status, Listing.ListingCategory category);
    
    // Type filters (RENT vs SALE)
    List<Listing> findByStatusAndType(Listing.ListingStatus status, Listing.ListingType type);
    
    // Location-based queries
    @Query("SELECT l FROM Listing l WHERE l.status = 'LIVE' AND LOWER(l.location) LIKE LOWER(CONCAT('%', :location, '%'))")
    List<Listing> findByStatusAndLocationContaining(@Param("location") String location);
    
    // Complex search query
    @Query("SELECT l FROM Listing l WHERE l.status = 'LIVE' " +
           "AND (:search IS NULL OR LOWER(l.title) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(l.description) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND (:category IS NULL OR l.category = :category) " +
           "AND (:type IS NULL OR l.type = :type)")
    List<Listing> searchListings(@Param("search") String search,
                                 @Param("category") Listing.ListingCategory category,
                                 @Param("type") Listing.ListingType type);
    
    // Count statistics
    long countByStatus(Listing.ListingStatus status);
    long countByOwnerAndStatus(User owner, Listing.ListingStatus status);
    
    // Get all publicly available listings (LIVE or APPROVED from database)
    @Query(value = "SELECT * FROM listings WHERE status IN ('LIVE', 'APPROVED')", nativeQuery = true)
    List<Listing> findAllPublicListings();
}
