package com.wefly.wefly.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "bookings")
@Data
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Flight flight;

    private String reservationCode; // El código de 6 dígitos de la aerolínea

    private boolean isConfirmed = false; // Esto se pondrá a true tras validar el PDF/OCR
}
