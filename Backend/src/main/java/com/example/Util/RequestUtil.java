package com.example.Util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import jakarta.servlet.http.HttpServletRequest;

/**
 * RequestUtil - Utility class for extracting request information
 * Safely retrieves client IP address from various sources
 */
@Component
@Slf4j
public class RequestUtil {

    /**
     * Extract client IP address from the request
     * Checks X-Forwarded-For header (for proxy/load balancer scenarios)
     * Falls back to direct client address
     */
    public static String getClientIpAddress(HttpServletRequest request) {
        try {
            // Check if request is behind a proxy
            String xForwardedFor = request.getHeader("X-Forwarded-For");
            if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
                // X-Forwarded-For can contain multiple IPs, get the first one
                return xForwardedFor.split(",")[0].trim();
            }

            String xRealIp = request.getHeader("X-Real-IP");
            if (xRealIp != null && !xRealIp.isEmpty()) {
                return xRealIp;
            }

            // Fallback to remote address
            String remoteAddr = request.getRemoteAddr();
            return remoteAddr != null ? remoteAddr : "Unknown";
        } catch (Exception e) {
            log.warn("Failed to extract client IP address: {}", e.getMessage());
            return "Unknown";
        }
    }

    /**
     * Get user agent from request
     */
    public static String getUserAgent(HttpServletRequest request) {
        try {
            String userAgent = request.getHeader("User-Agent");
            return userAgent != null ? userAgent : "Unknown";
        } catch (Exception e) {
            return "Unknown";
        }
    }

    /**
     * Get request origin/referer
     */
    public static String getReferer(HttpServletRequest request) {
        try {
            String referer = request.getHeader("Referer");
            return referer != null ? referer : "Direct";
        } catch (Exception e) {
            return "Unknown";
        }
    }
}
