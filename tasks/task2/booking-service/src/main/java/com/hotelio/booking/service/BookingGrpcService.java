package com.hotelio.booking.service;

import com.hotelio.booking.entity.BookingEntity;
import com.hotelio.booking.repository.BookingRepository;
import com.hotelio.proto.booking.BookingProto;
import com.hotelio.proto.booking.BookingServiceGrpc;
import com.hotelio.proto.booking.BookingRequest;
import com.hotelio.proto.booking.BookingResponse;
import com.hotelio.proto.booking.BookingListRequest;
import com.hotelio.proto.booking.BookingListResponse;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.List;

@GrpcService
public class BookingGrpcService extends BookingServiceGrpc.BookingServiceImplBase {

    private static final Logger log = LoggerFactory.getLogger(BookingGrpcService.class);

    private final BookingRepository bookingRepository;
    private final BookingBusinessService bookingBusinessService;

    @Autowired
    public BookingGrpcService(BookingRepository bookingRepository, BookingBusinessService bookingBusinessService) {
        this.bookingRepository = bookingRepository;
        this.bookingBusinessService = bookingBusinessService;
    }

    @Override
    public void createBooking(BookingRequest request,
                            StreamObserver<BookingResponse> responseObserver) {
        try {
            log.info("Creating booking via gRPC: userId={}, hotelId={}, promoCode={}",
                    request.getUserId(), request.getHotelId(), request.getPromoCode());

            // Create booking using business service
            BookingEntity booking = bookingBusinessService.createBooking(
                    request.getUserId(),
                    request.getHotelId(),
                    request.getPromoCode()
            );

            // Convert to response
            BookingResponse response = BookingResponse.newBuilder()
                    .setId(String.valueOf(booking.getId()))
                    .setUserId(booking.getUserId())
                    .setHotelId(booking.getHotelId())
                    .setPromoCode(booking.getPromoCode() != null ? booking.getPromoCode() : "")
                    .setDiscountPercent(booking.getDiscountPercent() != null ? booking.getDiscountPercent() : 0.0)
                    .setPrice(booking.getPrice())
                    .setCreatedAt(booking.getCreatedAt() != null ?
                            booking.getCreatedAt().toString() : Instant.now().toString())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("Error creating booking via gRPC", e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Failed to create booking: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void listBookings(BookingListRequest request,
                           StreamObserver<BookingListResponse> responseObserver) {
        try {
            log.info("Listing bookings via gRPC: userId={}", request.getUserId());

            List<BookingEntity> bookings;
            if (request.getUserId().isEmpty()) {
                bookings = bookingRepository.findAll();
            } else {
                bookings = bookingRepository.findByUserId(request.getUserId());
            }

            BookingListResponse.Builder responseBuilder =
                    BookingListResponse.newBuilder();

            for (BookingEntity booking : bookings) {
                BookingResponse bookingResponse = BookingResponse.newBuilder()
                        .setId(String.valueOf(booking.getId()))
                        .setUserId(booking.getUserId())
                        .setHotelId(booking.getHotelId())
                        .setPromoCode(booking.getPromoCode() != null ? booking.getPromoCode() : "")
                        .setDiscountPercent(booking.getDiscountPercent() != null ? booking.getDiscountPercent() : 0.0)
                        .setPrice(booking.getPrice())
                        .setCreatedAt(booking.getCreatedAt() != null ?
                                booking.getCreatedAt().toString() : Instant.now().toString())
                        .build();
                responseBuilder.addBookings(bookingResponse);
            }

            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("Error listing bookings via gRPC", e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Failed to list bookings: " + e.getMessage())
                    .asRuntimeException());
        }
    }
}