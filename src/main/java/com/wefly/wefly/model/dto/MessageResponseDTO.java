package com.wefly.wefly.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos de respuesta de mensaje")
public record MessageResponseDTO(
        @Schema(description = "Indica si la operación fue exitosa", example = "true")
        boolean success,
        @Schema(description = "Mensaje de respuesta", example = "Operación realizada con éxito")
        String message
) {}