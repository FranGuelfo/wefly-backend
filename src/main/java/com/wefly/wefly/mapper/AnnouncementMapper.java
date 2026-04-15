package com.wefly.wefly.mapper;

import com.wefly.wefly.model.Announcement;
import com.wefly.wefly.model.AnnouncementType;
import com.wefly.wefly.model.dto.AnnouncementDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Component
public class AnnouncementMapper {

    public AnnouncementDTO toDTO(Announcement entity) {
        if (entity == null) return null;

        return AnnouncementDTO.builder()
                .id(entity.getId())
                .flightNumber(entity.getFlight().getFlightNumber()) // Extraemos del objeto Flight
                .title(entity.getTitle())
                .description(entity.getDescription())
                .contactInfo(entity.getContactInfo())
                .category(entity.getCategory().name())
                .type(entity.getType().name())
                .seatsAvailable(entity.getSeatsAvailable())
                .authorId(entity.getAuthor().getId())
                .authorName(entity.getAuthor().getName()) // Extraemos del objeto User
                .build();
    }

    public Announcement toEntity(AnnouncementDTO dto) {
        if (dto == null) return null;

        Announcement entity = new Announcement();
        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        entity.setContactInfo(dto.getContactInfo());
        entity.setSeatsAvailable(dto.getSeatsAvailable());
        entity.setCategory(Announcement.Category.valueOf(dto.getCategory()));
        entity.setType(AnnouncementType.valueOf(dto.getType()));
        // El Flight y el Author se setean en el Service (lógica de negocio)
        return entity;
    }
}