package com.example.Util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import jakarta.servlet.http.HttpServletRequest;

@Component
@Slf4j
public class RequestUtil {

    public static String getClientIpAddress(HttpServletRequest request) {
        try {
            String xForwardedFor = request.getHeader("X-Forwarded-For");
            if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
                return xForwardedFor.split(",")[0].trim();
            }

            String xRealIp = request.getHeader("X-Real-IP");
            if (xRealIp != null && !xRealIp.isEmpty()) {
                return xRealIp;
            }

            String remoteAddr = request.getRemoteAddr();
            return remoteAddr != null ? remoteAddr : "Unknown";
        } catch (Exception e) {
            log.warn("Failed to extract client IP address: {}", e.getMessage());
            return "Unknown";
        }
    }

    public static String getUserAgent(HttpServletRequest request) {
        try {
            String userAgent = request.getHeader("User-Agent");
            return userAgent != null ? userAgent : "Unknown";
        } catch (Exception e) {
            return "Unknown";
        }
    }

    public static String getReferer(HttpServletRequest request) {
        try {
            String referer = request.getHeader("Referer");
            return referer != null ? referer : "Direct";
        } catch (Exception e) {
            return "Unknown";
        }
    }
}
