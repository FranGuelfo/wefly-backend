package com.wefly.wefly.repository;

import com.wefly.wefly.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    // Para saber si un usuario ya está verificado en un vuelo concreto
    Optional<Booking> findByUserIdAndFlightId(Long userId, Long flightId);

    // Listar todos los vuelos donde el usuario tiene reserva
    List<Booking> findByUserId(Long userId);
}
