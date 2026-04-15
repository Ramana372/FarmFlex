package com.example.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
public class OrderResponse {

    private Long id;
    private UserResponse buyer;
    private ListingResponse listing;
    private BigDecimal amount;
    private String paymentId;
    private String paymentStatus;
    private LocalDate rentStartDate;
    private LocalDate rentEndDate;
    private LocalDateTime createdAt;
    public OrderResponse() {}

    public OrderResponse(Long id, UserResponse buyer, ListingResponse listing, BigDecimal amount,
                        String paymentId, String paymentStatus, LocalDate rentStartDate,
                        LocalDate rentEndDate, LocalDateTime createdAt) {
        this.id = id;
        this.buyer = buyer;
        this.listing = listing;
        this.amount = amount;
        this.paymentId = paymentId;
        this.paymentStatus = paymentStatus;
        this.rentStartDate = rentStartDate;
        this.rentEndDate = rentEndDate;
        this.createdAt = createdAt;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public UserResponse getBuyer() { return buyer; }
    public void setBuyer(UserResponse buyer) { this.buyer = buyer; }

    public ListingResponse getListing() { return listing; }
    public void setListing(ListingResponse listing) { this.listing = listing; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public LocalDate getRentStartDate() { return rentStartDate; }
    public void setRentStartDate(LocalDate rentStartDate) { this.rentStartDate = rentStartDate; }

    public LocalDate getRentEndDate() { return rentEndDate; }
    public void setRentEndDate(LocalDate rentEndDate) { this.rentEndDate = rentEndDate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
