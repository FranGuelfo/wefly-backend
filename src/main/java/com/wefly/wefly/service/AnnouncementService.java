package com.wefly.wefly.service;

import com.wefly.wefly.model.dto.AnnouncementDTO;

import java.util.List;

public interface AnnouncementService {

    List<AnnouncementDTO> getAnnouncementsByFlight(String flightNumber);

    AnnouncementDTO createAnnouncement(AnnouncementDTO dto);
}
