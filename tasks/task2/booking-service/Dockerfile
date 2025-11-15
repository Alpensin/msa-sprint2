FROM openjdk:17.0.2-jdk-slim

WORKDIR /app

COPY build/libs/booking-service-1.0.0.jar app.jar

EXPOSE 8081 9090

ENTRYPOINT ["java", "-jar", "app.jar"]
