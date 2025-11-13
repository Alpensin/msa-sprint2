package com.hotelio.monolith.config;

import com.hotelio.proto.booking.BookingServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcClientConfig {

    @Value("${booking.service.host:localhost}")
    private String bookingServiceHost;

    @Value("${booking.service.port:9090}")
    private int bookingServicePort;

    @Bean
    public ManagedChannel bookingServiceChannel() {
        return ManagedChannelBuilder
                .forAddress(bookingServiceHost, bookingServicePort)
                .usePlaintext()
                .build();
    }

    @Bean
    public BookingServiceGrpc.BookingServiceBlockingStub bookingServiceStub(ManagedChannel channel) {
        return BookingServiceGrpc.newBlockingStub(channel);
    }
}