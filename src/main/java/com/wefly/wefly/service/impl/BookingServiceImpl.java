package com.wefly.wefly.service.impl;

import com.wefly.wefly.model.Announcement;
import com.wefly.wefly.model.Booking;
import com.wefly.wefly.model.Flight;
import com.wefly.wefly.model.User;
import com.wefly.wefly.model.dto.BookingResponseDTO;
import com.wefly.wefly.model.dto.JoinFlightRequestDTO;
import com.wefly.wefly.model.dto.MessageResponseDTO;
import com.wefly.wefly.repository.AnnouncementRepository;
import com.wefly.wefly.repository.BookingRepository;
import com.wefly.wefly.repository.FlightRepository;
import com.wefly.wefly.repository.UserRepository;
import com.wefly.wefly.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final FlightRepository flightRepository;
    private final AnnouncementRepository announcementRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponseDTO> getPendingBookings(Long flightId) {
        return mapToDTOList(bookingRepository.findByFlightIdAndIsConfirmedFalse(flightId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponseDTO> getConfirmedBookings(Long flightId) {
        return mapToDTOList(bookingRepository.findByFlightIdAndIsConfirmedTrue(flightId));
    }

    @Override
    @Transactional
    public void approveBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reserva no encontrada"));

        if (Boolean.TRUE.equals(booking.getIsConfirmed())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La reserva ya ha sido aprobada previamente");
        }

        Flight flight = booking.getFlight();
        int plazasVuelo = (flight.getPlazas() != null) ? flight.getPlazas() : 0;

        if (plazasVuelo <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No quedan plazas disponibles en este vuelo");
        }

        flight.setPlazas(plazasVuelo - 1);
        flightRepository.save(flight);

        Announcement ann = announcementRepository.findByFlightNumber(flight.getFlightNumber());
        if (ann != null && ann.getSeatsAvailable() > 0) {
            ann.setSeatsAvailable(ann.getSeatsAvailable() - 1);
            announcementRepository.save(ann);
        }

        booking.setIsConfirmed(true);
        bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public void rejectBooking(Long bookingId) {
        if (!bookingRepository.existsById(bookingId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Reserva no encontrada");
        }
        bookingRepository.deleteById(bookingId);
    }

    @Override
    @Transactional
    public MessageResponseDTO joinFlight(JoinFlightRequestDTO request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        Flight flight = flightRepository.findById(request.flightId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vuelo no encontrado"));

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setFlight(flight);
        booking.setReservationCode(request.reservationCode());
        booking.setIsConfirmed(false);

        bookingRepository.save(booking);

        return new MessageResponseDTO(true, "Solicitud de reserva enviada correctamente");
    }

    private List<BookingResponseDTO> mapToDTOList(List<Booking> bookings) {
        return bookings.stream()
                .map(b -> new BookingResponseDTO(
                        b.getId(),
                        b.getUser().getId(),
                        b.getUser().getName(),
                        b.getUser().getEmail(),
                        b.getReservationCode(),
                        b.getUser().getIsVerified()
                ))
                .toList();
    }
}