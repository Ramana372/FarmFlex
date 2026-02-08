package com.example.DTO;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * DTO for creating a new listing
 */
public class ListingCreateRequest {

    @NotBlank(message = "Title is required")
    @Size(min = 5, max = 200, message = "Title must be between 5 and 200 characters")
    private String title;

    @NotBlank(message = "Description is required")
    @Size(min = 10, max = 5000, message = "Description must be between 10 and 5000 characters")
    private String description;

    @NotNull(message = "Category is required")
    private String category;

    @NotNull(message = "Listing type is required")
    private String type; // RENT or SALE

    @Positive(message = "Sale price must be positive")
    private BigDecimal salePrice;

    @Positive(message = "Rent price per day must be positive")
    private BigDecimal rentPricePerDay;

    @NotBlank(message = "Location is required")
    private String location;

    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public BigDecimal getSalePrice() { return salePrice; }
    public void setSalePrice(BigDecimal salePrice) { this.salePrice = salePrice; }

    public BigDecimal getRentPricePerDay() { return rentPricePerDay; }
    public void setRentPricePerDay(BigDecimal rentPricePerDay) { this.rentPricePerDay = rentPricePerDay; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
}
