package com.wefly.wefly.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "vuelos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Flight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String flightNumber; // Ej: IB3240

    private String origin;       // Ej: MAD
    private String destination;  // Ej: ALC
    private LocalDateTime departureTime;
    private Integer plazas;

    // Constructor personalizado sin ID para que el servicio quede limpio
    public Flight(String flightNumber, String origin, String destination, LocalDateTime departureTime, Integer plazas) {
        this.flightNumber = flightNumber;
        this.origin = origin;
        this.destination = destination;
        this.departureTime = departureTime;
        this.plazas = plazas;
    }
}
