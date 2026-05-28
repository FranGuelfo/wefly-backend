package com.wefly.wefly.service;

import com.wefly.wefly.model.Flight;

import java.util.Optional;

public interface FlightService {

    Optional<Flight> getOrFetchFlight(String flightNumber);
}
