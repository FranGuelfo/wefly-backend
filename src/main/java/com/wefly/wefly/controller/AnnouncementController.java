package com.wefly.wefly.controller;

import com.wefly.wefly.model.dto.AnnouncementDTO;
import com.wefly.wefly.service.AnnouncementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "4. Anuncios", description = "Gestión de anuncios de vuelos y tablón de anuncios")
@RestController
@RequestMapping("/api/v1/announcements")
@RequiredArgsConstructor
@Slf4j
public class AnnouncementController {

    private final AnnouncementService announcementService;

    // 1. Endpoint para publicar un nuevo anuncio usando DTO
    @Operation(summary = "Publicar un nuevo anuncio")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Anuncio creado con éxito"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content)
    })
    @PostMapping("/create")
    public ResponseEntity<AnnouncementDTO> createAnnouncement(@Valid @RequestBody AnnouncementDTO dto) {
        log.info("Recibiendo petición DTO para crear anuncio en vuelo: {}", dto.getFlightNumber());
        AnnouncementDTO savedDto = announcementService.createAnnouncement(dto);
        log.info("Anuncio creado y procesado con éxito en Service. ID asignado: {}", savedDto.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(savedDto);
    }

    // 2. Endpoint para obtener los anuncios de un vuelo específico mapeados a DTO
    @Operation(summary = "Obtener anuncios por número de vuelo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Anuncios encontrados"),
            @ApiResponse(responseCode = "404", description = "Vuelo no encontrado", content = @Content)
    })
    @GetMapping("/flight/{flightNumber}")
    public ResponseEntity<List<AnnouncementDTO>> getAnnouncementsByFlight(@PathVariable String flightNumber) {
        String cleanFlight = flightNumber.toUpperCase().trim();
        log.info("Buscando anuncios reales mapeados a DTO para el vuelo: {}", cleanFlight);

        List<AnnouncementDTO> announcements = announcementService.getAnnouncementsByFlight(cleanFlight);
        return ResponseEntity.ok(announcements);
    }

    // 3. Eliminar anuncio
    @Operation(summary = "Eliminar un anuncio")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Anuncio eliminado con éxito"),
            @ApiResponse(responseCode = "404", description = "Anuncio no encontrado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAnnouncement(@PathVariable Long id, @RequestParam Long userId) {
        log.info("Petición para eliminar anuncio ID: {} por usuario: {}", id, userId);
        announcementService.delete(id, userId);
        return ResponseEntity.noContent().build();
    }

    // 4. Modificar parcialmente anuncio
    @Operation(summary = "Actualizar un anuncio")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Anuncio actualizado con éxito"),
            @ApiResponse(responseCode = "404", description = "Anuncio no encontrado", content = @Content)
    })
    @PatchMapping("/{id}")
    public ResponseEntity<AnnouncementDTO> updateAnnouncement(
            @PathVariable Long id,
            @RequestBody AnnouncementDTO dto,
            @RequestParam Long userId) {
        log.info("Petición para actualizar anuncio ID: {} por usuario: {}", id, userId);
        return ResponseEntity.ok(announcementService.updateAnnouncement(id, dto, userId));
    }
}