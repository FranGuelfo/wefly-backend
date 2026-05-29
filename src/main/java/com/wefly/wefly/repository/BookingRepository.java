package com.wefly.wefly.repository;

import com.wefly.wefly.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByFlightIdAndIsConfirmedFalse(Long flightId);

    List<Booking> findByFlightIdAndIsConfirmedTrue(Long flightId);
}
