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
@Schema(description = "Datos de solicitud de registro")
public class RegisterRequest {

    @Schema(description = "Nombre del usuario", example = "Juan Pérez")
    private String name;
    @Schema(description = "Correo electrónico del usuario", example = "juan.perez@example.com")
    private String email;
    @Schema(description = "Contraseña del usuario", example = "password123")
    private String password;
}
