package com.wefly.wefly.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos de respuesta de reserva")
public record BookingResponseDTO(
        @Schema(description = "ID único de la reserva en base de datos", example = "1")
        Long id,
        @Schema(description = "ID único del usuario en base de datos", example = "1")
        Long userId,
        @Schema(description = "Nombre del usuario", example = "Juan Pérez")
        String userName,
        @Schema(description = "Correo electrónico del usuario", example = "juan.perez@example.com")
        String userEmail,
        @Schema(description = "Código de reserva", example = "RES-001")
        String reservationCode,
        @Schema(description = "Indica si la reserva está verificada", example = "true")
        Boolean isVerified
) {}
