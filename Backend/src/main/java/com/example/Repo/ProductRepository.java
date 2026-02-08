package com.example.Repo;

import com.example.Model.Product;
import com.example.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

@Deprecated
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findBySeller(User seller);
    List<Product> findByApprovalStatus(Product.ApprovalStatus status);
    List<Product> findBySellerAndApprovalStatus(User seller, Product.ApprovalStatus status);
    List<Product> findByCategory(String category);
    Optional<Product> findByIdAndApprovalStatus(Long id, Product.ApprovalStatus status);
}
