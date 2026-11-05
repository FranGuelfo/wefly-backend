package com.wefly.wefly.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wefly.wefly.model.Message;
import com.wefly.wefly.model.Announcement;
import com.wefly.wefly.repository.MessageRepository;
import com.wefly.wefly.repository.AnnouncementRepository;
import jakarta.transaction.Transactional;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.net.URI;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    // Cambiamos System.out por el Logger oficial de SLF4J
    private static final Logger log = LoggerFactory.getLogger(ChatWebSocketHandler.class);

    private final MessageRepository messageRepository;
    private final AnnouncementRepository announcementRepository;
    private final ObjectMapper objectMapper;

    // Sala de chat agrupada por AnnouncementId
    private final Map<Long, Set<WebSocketSession>> chatRooms = new ConcurrentHashMap<>();

    // Inyectamos el ObjectMapper de Spring que ya maneja JavaTimeModule automáticamente
    public ChatWebSocketHandler(MessageRepository messageRepository,
                                AnnouncementRepository announcementRepository
    ) {
        this.messageRepository = messageRepository;
        this.announcementRepository = announcementRepository;
        this.objectMapper = new com.fasterxml.jackson.databind.ObjectMapper()
                .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule())
                .disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) throws Exception {
        try {
            Long announcementId = getAnnouncementId(session);
            chatRooms.computeIfAbsent(announcementId, k -> new CopyOnWriteArraySet<>()).add(session);
            log.info("Nueva conexión WebSocket establecida en el chat del viaje ID: {}", announcementId);
        } catch (IllegalArgumentException e) {
            log.error("Error al establecer conexión WebSocket: {}", e.getMessage());
            session.close(CloseStatus.BAD_DATA);
        }
    }

    @Override
    @Transactional
    protected void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage textMessage) throws Exception {
        try {
            Long announcementId = getAnnouncementId(session);

            // Mapeamos el JSON entrante
            Message incoming = objectMapper.readValue(textMessage.getPayload(), Message.class);

            Announcement announcement = announcementRepository.findById(announcementId)
                    .orElseThrow(() -> new IllegalArgumentException("Viaje no encontrado con ID: " + announcementId));

            // Guardamos en la base de datos
            Message messageToSave = new Message(announcement, incoming.getSenderId(), incoming.getSenderName(), incoming.getContent());
            Message savedMessage = messageRepository.save(messageToSave);

            // Emitimos el mensaje guardado a toda la sala
            String jsonResponse = objectMapper.writeValueAsString(savedMessage);
            TextMessage broadcastMessage = new TextMessage(jsonResponse);

            Set<WebSocketSession> sessionsInRoom = chatRooms.get(announcementId);
            if (sessionsInRoom != null) {
                for (WebSocketSession s : sessionsInRoom) {
                    if (s.isOpen()) {
                        s.sendMessage(broadcastMessage);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error procesando el mensaje de texto en WebSocket: {}", e.getMessage(), e);
        }
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) throws Exception {
        try {
            Long announcementId = getAnnouncementId(session);
            Set<WebSocketSession> sessionsInRoom = chatRooms.get(announcementId);
            if (sessionsInRoom != null) {
                sessionsInRoom.remove(session);
            }
            log.info("Conexión WebSocket cerrada en el chat del viaje ID: {}. Estado: {}", announcementId, status);
        } catch (IllegalArgumentException e) {
            log.warn("Conexión cerrada para una sesión sin URI válida.");
        }
    }

    // Método auxiliar blindado contra NullPointerException
    private Long getAnnouncementId(WebSocketSession session) {
        URI uri = session.getUri();
        if (uri == null) {
            throw new IllegalArgumentException("La sesión no contiene una URI válida.");
        }

        String path = uri.getPath();
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("El path de la URL del WebSocket está vacío.");
        }

        try {
            return Long.parseLong(path.substring(path.lastIndexOf('/') + 1));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("No se pudo extraer un ID numérico válido del path: " + path);
        }
    }
}