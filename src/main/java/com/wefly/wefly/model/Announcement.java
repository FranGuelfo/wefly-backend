package com.wefly.wefly.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "anuncios")
public class Announcement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Cambios para coincidir con Flutter
    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 1000)
    private String description;

    @Column(nullable = false)
    private int seatsAvailable;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String origin;

    @Column(nullable = false)
    private String destination;

    @Column(nullable = false)
    private String dateStr;

    @Column(nullable = false)
    private String flightNumber;

    @Column(nullable = false)
    private Long authorId;

    private String authorName;

    private String authorPhone;

    private LocalDateTime createdAt = LocalDateTime.now();

    public Announcement(String title, String description, int seatsAvailable, String category,
                        String origin, String destination, String dateStr, String flightNumber,
                        Long authorId, String authorName, String authorPhone) {
        this.title = title;
        this.description = description;
        this.seatsAvailable = seatsAvailable;
        this.category = category;
        this.origin = origin;
        this.destination = destination;
        this.dateStr = dateStr;
        this.flightNumber = flightNumber;
        this.authorId = authorId;
        this.authorName = authorName;
        this.authorPhone = authorPhone;
    }
}