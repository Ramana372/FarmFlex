package com.example.Controller;

import com.example.Service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getMyOrders(@RequestAttribute("userId") Long userId) {
        try {
            var orders = orderService.getOrdersByBuyer(userId);
            log.info("Retrieved {} orders for user {}", orders.size(), userId);
            return ResponseEntity.ok(Map.of(
                    "count", orders.size(),
                    "orders", orders
            ));
        } catch (Exception e) {
            log.error("Error getting orders for user {}: {}", userId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get orders"));
        }
    }

    @GetMapping("/{orderId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getOrderDetails(
            @PathVariable Long orderId,
            @RequestAttribute("userId") Long userId) {
        try {
            var order = orderService.getOrderById(orderId);
            if (!order.getBuyer().getId().equals(userId)) {
                log.warn("User {} attempted to access order {} belonging to user {}", 
                        userId, orderId, order.getBuyer().getId());
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "You don't have permission to view this order"));
            }
            
            log.info("Retrieved order {} details for user {}", orderId, userId);
            return ResponseEntity.ok(order);
        } catch (IllegalArgumentException e) {
            log.warn("Order not found: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error getting order details: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get order details"));
        }
    }

    @GetMapping("/listing/{listingId}/available")
    public ResponseEntity<?> checkListingAvailability(@PathVariable Long listingId) {
        try {
            boolean available = orderService.isListingAvailable(listingId);
            return ResponseEntity.ok(Map.of(
                    "listingId", listingId,
                    "available", available
            ));
        } catch (IllegalArgumentException e) {
            log.warn("Listing not found: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error checking listing availability: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to check availability"));
        }
    }
}
