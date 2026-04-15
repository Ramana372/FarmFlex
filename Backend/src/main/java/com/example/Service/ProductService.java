package com.example.Service;

import com.example.Model.Product;
import com.example.Model.User;
import com.example.Repo.ProductRepository;
import com.example.Repo.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Deprecated
@Slf4j
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepo userRepository;

    public Product createProduct(Long farmerId, String productName, String description, 
                                  String category, String condition, Double price, 
                                  Integer quantity, String location, String contactPhone) {
        User farmer = userRepository.findById(farmerId)
                .orElseThrow(() -> new IllegalArgumentException("Farmer not found"));

        if (!farmer.getRole().equals(User.UserRole.FARMER)) {
            throw new IllegalArgumentException("Only farmers can create products");
        }

        Product product = new Product();
        product.setSeller(farmer);
        product.setProductName(productName);
        product.setDescription(description);
        product.setCategory(category);
        product.setCondition(condition);
        product.setPrice(price);
        product.setQuantity(quantity);
        product.setLocation(location);
        product.setContactPhone(contactPhone);
        product.setApprovalStatus(Product.ApprovalStatus.PENDING);

        Product savedProduct = productRepository.save(product);
        log.info("Product created by farmer {}: {}", farmerId, savedProduct.getId());

        return savedProduct;
    }

    public List<Product> getApprovedProducts() {
        return productRepository.findByApprovalStatus(Product.ApprovalStatus.APPROVED);
    }

    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    /**
     * Get specific approved product
     */
    public Product getApprovedProductById(Long productId) {
        return productRepository.findById(productId)
                .filter(p -> p.getApprovalStatus().equals(Product.ApprovalStatus.APPROVED))
                .orElseThrow(() -> new IllegalArgumentException("Product not found or not approved"));
    }

    /**
     * Farmer views their own products
     */
    public List<Product> getMyProducts(Long farmerId) {
        User farmer = userRepository.findById(farmerId)
                .orElseThrow(() -> new IllegalArgumentException("Farmer not found"));
        return productRepository.findBySeller(farmer);
    }

    /**
     * Get pending products for admin approval
     */
    public List<Product> getPendingProducts() {
        return productRepository.findByApprovalStatus(Product.ApprovalStatus.PENDING);
    }

    /**
     * Admin approves a product
     */
    public Product approveProduct(Long productId, Long adminId) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new IllegalArgumentException("Admin not found"));
        if (!admin.getRole().equals(User.UserRole.ADMIN)) {
            throw new IllegalArgumentException("Only admins can approve products");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        product.setApprovalStatus(Product.ApprovalStatus.APPROVED);
        product.setApprovedByAdmin(admin);
        product.setApprovedAt(LocalDateTime.now());

        Product savedProduct = productRepository.save(product);
        log.info("Product {} approved by admin {}", productId, adminId);

        return savedProduct;
    }

    /**
     * Admin rejects a product
     */
    public Product rejectProduct(Long productId, Long adminId, String rejectionReason) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new IllegalArgumentException("Admin not found"));

        if (!admin.getRole().equals(User.UserRole.ADMIN)) {
            throw new IllegalArgumentException("Only admins can reject products");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        product.setApprovalStatus(Product.ApprovalStatus.REJECTED);
        product.setRejectionReason(rejectionReason);
        product.setApprovedByAdmin(admin);
        product.setApprovedAt(LocalDateTime.now());

        Product savedProduct = productRepository.save(product);
        log.info("Product {} rejected by admin {}", productId, adminId);

        return savedProduct;
    }

    /**
     * Update product listing
     */
    public Product updateProduct(Long productId, Long farmerId, String productName, 
                                 String description, Double price, Integer quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        if (!product.getSeller().getId().equals(farmerId)) {
            throw new IllegalArgumentException("You can only edit your own products");
        }

        product.setProductName(productName);
        product.setDescription(description);
        product.setPrice(price);
        product.setQuantity(quantity);

        // Reset to pending if modified
        if (!product.getApprovalStatus().equals(Product.ApprovalStatus.PENDING)) {
            product.setApprovalStatus(Product.ApprovalStatus.PENDING);
        }

        return productRepository.save(product);
    }

    /**
     * Delete product
     */
    public void deleteProduct(Long productId, Long farmerId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        if (!product.getSeller().getId().equals(farmerId)) {
            throw new IllegalArgumentException("You can only delete your own products");
        }

        productRepository.delete(product);
        log.info("Product {} deleted by farmer {}", productId, farmerId);
    }
}
