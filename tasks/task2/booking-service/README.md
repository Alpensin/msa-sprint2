# Booking Service Microservice

This is the extracted Booking Service microservice from the hotelio-monolith.

## Architecture

- **gRPC Interface**: Communication via `booking.proto`
- **Ports**:
  - HTTP: 8081
  - gRPC: 9090
- **Database**: PostgreSQL

## Services

### BookingGrpcService
- Implements gRPC service interface
- Handles CreateBooking and ListBookings RPC calls

### BookingBusinessService
- Contains core booking business logic
- Validates users, hotels, and promo codes
- Calculates pricing and discounts

### ExternalServiceClient
- Stub implementation for external service calls
- In production, would call other microservices via gRPC/REST

## Build

```bash
./gradlew build
```

## Run

```bash
./gradlew bootRun
```

## Docker

```bash
docker build -t booking-service .
docker run -p 8081:8081 -p 9090:9090 booking-service
```