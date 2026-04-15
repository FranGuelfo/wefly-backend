package com.wefly.wefly.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "announcements")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Announcement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private String contactInfo;

    @Enumerated(EnumType.STRING)
    private Category category; // LEISURE, TO_AIRPORT, FROM_AIRPORT

    @Enumerated(EnumType.STRING)
    private AnnouncementType type; // TRANSPORT, LEISURE

    private Integer seatsAvailable;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flight_id")
    private Flight flight; // Relación con el vuelo

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User author; // Quién lo publica

    private LocalDateTime createdAt = LocalDateTime.now();

    public enum Category { TO_AIRPORT, FROM_AIRPORT, LEISURE }
}