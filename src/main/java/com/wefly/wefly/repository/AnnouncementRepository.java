package com.wefly.wefly.repository;

import com.wefly.wefly.model.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

    List<Announcement> findByFlightNumberOrderByCreatedAtDesc(String flightNumber);

    Announcement findByFlightNumber(String flightNumber);
}
