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
@Schema(description = "Datos de solicitud de token")
public class TokenRequest {

    @Schema(description = "ID del token", example = "1")
    private String idToken;
}
