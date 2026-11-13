package com.wefly.wefly.model.dto.security;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Datos de respuesta de autenticación")
public class AuthResponse {

    @Schema(description = "Token de autenticación", example = "1")
    private String token;
    @Schema(description = "ID del usuario", example = "1")
    private Long userId;
    @Schema(description = "Nombre del usuario", example = "Juan Pérez")
    private String name;
    @Schema(description = "Correo electrónico del usuario", example = "juan.perez@example.com")
    private String email;
}
