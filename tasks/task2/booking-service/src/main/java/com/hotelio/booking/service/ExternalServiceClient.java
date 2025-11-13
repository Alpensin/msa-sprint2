package com.hotelio.booking.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ExternalServiceClient {

    private static final Logger log = LoggerFactory.getLogger(ExternalServiceClient.class);

    /**
     * Stub implementation - in real scenario would call user service via gRPC/REST
     */
    public boolean isUserActive(String userId) {
        log.debug("Checking if user {} is active (stub)", userId);
        // Stub implementation - assume all users are active
        return true;
    }

    /**
     * Stub implementation - in real scenario would call user service via gRPC/REST
     */
    public boolean isUserBlacklisted(String userId) {
        log.debug("Checking if user {} is blacklisted (stub)", userId);
        // Stub implementation - assume no users are blacklisted
        return false;
    }

    /**
     * Stub implementation - in real scenario would call user service via gRPC/REST
     */
    public String getUserStatus(String userId) {
        log.debug("Getting status for user {} (stub)", userId);
        // Stub implementation - return VIP for even user IDs, null for odd
        return userId.hashCode() % 2 == 0 ? "VIP" : null;
    }

    /**
     * Stub implementation - in real scenario would call hotel service via gRPC/REST
     */
    public boolean isHotelOperational(String hotelId) {
        log.debug("Checking if hotel {} is operational (stub)", hotelId);
        // Stub implementation - assume all hotels are operational
        return true;
    }

    /**
     * Stub implementation - in real scenario would call review service via gRPC/REST
     */
    public boolean isTrustedHotel(String hotelId) {
        log.debug("Checking if hotel {} is trusted (stub)", hotelId);
        // Stub implementation - assume all hotels are trusted
        return true;
    }

    /**
     * Stub implementation - in real scenario would call hotel service via gRPC/REST
     */
    public boolean isHotelFullyBooked(String hotelId) {
        log.debug("Checking if hotel {} is fully booked (stub)", hotelId);
        // Stub implementation - assume no hotels are fully booked
        return false;
    }

    /**
     * Stub implementation - in real scenario would call promo service via gRPC/REST
     */
    public Double validatePromoCode(String promoCode, String userId) {
        log.debug("Validating promo code {} for user {} (stub)", promoCode, userId);
        // Stub implementation - return 10% discount for VIP users, 5% for others
        String status = getUserStatus(userId);
        if ("VIP".equalsIgnoreCase(status)) {
            return 10.0;
        } else {
            return 5.0;
        }
    }
}