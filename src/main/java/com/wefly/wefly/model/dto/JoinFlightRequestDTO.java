package com.wefly.wefly.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Datos para solicitar plaza en un vuelo")
public record JoinFlightRequestDTO(
        @Schema(description = "ID único del vuelo en base de datos", example = "1")
        @NotNull Long flightId,

        @Schema(description = "ID del pasajero que solicita la reserva", example = "2")
        @NotNull Long userId,

        @Schema(description = "Código localizador de 6 caracteres de la aerolínea", example = "ABCDEF")
        String reservationCode
) {}
