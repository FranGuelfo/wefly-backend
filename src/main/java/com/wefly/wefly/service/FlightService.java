package com.wefly.wefly.service;

import com.wefly.wefly.model.Booking;

public interface FlightService {

    Booking joinFlight(Long userId, String flightNumber, String reservationCode);
}
