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

    @Value("${jwt.expiration:86400000}")
    private long jwtExpiration;

    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes();
        if (keyBytes.length < 64) {
            log.warn("JWT secret key is shorter than recommended 512 bits (needed: 64 bytes, actual: {} bytes). Consider using a stronger secret.", keyBytes.length);
            byte[] paddedKey = new byte[64];
            System.arraycopy(keyBytes, 0, paddedKey, 0, keyBytes.length);
            keyBytes = paddedKey;
        } else {
            log.debug("JWT secret key length: {} bytes ({} bits) - HS512 requirement met", keyBytes.length, keyBytes.length * 8);
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }
    public String generateToken(String userId, String email, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("role", role);
        return createToken(claims, userId);
    }

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

    public String generatePasswordResetToken(String userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "PASSWORD_RESET");

        try {
            return Jwts.builder()
                    .setClaims(claims)
                    .setSubject(userId)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                    .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                    .compact();
        } catch (Exception e) {
            log.error("Failed to generate password reset token", e);
            throw new JwtTokenException("Failed to generate password reset token", e);
        }
    }
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

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            log.debug("JWT token validation successful");
            return true;
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            log.warn("JWT token has expired - issued at: {}, expires at: {}, current time: {}", 
                e.getClaims().getIssuedAt(), e.getClaims().getExpiration(), new Date());
            return false;
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

    public String getUserIdFromToken(String token) {
        try {
            Claims claims = getAllClaimsFromToken(token);
            return claims.getSubject();
        } catch (Exception e) {
            log.error("Failed to extract userId from token", e);
            throw new JwtTokenException("Failed to extract userId from token", e);
        }
    }

    public String getEmailFromToken(String token) {
        try {
            Claims claims = getAllClaimsFromToken(token);
            return (String) claims.get("email");
        } catch (Exception e) {
            log.error("Failed to extract email from token", e);
            throw new JwtTokenException("Failed to extract email from token", e);
        }
    }

    public String getRoleFromToken(String token) {
        try {
            Claims claims = getAllClaimsFromToken(token);
            return (String) claims.get("role");
        } catch (Exception e) {
            log.error("Failed to extract role from token", e);
            throw new JwtTokenException("Failed to extract role from token", e);
        }
    }

    public boolean isTokenExpired(String token) {
        try {
            Claims claims = getAllClaimsFromToken(token);
            Date expiration = claims.getExpiration();
            Date currentTime = new Date();
            boolean isExpired = expiration.before(currentTime);
            
            if (isExpired) {
                long expirationDiff = currentTime.getTime() - expiration.getTime();
                log.debug("Token expired {} milliseconds ({} seconds) ago", expirationDiff, expirationDiff / 1000);
            }
            return isExpired;
        } catch (Exception e) {
            log.warn("Error checking token expiration: {}", e.getMessage());
            return true; // Treat as expired if we can't verify
        }
    }

    /**
     * Get token debug info for troubleshooting expiration issues
     */
    public String getTokenDebugInfo(String token) {
        try {
            Claims claims = getAllClaimsFromToken(token);
            Date issuedAt = claims.getIssuedAt();
            Date expiration = claims.getExpiration();
            Date currentTime = new Date();
            long tokenAge = currentTime.getTime() - issuedAt.getTime();
            long timeUntilExpiration = expiration.getTime() - currentTime.getTime();
            
            return String.format(
                "Token Debug: Issued at %s, Expires at %s, Current time %s, Age: %d ms, TTL: %d ms", 
                issuedAt, expiration, currentTime, tokenAge, timeUntilExpiration);
        } catch (Exception e) {
            return "Token Debug Error: " + e.getMessage();
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
