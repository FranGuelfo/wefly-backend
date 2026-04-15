package com.wefly.wefly.service;

import com.wefly.wefly.model.dto.AnnouncementDTO;

import java.util.List;

public interface AnnouncementService {

    List<AnnouncementDTO> getAnnouncementsByFlight(String flightNumber, Long userId);

    AnnouncementDTO createAnnouncement(AnnouncementDTO dto, Long userId);

    void delete(Long id, Long userId);

    AnnouncementDTO updateAnnouncement(Long id, AnnouncementDTO dto, Long userId);
}
