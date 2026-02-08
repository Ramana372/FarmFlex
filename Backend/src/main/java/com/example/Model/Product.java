package com.example.Model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Deprecated
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "seller_id", nullable = true)
    private User seller; // Farmer who listed the product

    private String productName;
    private String description;
    private String category; // Tractor, Plough, Harvester, etc.
    private String condition; // New, Used, Refurbished
    private Double price;
    private Integer quantity;
    private String location;
    private String contactPhone;

    @Enumerated(EnumType.STRING)
    private ApprovalStatus approvalStatus = ApprovalStatus.PENDING; // PENDING, APPROVED, REJECTED

    private String rejectionReason;

    @ManyToOne
    @JoinColumn(name = "approved_by_admin")
    private User approvedByAdmin;

    private LocalDateTime approvedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum ApprovalStatus {
        PENDING,    // Waiting for admin approval
        APPROVED,   // Approved and visible to buyers
        REJECTED    // Rejected by admin
    }
}
