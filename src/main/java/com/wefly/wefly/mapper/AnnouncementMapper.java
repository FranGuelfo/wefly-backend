package com.wefly.wefly.mapper;

import com.wefly.wefly.model.Announcement;
import com.wefly.wefly.model.dto.AnnouncementDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AnnouncementMapper {

    @Mapping(source = "author.name", target = "authorName")
    AnnouncementDTO toDTO(Announcement entity);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "author", ignore = true)
    Announcement toEntity(AnnouncementDTO dto);
}