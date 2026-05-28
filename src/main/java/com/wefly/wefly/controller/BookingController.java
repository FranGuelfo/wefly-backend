package com.wefly.wefly.controller;

import com.wefly.wefly.model.Booking;
import com.wefly.wefly.model.Flight;
import com.wefly.wefly.repository.BookingRepository;
import com.wefly.wefly.repository.FlightRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/bookings")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class BookingController {

    private final BookingRepository bookingRepository;
    private final FlightRepository flightRepository;

    @GetMapping("/pending/{flightId}")
    public ResponseEntity<?> getPendingBookings(@PathVariable Long flightId) {
        List<Booking> pendingBookings = bookingRepository.findByFlightIdAndIsConfirmedFalse(flightId);

        List<Map<String, Object>> response = pendingBookings.stream().map(b -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", b.getId());
            map.put("userId", b.getUser().getId());
            map.put("userName", b.getUser().getName());
            map.put("userEmail", b.getUser().getEmail());
            map.put("reservationCode", b.getReservationCode());
            map.put("isVerified", b.getUser().getIsVerified());
            return map;
        }).toList();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/approve/{bookingId}")
    public ResponseEntity<?> approveBooking(@PathVariable Long bookingId) {
        return bookingRepository.findById(bookingId).map(booking -> {
            booking.setIsConfirmed(true);
            bookingRepository.save(booking);

            Flight flight = booking.getFlight();

            int plazasActuales = (flight.getPlazas() != null) ? flight.getPlazas() : 0;

            if (plazasActuales > 0) {
                flight.setPlazas(plazasActuales - 1);
                flightRepository.save(flight);
            } else {
                return ResponseEntity.badRequest().body("No quedan plazas disponibles");
            }

            return ResponseEntity.ok("Reserva aprobada");
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/reject/{bookingId}")
    public ResponseEntity<?> rejectBooking(@PathVariable Long bookingId) {
        if (bookingRepository.existsById(bookingId)) {
            bookingRepository.deleteById(bookingId);
            return ResponseEntity.ok("Reserva rechazada y eliminada");
        }
        return ResponseEntity.notFound().build();
    }
}
