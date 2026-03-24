package com.example.Security;

import com.example.Exception.JwtTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JwtTokenProvider - Handles JWT token creation, validation, and parsing
 * Uses HS512 signature algorithm with 512-bit secret key
 */
@Component
@Slf4j
public class JwtTokenProvider {

    @Value("${jwt.secret:agrimart-secret-key-for-jwt-authentication-2024-agrimart-secure-key-12345}")
    private String jwtSecret;

    @Value("${jwt.expiration:86400000}") // 24 hours in milliseconds
    private long jwtExpiration;

    /**
     * Get properly formatted signing key
     * Ensures key is at least 64 bytes (512 bits) for HS512
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes();
        if (keyBytes.length < 64) {
            // Pad the key if it's too short (not recommended for production)
            byte[] paddedKey = new byte[64];
            System.arraycopy(keyBytes, 0, paddedKey, 0, keyBytes.length);
            keyBytes = paddedKey;
            log.warn("JWT secret key is shorter than recommended 512 bits");
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Generate JWT token with user claims
     * Standard login token with 24-hour expiration
     */
    public String generateToken(String userId, String email, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("role", role);
        return createToken(claims, userId);
    }

    /**
     * Generate email verification token (shorter expiration)
     * Used for email verification process
     */
    public String generateVerificationToken(String email) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "EMAIL_VERIFICATION");
        claims.put("email", email);

        try {
            return Jwts.builder()
                    .setClaims(claims)
                    .setSubject(email)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 24 hours
                    .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                    .compact();
        } catch (Exception e) {
            log.error("Failed to generate verification token", e);
            throw new JwtTokenException("Failed to generate verification token", e);
        }
    }

    /**
     * Generate password reset token
     * Short-lived token (1 hour expiration)
     */
    public String generatePasswordResetToken(String userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "PASSWORD_RESET");

        try {
            return Jwts.builder()
                    .setClaims(claims)
                    .setSubject(userId)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + 3600000)) // 1 hour
                    .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                    .compact();
        } catch (Exception e) {
            log.error("Failed to generate password reset token", e);
            throw new JwtTokenException("Failed to generate password reset token", e);
        }
    }

    /**
     * Create token with claims and subject
     */
    private String createToken(Map<String, Object> claims, String subject) {
        try {
            return Jwts.builder()
                    .setClaims(claims)
                    .setSubject(subject)
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                    .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                    .compact();
        } catch (Exception e) {
            log.error("Failed to create JWT token", e);
            throw new JwtTokenException("Failed to create JWT token", e);
        }
    }

    /**
     * Validate JWT token
     * Returns true if token is valid and not expired
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            log.debug("JWT token validation successful");
            return true;
        } catch (JwtException e) {
            log.warn("JWT token validation failed: {}", e.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            log.warn("JWT token validation failed - illegal argument: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            log.warn("JWT token validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Extract user ID from token
     */
    public String getUserIdFromToken(String token) {
        try {
            Claims claims = getAllClaimsFromToken(token);
            return claims.getSubject();
        } catch (Exception e) {
            log.error("Failed to extract userId from token", e);
            throw new JwtTokenException("Failed to extract userId from token", e);
        }
    }

    /**
     * Extract email from token
     */
    public String getEmailFromToken(String token) {
        try {
            Claims claims = getAllClaimsFromToken(token);
            return (String) claims.get("email");
        } catch (Exception e) {
            log.error("Failed to extract email from token", e);
            throw new JwtTokenException("Failed to extract email from token", e);
        }
    }

    /**
     * Extract role from token
     */
    public String getRoleFromToken(String token) {
        try {
            Claims claims = getAllClaimsFromToken(token);
            return (String) claims.get("role");
        } catch (Exception e) {
            log.error("Failed to extract role from token", e);
            throw new JwtTokenException("Failed to extract role from token", e);
        }
    }

    /**
     * Check if token is expired
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = getAllClaimsFromToken(token);
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            log.warn("Error checking token expiration: {}", e.getMessage());
            return true; // Treat as expired if we can't verify
        }
    }

    /**
     * Extract all claims from token
     */
    private Claims getAllClaimsFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            log.error("Failed to extract claims from token", e);
            throw new JwtTokenException("Failed to extract claims from token", e);
        }
    }
}
