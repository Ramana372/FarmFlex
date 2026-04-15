package com.example.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "listings")
public class Listing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 4000)
    private String description;

    @Enumerated(EnumType.STRING)
    private ListingCategory category;

    @Enumerated(EnumType.STRING)
    private ListingType type;

    @Enumerated(EnumType.STRING)
    private ListingStatus status = ListingStatus.PENDING;

    private BigDecimal salePrice;

    private BigDecimal rentPricePerDay;

    private String location;

    @OneToMany(mappedBy = "listing", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("listing")
    private List<ListingImage> images = new ArrayList<>();

    // Helper method to get image URLs for JSON serialization
    public List<String> getImageUrls() {
        if (images == null || images.isEmpty()) {
            return new ArrayList<>();
        }
        return images.stream()
                .map(ListingImage::getImageUrl)
                .collect(Collectors.toList());
    }

    // Helper method to set image URLs
    public void setImageUrls(List<String> imageUrls) {
        if (imageUrls == null || imageUrls.isEmpty()) {
            this.images = new ArrayList<>();
            return;
        }
        this.images = imageUrls.stream()
                .map(url -> {
                    ListingImage img = new ListingImage();
                    img.setImageUrl(url);
                    img.setListing(this);
                    return img;
                })
                .collect(Collectors.toList());
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id")
    private User owner;

    private String rejectionReason;

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

    public enum ListingCategory {
        TRACTOR,
        HARVESTER,
        PLOUGH,
        SEEDER,
        SPRAYER,
        THRESHER,
        GRAINS,
        VEGETABLES,
        SEEDS,
        LIVESTOCK,
        EQUIPMENT,
        OTHER
    }

    public enum ListingType {
        RENT,
        SALE
    }

    public enum ListingStatus {
        PENDING,
        APPROVED,
        LIVE,
        REJECTED,
        SOLD,
        BOOKED
    }
}
