package com.example.Controller;

import com.example.DTO.PaymentConfirmRequest;
import com.example.DTO.PaymentCreateOrderRequest;
import com.example.Model.Listing;
import com.example.Model.Order;
import com.example.Model.User;
import com.example.Repo.ListingRepository;
import com.example.Repo.UserRepo;
import com.example.Service.OrderService;
import com.example.Service.PaymentService;
import com.example.Service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

/**
 * PaymentController - UPI payment endpoints
 * Handles order creation and payment confirmation for RENT and SALE listings
 */
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class PaymentController {

    private final PaymentService paymentService;
    private final OrderService orderService;
    private final ListingRepository listingRepository;
    private final UserRepo userRepository;
    private final EmailService emailService;

    /**
     * Create a Razorpay order for a listing
     * For RENT: requires rentStartDate and rentEndDate
     * For SALE: amount = salePrice
     */
    @PostMapping("/create-order")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createOrder(@RequestBody PaymentCreateOrderRequest request) {
        try {
            // Get the listing
            Listing listing = listingRepository.findById(request.getListingId())
                    .orElseThrow(() -> new IllegalArgumentException("Listing not found"));

            // Check if listing is available for purchase
            if (listing.getStatus() != Listing.ListingStatus.LIVE) {
                return ResponseEntity.badRequest().body(Map.of("error", "This listing is not available for purchase"));
            }

            // Create Razorpay order
            JSONObject razorpayOrder = paymentService.createRazorpayOrder(
                    request.getListingId(),
                    request.getRentStartDate(),
                    request.getRentEndDate()
            );

            // Calculate final amount
            BigDecimal amount = orderService.calculateAmount(listing, request.getRentStartDate(), request.getRentEndDate());

            log.info("Created Razorpay order {} for listing {}", razorpayOrder.get("id"), request.getListingId());

            return ResponseEntity.ok(Map.of(
                "orderId", razorpayOrder.get("id"),
                "amount", amount,
                "amountInPaise", razorpayOrder.get("amount"),
                "currency", razorpayOrder.get("currency"),
                "keyId", paymentService.getKeyId(),
                "listingId", listing.getId(),
                "listingType", listing.getType().name(),
                "listingTitle", listing.getTitle()
            ));
        } catch (IllegalArgumentException e) {
            log.warn("Invalid order request: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error creating Razorpay order: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create payment order"));
        }
    }

    /**
     * Confirm payment after successful UPI/Bank transaction
     * For UPI payments, signature can be 'upi_verified'
     */
    @PostMapping("/confirm")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> confirmPayment(
            @RequestAttribute("userId") Long userId,
            @RequestBody PaymentConfirmRequest request) {
        try {
            // For UPI payments, signature is 'upi_verified'
            boolean isUPIPayment = "upi_verified".equals(request.getSignature());
            
            // Verify payment (skip Razorpay verification for UPI)
            boolean verified = isUPIPayment || paymentService.verifySignature(
                    request.getOrderId(),
                    request.getPaymentId(),
                    request.getSignature()
            );

            if (!verified) {
                log.warn("Payment signature verification failed for order: {}", request.getOrderId());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Payment verification failed"));
            }

            // Get the listing
            Listing listing = listingRepository.findById(request.getListingId())
                    .orElseThrow(() -> new IllegalArgumentException("Listing not found"));

            // Calculate amount
            BigDecimal amount = orderService.calculateAmount(listing, request.getRentStartDate(), request.getRentEndDate());

            // Create order in database
            Order order = orderService.createOrder(
                    userId,
                    request.getListingId(),
                    amount,
                    request.getPaymentId(),
                    "PAID",
                    request.getRentStartDate(),
                    request.getRentEndDate()
            );

            // Get buyer and seller user details
            User buyer = userRepository.findById(userId).orElse(null);
            User seller = listing.getOwner();

            // Send confirmation email to buyer
            if (buyer != null) {
                String typeString = listing.getType().toString();
                String dates = listing.getType().equals(Listing.ListingType.RENT) 
                    ? request.getRentStartDate() + " to " + request.getRentEndDate()
                    : "Immediate";
                emailService.sendOrderConfirmationEmail(
                    buyer.getEmail(),
                    buyer.getName(),
                    listing.getTitle(),
                    order.getId().toString(),
                    amount.toString(),
                    typeString,
                    dates
                );
            }

            // Send notification email to seller
            if (seller != null && buyer != null) {
                String typeString = listing.getType().toString();
                emailService.sendFarmerNotificationEmail(
                    seller.getEmail(),
                    seller.getName(),
                    listing.getTitle(),
                    buyer.getName(),
                    buyer.getPhone(),
                    typeString
                );
            }

            log.info("Payment confirmed for order {}, listing {}, paymentMethod: {}", 
                    request.getPaymentId(), request.getListingId(), isUPIPayment ? "UPI" : "Razorpay");

            return ResponseEntity.ok(Map.of(
                    "message", "Payment successful! Booking confirmed.",
                    "order", order,
                    "listingStatus", listing.getStatus().name()
            ));
        } catch (IllegalArgumentException e) {
            log.warn("Invalid payment confirmation request: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error confirming payment: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to confirm payment"));
        }
    }
}
