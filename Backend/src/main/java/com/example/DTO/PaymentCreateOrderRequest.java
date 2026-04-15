package com.example.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
public class PaymentCreateOrderRequest {

    @NotNull(message = "Listing ID is required")
    private Long listingId;

    private LocalDate rentStartDate;

    private LocalDate rentEndDate;

    public Long getListingId() { return listingId; }
    public void setListingId(Long listingId) { this.listingId = listingId; }

    public LocalDate getRentStartDate() { return rentStartDate; }
    public void setRentStartDate(LocalDate rentStartDate) { this.rentStartDate = rentStartDate; }

    public LocalDate getRentEndDate() { return rentEndDate; }
    public void setRentEndDate(LocalDate rentEndDate) { this.rentEndDate = rentEndDate; }
}
