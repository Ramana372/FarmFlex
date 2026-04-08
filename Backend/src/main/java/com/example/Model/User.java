package com.example.Model;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String phone;

    private String location;

    private String state;

    @Column(unique = true)
    private String email;

    @JsonIgnore
    @Column(name = "password_hash", nullable = false)
    private String password;

    @Column(name = "password")
    private String passwordOld; // Legacy column support

    @Enumerated(EnumType.STRING)
    private UserRole role; // FARMER, ADMIN

    @Column(nullable = true)
    private Boolean emailVerified = false;

    @JsonIgnore
    private String emailVerificationToken;

    @JsonIgnore
    private LocalDateTime emailVerificationTokenExpiry;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "is_verified")
    private Boolean isVerified = false;

    @Column(name = "profile_picture_url")
    private String profilePictureUrl;

    private BigDecimal rating = BigDecimal.ZERO;

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

    public enum UserRole {
        FARMER,  // Equipment provider
        ADMIN    // System administrator
    }
}
