package com.wefly.wefly.controller;

import com.wefly.wefly.model.ChatReadProgress;
import com.wefly.wefly.model.Message;
import com.wefly.wefly.model.dto.MessageResponseDTO;
import com.wefly.wefly.model.dto.UnreadCountDTO;
import com.wefly.wefly.repository.ChatReadProgressRepository;
import com.wefly.wefly.repository.MessageRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Tag(name = "6. Chat", description = "Gestión del historial de mensajes, marcas de lectura y contadores de mensajes no leídos")
@RestController
@RequestMapping("/api/v1/chat")
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
     * Endpoint: GET /api/v1/chat/history/{announcementId}
     */
    @Operation(
            summary = "Obtener historial de mensajes del chat",
            description = "Recupera todos los mensajes intercambiados en la sala de chat de un viaje especifico ordenados de forma ascendente por fecha."
    )
    @ApiResponse(responseCode = "200", description = "Historial de conversación recuperado con éxito")
    @GetMapping("/history/{announcementId}")
    public ResponseEntity<List<Message>> getChatHistory(
            @Parameter(description = "ID del anuncio/grupo de viaje", example = "1")
            @PathVariable Long announcementId) {
        log.info("Petición HTTP recibida para cargar historial de chat del viaje ID: {}", announcementId);

        List<Message> history = messageRepository.findByAnnouncement_IdOrderByTimestampAsc(announcementId);

        log.info("Historial cargado con éxito. Total mensajes recuperados: {}", history.size());
        return ResponseEntity.ok(history);
    }

    @Operation(
            summary = "Marcar conversación como leída",
            description = "Actualiza o crea el progreso de lectura del usuario guardando la marca de tiempo actual."
    )
    @ApiResponse(responseCode = "200", description = "Chat marcado como leído correctamente")
    @PostMapping("/read/{announcementId}")
    public ResponseEntity<MessageResponseDTO> markAsRead(
            @Parameter(description = "ID del anuncio/grupo de viaje", example = "1")
            @PathVariable Long announcementId,
            @Parameter(description = "ID del usuario que lee el chat", example = "1")
            @RequestParam Long userId) {

        Optional<ChatReadProgress> progressOpt = readProgressRepository.findByUserIdAndAnnouncementId(userId, announcementId);

        ChatReadProgress progress;
        if (progressOpt.isPresent()) {
            progress = progressOpt.get();
            progress.setLastReadAt(LocalDateTime.now());
        } else {
            progress = new ChatReadProgress(userId, announcementId, LocalDateTime.now());
        }

        readProgressRepository.save(progress);
        return ResponseEntity.ok(new MessageResponseDTO(true, "Chat marcado como leído"));
    }

    @Operation(
            summary = "Obtener número de mensajes no leídos",
            description = "Devuelve el total de mensajes emitidos en la sala con posterioridad a la última marca de lectura del usuario."
    )
    @ApiResponse(responseCode = "200", description = "Conteo de mensajes no leídos obtenido correctamente")
    @GetMapping("/unread")
    public ResponseEntity<UnreadCountDTO> getUnreadCount(
            @Parameter(description = "ID del anuncio/grupo de viaje", example = "1")
            @RequestParam Long announcementId,
            @Parameter(description = "ID del usuario que consulta", example = "1")
            @RequestParam Long userId) {

        Optional<ChatReadProgress> progressOpt = readProgressRepository.findByUserIdAndAnnouncementId(userId, announcementId);

        long unreadCount = progressOpt.map(chatReadProgress ->
                messageRepository.countByAnnouncement_IdAndTimestampAfter(
                        announcementId,
                        chatReadProgress.getLastReadAt()
                )
        ).orElseGet(() -> messageRepository.countByAnnouncement_Id(announcementId));

        return ResponseEntity.ok(new UnreadCountDTO(unreadCount));
    }
}