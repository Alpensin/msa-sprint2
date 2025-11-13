package com.hotelio.booking.service;

import com.hotelio.booking.entity.BookingEntity;
import com.hotelio.booking.repository.BookingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class BookingBusinessService {

    private static final Logger log = LoggerFactory.getLogger(BookingBusinessService.class);

    private final BookingRepository bookingRepository;
    private final ExternalServiceClient externalServiceClient;

    @Autowired
    public BookingBusinessService(BookingRepository bookingRepository,
                                 ExternalServiceClient externalServiceClient) {
        this.bookingRepository = bookingRepository;
        this.externalServiceClient = externalServiceClient;
    }

    public BookingEntity createBooking(String userId, String hotelId, String promoCode) {
        log.info("Creating booking: userId={}, hotelId={}, promoCode={}", userId, hotelId, promoCode);

        validateUser(userId);
        validateHotel(hotelId);

        double basePrice = resolveBasePrice(userId);
        double discount = resolvePromoDiscount(promoCode, userId);

        double finalPrice = basePrice - discount;
        log.info("Final price calculated: base={}, discount={}, final={}", basePrice, discount, finalPrice);

        BookingEntity booking = new BookingEntity();
        booking.setUserId(userId);
        booking.setHotelId(hotelId);
        booking.setPromoCode(promoCode);
        booking.setDiscountPercent(discount);
        booking.setPrice(finalPrice);
        booking.setCreatedAt(Instant.now());

        return bookingRepository.save(booking);
    }

    private void validateUser(String userId) {
        if (!externalServiceClient.isUserActive(userId)) {
            log.warn("User {} is inactive", userId);
            throw new IllegalArgumentException("User is inactive");
        }
        if (externalServiceClient.isUserBlacklisted(userId)) {
            log.warn("User {} is blacklisted", userId);
            throw new IllegalArgumentException("User is blacklisted");
        }
    }

    private void validateHotel(String hotelId) {
        if (!externalServiceClient.isHotelOperational(hotelId)) {
            log.warn("Hotel {} is not operational", hotelId);
            throw new IllegalArgumentException("Hotel is not operational");
        }
        if (!externalServiceClient.isTrustedHotel(hotelId)) {
            log.warn("Hotel {} is not trusted", hotelId);
            throw new IllegalArgumentException("Hotel is not trusted based on reviews");
        }
        if (externalServiceClient.isHotelFullyBooked(hotelId)) {
            log.warn("Hotel {} is fully booked", hotelId);
            throw new IllegalArgumentException("Hotel is fully booked");
        }
    }

    private double resolveBasePrice(String userId) {
        String status = externalServiceClient.getUserStatus(userId);
        if (status != null) {
            boolean isVip = status.equalsIgnoreCase("VIP");
            log.debug("User {} has status '{}', base price is {}", userId, status, isVip ? 80.0 : 100.0);
            return isVip ? 80.0 : 100.0;
        } else {
            log.debug("User {} has unknown status, default base price 100.0", userId);
            return 100.0;
        }
    }

    private double resolvePromoDiscount(String promoCode, String userId) {
        if (promoCode == null || promoCode.isEmpty()) {
            return 0.0;
        }

        Double discount = externalServiceClient.validatePromoCode(promoCode, userId);
        if (discount == null) {
            log.info("Promo code '{}' is invalid or not applicable for user {}", promoCode, userId);
            return 0.0;
        }

        log.debug("Promo code '{}' applied with discount {}", promoCode, discount);
        return discount;
    }
}