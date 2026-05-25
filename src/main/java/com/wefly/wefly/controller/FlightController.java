package com.wefly.wefly.controller;

import com.wefly.wefly.model.Flight;
import com.wefly.wefly.repository.BookingRepository;
import com.wefly.wefly.service.FlightService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/flights")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class FlightController {

    private final FlightService flightService;
    private final BookingRepository bookingRepository;

    @GetMapping
    public ResponseEntity<java.util.List<Flight>> getAllFlights() {
        // Retornamos todos los vuelos que se hayan creado en la base de datos
        return ResponseEntity.ok(flightService.findAllFlights());
    }

    // POST /api/v1/flights/join?userId=2&flightNumber=IB3110&reservationCode=XY1234
    @PostMapping("/join")
    public ResponseEntity<String> joinFlight(
            @RequestParam Long userId,
            @RequestParam String flightNumber,
            @RequestParam String reservationCode) {

        flightService.joinFlight(userId, flightNumber, reservationCode);
        return ResponseEntity.ok("Solicitud de unión al vuelo enviada con éxito. Pendiente de verificación.");
    }

    @PutMapping("/verify/{bookingId}")
    public ResponseEntity<String> verifyBooking(@PathVariable Long bookingId) {
        return bookingRepository.findById(bookingId)
                .map(booking -> {
                    booking.setIsConfirmed(true);
                    bookingRepository.save(booking);
                    return ResponseEntity.ok("Reserva confirmada");
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
