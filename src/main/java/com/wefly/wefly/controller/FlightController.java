package com.wefly.wefly.controller;

import com.wefly.wefly.model.Booking;
import com.wefly.wefly.model.Flight;
import com.wefly.wefly.model.User;
import com.wefly.wefly.repository.BookingRepository;
import com.wefly.wefly.repository.FlightRepository;
import com.wefly.wefly.repository.UserRepository;
import com.wefly.wefly.service.FlightService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/flights")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class FlightController {

    private final FlightService flightService;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final FlightRepository flightRepository;

    @GetMapping("/{flightNumber}")
    public ResponseEntity<?> verifyFlight(@PathVariable String flightNumber) {
        Optional<Flight> flightOpt = flightService.getOrFetchFlight(flightNumber.toUpperCase().trim());

        if (flightOpt.isEmpty()) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "El número de vuelo no es válido o no opera hoy.");
            return ResponseEntity.status(404).body(errorResponse);
        }

        // Preparamos la respuesta JSON estructurada para Flutter
        Map<String, Object> response = new HashMap<>();
        response.put("flight", flightOpt.get());

        // El TODO de pasajeros lo dejamos listo para cuando asocies usuarios a los vuelos
        // response.put("passengers", userService.getPassengersByFlight(flightOpt.get().getId()));

        return ResponseEntity.ok(response);
    }

    @PutMapping("/approve/{bookingId}")
    public ResponseEntity<?> approveBooking(@PathVariable Long bookingId) {
        try {
            // 1. Buscamos la reserva
            Booking booking = bookingRepository.findById(bookingId)
                    .orElseThrow(() -> new Exception("Reserva no encontrada"));

            // 2. Comprobamos si ya estaba confirmada para no restar plazas dos veces
            if (Boolean.TRUE.equals(booking.getIsConfirmed())) {
                return ResponseEntity.badRequest().body("Esta reserva ya fue aprobada anteriormente.");
            }

            // 3. Lógica para restar la plaza en el vuelo
            Flight flight = booking.getFlight();
            if (flight.getPlazas() > 0) {
                flight.setPlazas(flight.getPlazas() - 1);
                flightRepository.save(flight);
            } else {
                return ResponseEntity.badRequest().body("No quedan plazas disponibles.");
            }

            // 4. Confirmamos la reserva
            booking.setIsConfirmed(true);
            bookingRepository.save(booking);

            return ResponseEntity.ok("Reserva aprobada y plaza restada correctamente.");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/join")
    public ResponseEntity<?> joinFlight(@RequestBody Map<String, Object> payload) {
        try {
            Long flightId = Long.parseLong(payload.get("flightId").toString());
            Long userId = Long.parseLong(payload.get("userId").toString());
            String reservationCode = (String) payload.get("reservationCode");

            // 1. Buscar las entidades reales en BD
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new Exception("Usuario no encontrado"));
            Flight flight = flightRepository.findById(flightId)
                    .orElseThrow(() -> new Exception("Vuelo no encontrado"));

            // 2. Crear y guardar la reserva (isConfirmed viene false por defecto)
            Booking booking = new Booking();
            booking.setUser(user);
            booking.setFlight(flight);
            booking.setReservationCode(reservationCode);
            booking.setIsConfirmed(false); // Pendiente de aprobación

            bookingRepository.save(booking);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Solicitud enviada. Pendiente de aprobación por el anfitrión.");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.singletonMap("error", e.getMessage()));
        }
    }
}
