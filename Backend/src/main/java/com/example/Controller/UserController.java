package com.example.Controller;

import com.example.Model.User;
import com.example.Repo.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class UserController {
    
    private final UserRepo userRepository;
    @GetMapping("/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getUserProfile(@PathVariable Long userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            
            Map<String, Object> response = new HashMap<>();
            response.put("id", user.getId());
            response.put("name", user.getName());
            response.put("email", user.getEmail());
            response.put("phone", user.getPhone());
            response.put("location", user.getLocation());
            response.put("role", user.getRole());
            response.put("emailVerified", user.getEmailVerified());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting user profile: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "User not found"));
        }
    }

    @GetMapping("/role/farmer")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllFarmers() {
        try {
            List<User> farmers = userRepository.findByRole(User.UserRole.FARMER);
            return ResponseEntity.ok(farmers);
        } catch (Exception e) {
            log.error("Error getting farmers: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get farmers"));
        }
    }
}
