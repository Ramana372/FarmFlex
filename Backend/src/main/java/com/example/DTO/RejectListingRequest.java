package com.example.DTO;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO for rejecting a listing
 */
public class RejectListingRequest {

    @NotBlank(message = "Rejection reason is required")
    private String reason;

    // Getters and Setters
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
