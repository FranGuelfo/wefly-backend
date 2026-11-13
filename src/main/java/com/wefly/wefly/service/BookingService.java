package com.wefly.wefly.service;

import com.wefly.wefly.model.dto.BookingResponseDTO;
import com.wefly.wefly.model.dto.JoinFlightRequestDTO;
import com.wefly.wefly.model.dto.MessageResponseDTO;

import java.util.List;

public interface BookingService {

    List<BookingResponseDTO> getPendingBookings(Long flightId);
    List<BookingResponseDTO> getConfirmedBookings(Long flightId);
    void approveBooking(Long bookingId);
    void rejectBooking(Long bookingId);
    MessageResponseDTO joinFlight(JoinFlightRequestDTO request);
}
