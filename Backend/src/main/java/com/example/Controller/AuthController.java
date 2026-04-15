package com.example.Controller;

import com.example.Model.User;
import com.example.Repo.UserRepo;
import com.example.Security.JwtTokenProvider;
import com.example.Service.EmailService;
import com.example.Util.RequestUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class AuthController {

    private final UserRepo userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final EmailService emailService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Email already registered"));
            }

            User user = new User();
            user.setName(request.getName());
            user.setEmail(request.getEmail());
            user.setPhone(request.getPhone());
            user.setLocation(request.getLocation());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            User.UserRole role;
            try {
                role = request.getRole() == null || request.getRole().isBlank()
                        ? User.UserRole.FARMER
                        : User.UserRole.valueOf(request.getRole().toUpperCase());
            } catch (IllegalArgumentException ex) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid role"));
            }
            user.setRole(role);
            user.setEmailVerified(true);
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());

            User savedUser = userRepository.save(user);
            log.info("New user registered: {} with email: {}", user.getName(), user.getEmail());

            String token = jwtTokenProvider.generateToken(
                    savedUser.getId().toString(),
                    savedUser.getEmail(),
                    savedUser.getRole().toString()
            );

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Registration successful! You can now login.");
            response.put("token", token);
            response.put("userId", savedUser.getId());
            response.put("name", savedUser.getName());
            response.put("email", savedUser.getEmail());
            response.put("role", savedUser.getRole().toString());
            response.put("phone", savedUser.getPhone());
            response.put("location", savedUser.getLocation());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Registration error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Registration failed: " + e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        try {
            Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
            if (userOpt.isEmpty()) {
                log.warn("Login attempt with non-existent email: {}", request.getEmail());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid email or password"));
            }

            User user = userOpt.get();

            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                log.warn("Failed login attempt for user: {}", request.getEmail());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid email or password"));
            }

            String token = jwtTokenProvider.generateToken(
                    user.getId().toString(),
                    user.getEmail(),
                    user.getRole().toString()
            );

            log.info("User logged in successfully: {} ({})", user.getName(), user.getRole());

            String clientIp = RequestUtil.getClientIpAddress(httpRequest);
            String userAgent = RequestUtil.getUserAgent(httpRequest);

            try {
                emailService.sendLoginNotificationEmail(
                        user.getEmail(),
                        user.getName(),
                        clientIp,
                        userAgent
                );
                log.debug("Login notification email queued for: {}", user.getEmail());
            } catch (Exception e) {
                log.warn("Failed to queue login notification email for: {} - {}", user.getEmail(), e.getMessage());
            }

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("userId", user.getId());
            response.put("name", user.getName());
            response.put("email", user.getEmail());
            response.put("role", user.getRole().toString());
            response.put("phone", user.getPhone());
            response.put("location", user.getLocation());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Login error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Login failed"));
        }
    }

    @GetMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam String token) {
        try {
            Optional<User> userOpt = userRepository.findByEmailVerificationToken(token);

            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid verification token"));
            }

            User user = userOpt.get();

            if (user.getEmailVerificationTokenExpiry().isBefore(LocalDateTime.now())) {
                return ResponseEntity.badRequest().body(Map.of("error", "Verification token has expired"));
            }

            user.setEmailVerified(true);
            user.setEmailVerificationToken(null);
            user.setEmailVerificationTokenExpiry(null);
            userRepository.save(user);

            log.info("Email verified for user: {}", user.getName());

            try {
                emailService.sendWelcomeEmail(user.getEmail(), user.getName(), user.getRole().toString());
            } catch (Exception e) {
                log.error("Failed to send welcome email", e);
            }

            return ResponseEntity.ok(Map.of("message", "Email verified successfully. You can now log in."));
        } catch (Exception e) {
            log.error("Email verification error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Email verification failed"));
        }
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<?> resendVerification(@RequestBody ResendVerificationRequest request) {
        try {
            Optional<User> userOpt = userRepository.findByEmail(request.getEmail());

            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "User not found"));
            }

            User user = userOpt.get();

            if (user.getEmailVerified()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Email already verified"));
            }

            String verificationToken = UUID.randomUUID().toString();
            user.setEmailVerificationToken(verificationToken);
            user.setEmailVerificationTokenExpiry(LocalDateTime.now().plusHours(24));
            userRepository.save(user);

            emailService.sendVerificationEmail(user.getEmail(), user.getName(), verificationToken);

            log.info("Verification email resent to: {}", user.getEmail());

            return ResponseEntity.ok(Map.of("message", "Verification email sent successfully"));
        } catch (Exception e) {
            log.error("Resend verification error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to resend verification email"));
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        try {
            Optional<User> userOpt = userRepository.findByEmail(request.getEmail());

            if (userOpt.isEmpty()) {
                return ResponseEntity.ok(Map.of("message", "If email exists, password reset link has been sent"));
            }

            User user = userOpt.get();
            String resetToken = jwtTokenProvider.generatePasswordResetToken(user.getId().toString());

            emailService.sendPasswordResetEmail(user.getEmail(), user.getName(), resetToken);

            log.info("Password reset email sent to: {}", user.getEmail());

            return ResponseEntity.ok(Map.of("message", "If email exists, password reset link has been sent"));
        } catch (Exception e) {
            log.error("Forgot password error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Password reset failed"));
        }
    }

    public static class RegisterRequest {
        @NotBlank
        @Size(min = 2, max = 120)
        public String name;

        @NotBlank
        @Email
        public String email;

        @NotBlank
        @Size(min = 6, max = 120)
        public String password;

        @NotBlank
        public String phone;

        @NotBlank
        public String location;

        public String role;

        public String getName() { return name; }
        public String getEmail() { return email; }
        public String getPassword() { return password; }
        public String getPhone() { return phone; }
        public String getLocation() { return location; }
        public String getRole() { return role; }
    }

    public static class LoginRequest {
        public String email;
        public String password;

        public String getEmail() { return email; }
        public String getPassword() { return password; }
    }

    public static class ResendVerificationRequest {
        public String email;

        public String getEmail() { return email; }
    }

    public static class ForgotPasswordRequest {
        public String email;

        public String getEmail() { return email; }
    }
}
