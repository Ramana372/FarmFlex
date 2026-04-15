package com.example.Repo;

import com.example.Model.ListingImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository for ListingImage entity
 */
public interface ListingImageRepository extends JpaRepository<ListingImage, Long> {
    List<ListingImage> findByListingId(Long listingId);
}