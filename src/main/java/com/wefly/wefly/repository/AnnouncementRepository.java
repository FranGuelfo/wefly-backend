package com.wefly.wefly.repository;

import com.wefly.wefly.model.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    // Esto busca el código dentro del objeto Flight que está dentro de Announcement
    List<Announcement> findByFlight_FlightNumberIgnoreCase(String flightNumber);
}
