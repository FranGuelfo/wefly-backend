package com.wefly.wefly.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncementDTO {
    private Long id;
    private String flightNumber;
    private String title;
    private String description;
    private String category;
    private Integer seatsAvailable;
    private String contactInfo;
    private String authorName;
    private String createdAt;
}
