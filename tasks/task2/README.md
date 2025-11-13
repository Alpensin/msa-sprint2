## 🛠️ Подготовка окружения

1. Выполните в папке первого задания
```bash
docker compose down
```
Это связано с тем, что необходима пересборка контейнера для задания 2.

2. Создайте Docker сеть (если еще не создана):
```bash
docker network create hotelio-net
```

3. Поднимите приложение в папке второго задания:
```bash
docker compose up -d --build
```
---

## 🚀 Проверка корректности

В логах приложения должно быть:
```
➡️  BookingService beans:
    - bookingGrpcClientService: class com.hotelio.monolith.service.BookingGrpcClientService
    - bookingService: class com.hotelio.monolith.service.BookingService (legacy)
```

При запуске тестов, тесты на booking должны упасть.

---

## 🏗️ Архитектура задания 2

### Сервисы
- **Monolith**: порт 8080, использует gRPC клиент для связи с booking-service
- **Booking Service**: порт 8081 (HTTP), порт 9090 (gRPC)
- **Базы данных**:
  - Monolith DB: localhost:5432
  - Booking DB: localhost:5433

### API Endpoints
- `GET /api/bookings?userId={userId}` - получение бронирований
- `POST /api/bookings?userId={userId}&hotelId={hotelId}&promoCode={promoCode}` - создание бронирования

### Тестирование
```bash
# Создание бронирования
curl -X POST "http://localhost:8080/api/bookings?userId=user123&hotelId=hotel456&promoCode=VIP10"

# Получение бронирований
curl "http://localhost:8080/api/bookings?userId=user123"
```


---
