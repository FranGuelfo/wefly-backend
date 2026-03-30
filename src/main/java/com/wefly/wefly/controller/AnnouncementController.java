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
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementService service;

    @GetMapping("/flight/{number}")
    public ResponseEntity<List<AnnouncementDTO>> getByFlight(@PathVariable String number) {
        return ResponseEntity.ok(service.getAnnouncementsByFlight(number));
    }

    @PostMapping
    public ResponseEntity<AnnouncementDTO> create(@RequestBody AnnouncementDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createAnnouncement(dto));
    }
}
