package com.wefly.wefly.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos de un anuncio de vuelo")
public class AnnouncementDTO {
    @Schema(description = "ID único del vuelo en base de datos", example = "1")
    private Long id;
    @Schema(description = "Título del anuncio", example = "Vuelo a París")
    private String title;
    @Schema(description = "Descripción del anuncio", example = "Vuelo directo a París")
    private String description;
    @Schema(description = "Número de plazas disponibles", example = "5")
    private int seatsAvailable;
    @Schema(description = "Categoría del anuncio", example = "Economy")
    private String category;
    @Schema(description = "Origen del vuelo", example = "Madrid")
    private String origin;
    @Schema(description = "Destino del vuelo", example = "París")
    private String destination;
    @Schema(description = "Fecha del vuelo", example = "2023-10-10")
    private String dateStr;
    @Schema(description = "Número de vuelo", example = "IB3509")
    private String flightNumber;
    @Schema(description = "ID del autor del anuncio", example = "1")
    private Long authorId;
    @Schema(description = "Nombre del autor del anuncio", example = "Juan Pérez")
    private String authorName;
    @Schema(description = "Teléfono del autor del anuncio", example = "+34 123 456 789")
    private String authorPhone;
    @Schema(description = "Fecha de creación del anuncio", example = "2023-10-10T10:00:00")
    private LocalDateTime createdAt;
}
