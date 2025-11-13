package com.hotelio.booking.service;

import com.hotelio.booking.entity.BookingEntity;
import com.hotelio.booking.repository.BookingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingBusinessServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ExternalServiceClient externalServiceClient;

    @InjectMocks
    private BookingBusinessService bookingBusinessService;

    @Test
    void createBooking_Success() {
        // Given
        String userId = "user123";
        String hotelId = "hotel456";
        String promoCode = "VIP10";

        when(externalServiceClient.isUserActive(userId)).thenReturn(true);
        when(externalServiceClient.isUserBlacklisted(userId)).thenReturn(false);
        when(externalServiceClient.isHotelOperational(hotelId)).thenReturn(true);
        when(externalServiceClient.isTrustedHotel(hotelId)).thenReturn(true);
        when(externalServiceClient.isHotelFullyBooked(hotelId)).thenReturn(false);
        when(externalServiceClient.getUserStatus(userId)).thenReturn("VIP");
        when(externalServiceClient.validatePromoCode(promoCode, userId)).thenReturn(10.0);

        BookingEntity savedBooking = new BookingEntity();
        savedBooking.setId(1L);
        savedBooking.setUserId(userId);
        savedBooking.setHotelId(hotelId);
        savedBooking.setPromoCode(promoCode);
        savedBooking.setDiscountPercent(10.0);
        savedBooking.setPrice(70.0);

        when(bookingRepository.save(any(BookingEntity.class))).thenReturn(savedBooking);

        // When
        BookingEntity result = bookingBusinessService.createBooking(userId, hotelId, promoCode);

        // Then
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(hotelId, result.getHotelId());
        assertEquals(promoCode, result.getPromoCode());
        assertEquals(10.0, result.getDiscountPercent());
        assertEquals(70.0, result.getPrice());
    }
}