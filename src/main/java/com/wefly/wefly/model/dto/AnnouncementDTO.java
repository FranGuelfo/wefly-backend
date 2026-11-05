package com.wefly.wefly.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncementDTO {
    private Long id;
    private String title;
    private String description;
    private int seatsAvailable;
    private String category;
    private String origin;
    private String destination;
    private String dateStr;
    private String flightNumber;
    private Long authorId;
    private String authorName;
    private String authorPhone;
    private LocalDateTime createdAt;
}
