package com.wefly.wefly.mapper;

import com.wefly.wefly.model.Announcement;
import com.wefly.wefly.model.dto.AnnouncementDTO;
import org.springframework.stereotype.Component;

@Component
public class AnnouncementMapper {

    public AnnouncementDTO toDTO(Announcement entity) {
        if (entity == null) return null;
        return AnnouncementDTO.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .seatsAvailable(entity.getSeatsAvailable())
                .category(entity.getCategory())
                .origin(entity.getOrigin())
                .destination(entity.getDestination())
                .dateStr(entity.getDateStr())
                .flightNumber(entity.getFlightNumber())
                .authorId(entity.getAuthorId())
                .authorName(entity.getAuthorName())
                .authorPhone(entity.getAuthorPhone())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public Announcement toEntity(AnnouncementDTO dto) {
        if (dto == null) return null;
        Announcement entity = new Announcement();

        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        entity.setSeatsAvailable(dto.getSeatsAvailable());
        entity.setCategory(dto.getCategory());
        entity.setAuthorId(dto.getAuthorId());
        entity.setAuthorName(dto.getAuthorName());
        entity.setAuthorPhone(dto.getAuthorPhone());
        entity.setOrigin(dto.getOrigin());
        entity.setDestination(dto.getDestination());
        entity.setDateStr(dto.getDateStr());
        entity.setFlightNumber(dto.getFlightNumber());
        entity.setCreatedAt(dto.getCreatedAt());

        return entity;
    }
}