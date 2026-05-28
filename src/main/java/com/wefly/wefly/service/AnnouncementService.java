package com.wefly.wefly.service;

import com.wefly.wefly.model.dto.AnnouncementDTO;

import java.util.List;

public interface AnnouncementService {

    AnnouncementDTO createAnnouncement(AnnouncementDTO dto);
    List<AnnouncementDTO> getAnnouncementsByFlight(String flightNumber);
    AnnouncementDTO updateAnnouncement(Long id, AnnouncementDTO dto, Long userId);
    void delete(Long id, Long userId);
}
