package com.wefly.wefly.service.impl;

import com.wefly.wefly.model.Booking;
import com.wefly.wefly.model.Flight;
import com.wefly.wefly.model.User;
import com.wefly.wefly.repository.BookingRepository;
import com.wefly.wefly.repository.FlightRepository;
import com.wefly.wefly.repository.UserRepository;
import com.wefly.wefly.service.FlightService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FlightServiceImpl implements FlightService {

    private final FlightRepository flightRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    @Override
    public Booking joinFlight(Long userId, String flightNumber, String reservationCode) {

            // 1. Buscamos el vuelo. Si no existe en nuestra DB, lo creamos
            Flight flight = flightRepository.findByFlightNumber(flightNumber)
                    .orElseGet(() -> {
                        Flight newFlight = new Flight();
                        newFlight.setFlightNumber(flightNumber);
                        // Aquí podrías llamar a una API externa de vuelos para traer origen/destino
                        return flightRepository.save(newFlight);
                    });

            // 2. Buscamos al usuario
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // 3. Creamos la reserva (Booking) en estado "no confirmado"
            Booking booking = new Booking();
            booking.setUser(user);
            booking.setFlight(flight);
            booking.setReservationCode(reservationCode);
            booking.setConfirmed(false); // Esperando validación de ticket/OCR

            return bookingRepository.save(booking);
        }
}
