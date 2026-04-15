package com.example.Service;

import com.example.Model.Listing;
import com.example.Model.Order;
import com.example.Model.User;
import com.example.Repo.ListingRepository;
import com.example.Repo.OrderRepository;
import com.example.Repo.UserRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final ListingRepository listingRepository;
    private final UserRepo userRepository;

    public Order createOrder(Long buyerId,
                             Long listingId,
                             BigDecimal amount,
                             String paymentId,
                             String paymentStatus,
                             LocalDate rentStartDate,
                             LocalDate rentEndDate) {
        User buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new IllegalArgumentException("Buyer not found"));

        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new IllegalArgumentException("Listing not found"));

        if (listing.getStatus() != Listing.ListingStatus.LIVE) {
            throw new IllegalArgumentException("Listing is not available for purchase");
        }

        List<Order> existingOrders = orderRepository.findByListing(listing);
        if (!existingOrders.isEmpty() && "PAID".equals(paymentStatus)) {
            throw new IllegalArgumentException("This listing has already been booked/sold");
        }

        if (listing.getType() == Listing.ListingType.RENT) {
            validateRentDates(rentStartDate, rentEndDate);
            listing.setStatus(Listing.ListingStatus.BOOKED);
        } else {
            listing.setStatus(Listing.ListingStatus.SOLD);
        }

        Order order = new Order();
        order.setBuyer(buyer);
        order.setListing(listing);
        order.setAmount(amount);
        order.setPaymentId(paymentId);
        order.setPaymentStatus(paymentStatus);
        order.setRentStartDate(rentStartDate);
        order.setRentEndDate(rentEndDate);

        listingRepository.save(listing);
        Order saved = orderRepository.save(order);
        log.info("Order created: id={}, listing={}, buyer={}, type={}", 
                saved.getId(), listingId, buyerId, listing.getType().name());
        return saved;
    }

    public List<Order> getOrdersByBuyer(Long buyerId) {
        User buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new IllegalArgumentException("Buyer not found"));
        return orderRepository.findByBuyer(buyer);
    }

    public List<Order> getOrdersByListing(Long listingId) {
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new IllegalArgumentException("Listing not found"));
        return orderRepository.findByListing(listing);
    }

    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
    }

    public BigDecimal calculateAmount(Listing listing, LocalDate rentStartDate, LocalDate rentEndDate) {
        if (listing.getType() == Listing.ListingType.SALE) {
            if (listing.getSalePrice() == null) {
                throw new IllegalArgumentException("Sale price not set for this listing");
            }
            return listing.getSalePrice();
        }

        // RENT type
        validateRentDates(rentStartDate, rentEndDate);
        long days = ChronoUnit.DAYS.between(rentStartDate, rentEndDate) + 1;
        if (days < 1) {
            throw new IllegalArgumentException("Invalid rental dates - end date must be after start date");
        }
        if (listing.getRentPricePerDay() == null) {
            throw new IllegalArgumentException("Rent price not set for this listing");
        }
        
        BigDecimal totalPrice = listing.getRentPricePerDay().multiply(BigDecimal.valueOf(days));
        log.info("Calculated rent amount: {} days * {} = {}", days, listing.getRentPricePerDay(), totalPrice);
        return totalPrice;
    }

    private void validateRentDates(LocalDate start, LocalDate end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("Rental dates are required for rent listings");
        }
        if (start.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Rental start date cannot be in the past");
        }
        if (end.isBefore(start)) {
            throw new IllegalArgumentException("Rental end date must be on or after start date");
        }
    }

    public boolean isListingAvailable(Long listingId) {
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new IllegalArgumentException("Listing not found"));
        return listing.getStatus() == Listing.ListingStatus.LIVE;
    }
}
