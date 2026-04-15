package com.wefly.wefly.repository;

import com.wefly.wefly.model.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {

    // Este es el método que le falta a tu Service
    Optional<Flight> findByFlightNumber(String flightNumber);

    // También es útil tener este para las búsquedas ignorando mayúsculas/minúsculas
    Optional<Flight> findByFlightNumberIgnoreCase(String flightNumber);
}
