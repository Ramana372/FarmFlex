package com.example.Service;

import com.example.Model.Listing;
import com.example.Repo.ListingRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    @Value("${razorpay.key-id}")
    private String keyId;

    @Value("${razorpay.key-secret}")
    private String keySecret;

    private final ListingRepository listingRepository;
    private final OrderService orderService;

    public JSONObject createRazorpayOrder(Long listingId, LocalDate rentStartDate, LocalDate rentEndDate) {
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new IllegalArgumentException("Listing not found"));

        if (listing.getStatus() != Listing.ListingStatus.LIVE) {
            throw new IllegalArgumentException("Listing is not available for purchase");
        }

        BigDecimal amount = orderService.calculateAmount(listing, rentStartDate, rentEndDate);

        try {
            RazorpayClient razorpayClient = new RazorpayClient(keyId, keySecret);
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amount.multiply(BigDecimal.valueOf(100)).longValue());
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "listing_" + listingId + "_" + System.currentTimeMillis());
            
            Order razorpayOrder = razorpayClient.orders.create(orderRequest);
            
            JSONObject response = new JSONObject();
            response.put("id", razorpayOrder.get("id").toString());
            response.put("amount", razorpayOrder.get("amount").toString());
            response.put("currency", razorpayOrder.get("currency").toString());
            
            log.info("Razorpay order created: {} for listing {} with amount: {}", 
                    response.get("id"), listingId, amount);
            return response;
        } catch (Exception e) {
            log.error("Failed to create Razorpay order for listing {}: {}", listingId, e.getMessage());
            throw new IllegalArgumentException("Failed to create payment order", e);
        }
    }

    public boolean verifySignature(String orderId, String paymentId, String signature) {
        try {
            String payload = orderId + "|" + paymentId;
            boolean isValid = Utils.verifySignature(payload, signature, keySecret);
            
            if (isValid) {
                log.info("Payment signature verified for order: {}, payment: {}", orderId, paymentId);
            } else {
                log.warn("Payment signature verification failed for order: {}", orderId);
            }
            
            return isValid;
        } catch (Exception e) {
            log.error("Error verifying payment signature: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Get Razorpay key ID for frontend
     * Frontend uses this to initialize Razorpay payment modal
     */
    public String getKeyId() {
        return keyId;
    }
}
