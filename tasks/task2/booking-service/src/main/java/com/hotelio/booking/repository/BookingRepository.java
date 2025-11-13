package com.hotelio.booking.repository;

import com.hotelio.booking.entity.BookingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<BookingEntity, Long> {
    List<BookingEntity> findByUserId(String userId);
}