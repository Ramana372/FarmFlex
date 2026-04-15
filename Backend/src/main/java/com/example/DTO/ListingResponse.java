package com.example.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
public class ListingResponse {

    private Long id;
    private String title;
    private String description;
    private String category;
    private String type;
    private String status;
    private BigDecimal salePrice;
    private BigDecimal rentPricePerDay;
    private String location;
    private List<String> imageUrls;
    private UserResponse owner;
    private String rejectionReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public ListingResponse() {}

    public ListingResponse(Long id, String title, String description, String category, String type,
                          String status, BigDecimal salePrice, BigDecimal rentPricePerDay,
                          String location, List<String> imageUrls, UserResponse owner,
                          String rejectionReason, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.type = type;
        this.status = status;
        this.salePrice = salePrice;
        this.rentPricePerDay = rentPricePerDay;
        this.location = location;
        this.imageUrls = imageUrls;
        this.owner = owner;
        this.rejectionReason = rejectionReason;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public BigDecimal getSalePrice() { return salePrice; }
    public void setSalePrice(BigDecimal salePrice) { this.salePrice = salePrice; }

    public BigDecimal getRentPricePerDay() { return rentPricePerDay; }
    public void setRentPricePerDay(BigDecimal rentPricePerDay) { this.rentPricePerDay = rentPricePerDay; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public List<String> getImageUrls() { return imageUrls; }
    public void setImageUrls(List<String> imageUrls) { this.imageUrls = imageUrls; }

    public UserResponse getOwner() { return owner; }
    public void setOwner(UserResponse owner) { this.owner = owner; }

    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
