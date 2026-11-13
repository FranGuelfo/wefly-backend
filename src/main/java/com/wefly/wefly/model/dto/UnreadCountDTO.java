package com.wefly.wefly.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta con el número de mensajes sin leer en una conversación")
public record UnreadCountDTO(
        @Schema(description = "Cantidad total de mensajes no leídos", example = "3")
        long unreadCount
) {}