package com.wefly.wefly.controller;

import com.wefly.wefly.model.dto.AnnouncementDTO;
import com.wefly.wefly.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/announcements")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
@Slf4j
public class AnnouncementController {

    private final AnnouncementService announcementService;

    // 1. Endpoint para publicar un nuevo anuncio usando DTO
    @PostMapping("/create")
    public ResponseEntity<?> createAnnouncement(@RequestBody AnnouncementDTO dto) {
        log.info("Recibiendo petición DTO para crear anuncio en vuelo: {}", dto.getFlightNumber());
        try {
            AnnouncementDTO savedDto = announcementService.createAnnouncement(dto);
            log.info("Anuncio creado y procesado con éxito en Service. ID asignado: {}", savedDto.getId());
            return ResponseEntity.ok(Map.of("success", true, "announcement", savedDto));
        } catch (Exception e) {
            log.error("Error en el flujo de creación del anuncio: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    // 2. Endpoint para obtener los anuncios de un vuelo específico mapeados a DTO
    @GetMapping("/flight/{flightNumber}")
    public ResponseEntity<List<AnnouncementDTO>> getAnnouncementsByFlight(@PathVariable String flightNumber) {
        String cleanFlight = flightNumber.toUpperCase().trim();
        log.info("Buscando anuncios reales mapeados a DTO para el vuelo: {}", cleanFlight);

        List<AnnouncementDTO> announcements = announcementService.getAnnouncementsByFlight(cleanFlight);
        return ResponseEntity.ok(announcements);
    }

    // 3. Eliminar anuncio
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAnnouncement(@PathVariable Long id, @RequestParam Long userId) {
        log.info("Petición para eliminar anuncio ID: {} por usuario: {}", id, userId);
        announcementService.delete(id, userId);
        return ResponseEntity.noContent().build();
    }

    // 4. Modificar parcialmente anuncio
    @PatchMapping("/{id}")
    public ResponseEntity<AnnouncementDTO> updateAnnouncement(
            @PathVariable Long id,
            @RequestBody AnnouncementDTO dto,
            @RequestParam Long userId) {
        log.info("Petición para actualizar anuncio ID: {} por usuario: {}", id, userId);
        return ResponseEntity.ok(announcementService.updateAnnouncement(id, dto, userId));
    }
}