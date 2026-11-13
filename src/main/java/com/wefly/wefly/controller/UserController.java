package com.wefly.wefly.controller;

import com.wefly.wefly.model.dto.UserDTO;
import com.wefly.wefly.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "5. Usuarios", description = "Gestión de perfiles de usuario")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "Obtener perfil de usuario por ID")
    @ApiResponse(responseCode = "200", description = "Perfil de usuario recuperado con éxito")
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserProfile(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }
}