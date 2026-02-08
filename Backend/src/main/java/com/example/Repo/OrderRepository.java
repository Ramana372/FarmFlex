package com.example.Repo;

import com.example.Model.Listing;
import com.example.Model.Order;
import com.example.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Order entity
 * Provides database operations for orders (purchases and rentals)
 */
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    // Find orders by buyer
    List<Order> findByBuyer(User buyer);
    
    // Find orders by listing
    List<Order> findByListing(Listing listing);
    
    // Check if listing has active order
    Optional<Order> findFirstByListingAndPaymentStatus(Listing listing, String paymentStatus);
    
    // Find order by payment ID
    Optional<Order> findByPaymentId(String paymentId);
    
    // Count statistics
    long countByBuyer(User buyer);
}

