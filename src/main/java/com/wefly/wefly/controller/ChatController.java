package com.wefly.wefly.controller;

import com.wefly.wefly.model.ChatReadProgress;
import com.wefly.wefly.model.Message;
import com.wefly.wefly.repository.ChatReadProgressRepository;
import com.wefly.wefly.repository.MessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/chat")
@CrossOrigin(origins = "*")
public class ChatController {

    private static final Logger log = LoggerFactory.getLogger(ChatController.class);
    private final MessageRepository messageRepository;
    private final ChatReadProgressRepository readProgressRepository;

    public ChatController(MessageRepository messageRepository, ChatReadProgressRepository readProgressRepository) {
        this.messageRepository = messageRepository;
        this.readProgressRepository = readProgressRepository;
    }

    /**
     * Recupera el historial de mensajes de un viaje específico ordenados cronológicamente.
     * Endpoint: GET /api/chat/history/{announcementId}
     */
    @GetMapping("/history/{announcementId}")
    public ResponseEntity<List<Message>> getChatHistory(@PathVariable Long announcementId) {
        log.info("Petición HTTP recibida para cargar historial de chat del viaje ID: {}", announcementId);

        List<Message> history = messageRepository.findByAnnouncement_IdOrderByTimestampAsc(announcementId);

        log.info("Historial cargado con éxito. Total mensajes recuperados: {}", history.size());
        return ResponseEntity.ok(history);
    }

    @PostMapping("/read/{announcementId}")
    public ResponseEntity<?> markAsRead(@PathVariable Long announcementId, @RequestParam Long userId) {
        Optional<ChatReadProgress> progressOpt = readProgressRepository.findByUserIdAndAnnouncementId(userId, announcementId);

        ChatReadProgress progress;
        if (progressOpt.isPresent()) {
            progress = progressOpt.get();
            progress.setLastReadAt(LocalDateTime.now());
        } else {
            progress = new ChatReadProgress(userId, announcementId, LocalDateTime.now());
        }

        readProgressRepository.save(progress);
        return ResponseEntity.ok().body(Map.of("message", "Chat marcado como leído"));
    }

    @GetMapping("/unread")
    public ResponseEntity<?> getUnreadCount(@RequestParam Long announcementId, @RequestParam Long userId) {
        Optional<ChatReadProgress> progressOpt = readProgressRepository.findByUserIdAndAnnouncementId(userId, announcementId);

        long unreadCount;
        // Si nunca ha entrado, todos los mensajes del viaje son no leídos
        unreadCount = progressOpt.map(chatReadProgress -> messageRepository.countByAnnouncement_IdAndTimestampAfter(
                announcementId,
                chatReadProgress.getLastReadAt()
        )).orElseGet(() -> messageRepository.countByAnnouncement_Id(announcementId));

        return ResponseEntity.ok().body(Map.of("unreadCount", unreadCount));
    }
}
