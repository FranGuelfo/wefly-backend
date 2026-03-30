package com.wefly.wefly.service.impl;

import com.wefly.wefly.mapper.AnnouncementMapper;
import com.wefly.wefly.model.Announcement;
import com.wefly.wefly.model.dto.AnnouncementDTO;
import com.wefly.wefly.repository.AnnouncementRepository;
import com.wefly.wefly.service.AnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnnouncementServiceImpl implements AnnouncementService {

    @Autowired
    private AnnouncementRepository repository;
    @Autowired
    private AnnouncementMapper mapper;

    public List<AnnouncementDTO> getAnnouncementsByFlight(String flightNumber) {
        return repository.findByFlightNumberIgnoreCase(flightNumber)
                .stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    public AnnouncementDTO createAnnouncement(AnnouncementDTO dto) {
        Announcement entity = mapper.toEntity(dto);
        Announcement saved = repository.save(entity);
        return mapper.toDTO(saved);
    }
}
