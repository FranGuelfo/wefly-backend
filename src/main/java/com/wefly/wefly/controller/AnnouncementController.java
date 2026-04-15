package com.wefly.wefly.controller;

import com.wefly.wefly.model.Announcement;
import com.wefly.wefly.model.dto.AnnouncementDTO;
import com.wefly.wefly.repository.AnnouncementRepository;
import com.wefly.wefly.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/announcements")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementService announcementService;

    @GetMapping("/flight/{flightCode}")
    public ResponseEntity<List<AnnouncementDTO>> getByFlight(
            @PathVariable String flightCode,
            @RequestParam Long userId) { // Pedimos el ID del usuario que consulta

        List<AnnouncementDTO> announcements = announcementService.getAnnouncementsByFlight(flightCode, userId);
        return ResponseEntity.ok(announcements);
    }

    @PostMapping
    public ResponseEntity<AnnouncementDTO> createAnnouncement(
            @RequestBody AnnouncementDTO dto,
            @RequestParam Long userId) {
        return ResponseEntity.ok(announcementService.createAnnouncement(dto, userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAnnouncement(@PathVariable Long id, @RequestParam Long userId) {
        announcementService.delete(id, userId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AnnouncementDTO> updateAnnouncement(
            @PathVariable Long id,
            @RequestBody AnnouncementDTO dto,
            @RequestParam Long userId) {
        return ResponseEntity.ok(announcementService.updateAnnouncement(id, dto, userId));
    }
}
