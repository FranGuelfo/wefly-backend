package com.wefly.wefly.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Datos del usuario")
public class UserDTO {

    @Schema(description = "ID único del usuario en base de datos", example = "1")
    private Long id;
    @Schema(description = "Nombre del usuario", example = "Juan Pérez")
    private String name;
    @Schema(description = "Correo electrónico del usuario", example = "juan.perez@example.com")
    private String email;
    @Schema(description = "Biografía del usuario", example = "Ingeniero de software con 5 años de experiencia")
    private String bio;
    @Schema(description = "URL de la imagen de perfil del usuario", example = "https://example.com/profile.jpg")
    private String profilePictureUrl;
    @Schema(description = "Indica si el usuario está verificado", example = "true")
    private Boolean isVerified;
}
