package com.wefly.wefly.service;

import com.wefly.wefly.model.Booking;
import com.wefly.wefly.model.Flight;

import java.util.List;

public interface FlightService {

    List<Flight> findAllFlights();

    Booking joinFlight(Long userId, String flightNumber, String reservationCode);
}
