package com.hotelio.monolith.service;

import com.hotelio.monolith.entity.Booking;
import com.hotelio.proto.booking.BookingProto;
import com.hotelio.proto.booking.BookingServiceGrpc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingGrpcClientService {

    private static final Logger log = LoggerFactory.getLogger(BookingGrpcClientService.class);

    private final BookingServiceGrpc.BookingServiceBlockingStub bookingServiceStub;

    @Autowired
    public BookingGrpcClientService(BookingServiceGrpc.BookingServiceBlockingStub bookingServiceStub) {
        this.bookingServiceStub = bookingServiceStub;
    }

    public List<Booking> listAll(String userId) {
        try {
            log.info("Fetching bookings via gRPC: userId={}", userId);

            BookingProto.BookingListRequest request = BookingProto.BookingListRequest.newBuilder()
                    .setUserId(userId != null ? userId : "")
                    .build();

            BookingProto.BookingListResponse response = bookingServiceStub.listBookings(request);

            return response.getBookingsList().stream()
                    .map(this::convertToBooking)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Error fetching bookings via gRPC", e);
            throw new RuntimeException("Failed to fetch bookings: " + e.getMessage(), e);
        }
    }

    public Booking createBooking(String userId, String hotelId, String promoCode) {
        try {
            log.info("Creating booking via gRPC: userId={}, hotelId={}, promoCode={}", userId, hotelId, promoCode);

            BookingProto.BookingRequest request = BookingProto.BookingRequest.newBuilder()
                    .setUserId(userId)
                    .setHotelId(hotelId)
                    .setPromoCode(promoCode != null ? promoCode : "")
                    .build();

            BookingProto.BookingResponse response = bookingServiceStub.createBooking(request);

            return convertToBooking(response);

        } catch (Exception e) {
            log.error("Error creating booking via gRPC", e);
            throw new RuntimeException("Failed to create booking: " + e.getMessage(), e);
        }
    }

    private Booking convertToBooking(BookingProto.BookingResponse protoBooking) {
        Booking booking = new Booking();
        booking.setId(Long.valueOf(protoBooking.getId()));
        booking.setUserId(protoBooking.getUserId());
        booking.setHotelId(protoBooking.getHotelId());
        booking.setPromoCode(protoBooking.getPromoCode());
        booking.setDiscountPercent(protoBooking.getDiscountPercent());
        booking.setPrice(protoBooking.getPrice());

        try {
            if (!protoBooking.getCreatedAt().isEmpty()) {
                booking.setCreatedAt(Instant.parse(protoBooking.getCreatedAt()));
            }
        } catch (DateTimeParseException e) {
            log.warn("Failed to parse created_at timestamp: {}", protoBooking.getCreatedAt());
            booking.setCreatedAt(Instant.now());
        }

        return booking;
    }
}