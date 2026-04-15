package com.wefly.wefly.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnnouncementDTO {
    private Long id;
    private String flightNumber; // Lo mantenemos como Number
    private String title;
    private String description;
    private String contactInfo;
    private String category;     // TO_AIRPORT, etc.
    private String type;         // TRANSPORT, etc.
    private Integer seatsAvailable;

    // Datos del autor para el Frontend
    private Long authorId;
    private String authorName;
}
